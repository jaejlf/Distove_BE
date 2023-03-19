package distove.community.service;


import distove.community.client.ChatClient;
import distove.community.dto.response.CategoryResponse;
import distove.community.entity.*;
import distove.community.enumerate.ChannelType;
import distove.community.exception.DistoveException;
import distove.community.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static distove.community.dto.response.ChannelResponse.of;
import static distove.community.enumerate.DefaultRoleName.OWNER;
import static distove.community.exception.ErrorCode.ROLE_NOT_FOUND;
import static distove.community.exception.ErrorCode.SERVER_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServerService {

    private final ChatClient chatClient;
    private final ServerRepository serverRepository;
    private final MemberRepository memberRepository;
    private final MemberRoleRepository memberRoleRepository;
    private final CategoryRepository categoryRepository;
    private final ChannelRepository channelRepository;
    private final InvitationRepository invitationRepository;
    private final StorageService storageService;
    private final ChannelService channelService;


    private static final String defaultCategoryName = null;
    private static final String defaultChatCategoryName = "채팅 채널";
    private static final String defaultVoiceCategoryName = "음성 채널";
    private static final String defaultChannelName = "일반";

    public List<CategoryResponse> getCategoriesWithChannelsByServerId(Long serverId) {
        List<Category> categories = categoryRepository.findCategoriesByServerId(serverId);
        List<Channel> channels = channelRepository.findChannelsByCategoryIn(categories);
        Map<Long, CategoryResponse> categoryHashMap = categories.stream()
                .collect(Collectors.toMap(
                        Category::getId,
                        category -> CategoryResponse.of(category.getId(), category.getName(), new ArrayList<>())));
        for (Channel channel : channels) {
            categoryHashMap.get(channel.getCategory().getId()).getChannels().add(of(channel.getId(), channel.getName(), channel.getChannelTypeId()));
        }
        return new ArrayList<>(categoryHashMap.values());
    }

    @Transactional
    public Server createNewServer(Long userId, String name, MultipartFile image) {
        String imgUrl = null;
        if (!image.isEmpty()) {
            imgUrl = storageService.upload(image);
        }
        Server newServer = serverRepository.save(new Server(name, imgUrl));
        categoryRepository.save(new Category(defaultCategoryName, newServer));

        Category defaultChatCategory = categoryRepository.save(new Category(defaultChatCategoryName, newServer));
        Category defaultVoiceCategory = categoryRepository.save(new Category(defaultVoiceCategoryName, newServer));
        channelService.createNewChannel(newServer.getId(), defaultChannelName, defaultChatCategory.getId(), ChannelType.CHAT.getCode());
        channelService.createNewChannel(newServer.getId(), defaultChannelName, defaultVoiceCategory.getId(), ChannelType.VOICE.getCode());

        setOwnerAndRole(userId, newServer);
        return newServer;
    }

    @Transactional
    public Server updateServer(Long serverId, String name, String imgUrl, MultipartFile image) {
        Server server = serverRepository.findById(serverId)
                .orElseThrow(() -> new DistoveException(SERVER_NOT_FOUND));
        if (!image.isEmpty()) {
            imgUrl = storageService.upload(image);
        }
        server.updateServer(name, imgUrl);

        return server;
    }

    public List<Server> getServersByUserId(Long userId) {
        List<Member> members = memberRepository.findMembersByUserId(userId);
        return members.stream().map(Member::getServer).collect(Collectors.toList());

    }

    public List<Long> getServerIdsByUserId(Long userId) {
        List<Member> members = memberRepository.findMembersByUserId(userId);
        return members.stream().map(member -> member.getServer().getId()).collect(Collectors.toList());
    }

    @Transactional
    public void deleteServerById(Long serverId) {
        Server server = serverRepository.findById(serverId).orElseThrow(() -> new DistoveException(SERVER_NOT_FOUND));
        List<Category> categories = categoryRepository.findCategoriesByServerId(serverId);
        List<Channel> channels = channelRepository.findChannelsByCategoryInAndChannelTypeIdEquals(categories, ChannelType.CHAT.getCode());
        String channelIds = channels.stream().map(channel -> channel.getId()).collect(Collectors.toList()).toString().replace("[", "").replace("]", "");
        chatClient.clearAll(channelIds);
        channelRepository.deleteAllByCategoryIn(categories);
        memberRepository.deleteAllByServerId(serverId);
        memberRoleRepository.deleteMemberRolesByServer(server);
        invitationRepository.deleteInvitationsByServer(server);
        categoryRepository.deleteAllByServerId(serverId);
        serverRepository.deleteById(serverId);
    }

    private void setOwnerAndRole(Long userId, Server newServer) {
        memberRoleRepository.saveAll(MemberRole.createDefaultRoles(newServer));
        MemberRole ownerRole = memberRoleRepository.findByRoleNameAndServerId(OWNER.getName(), newServer.getId())
                .orElseThrow(() -> new DistoveException(ROLE_NOT_FOUND));
        memberRepository.save(new Member(newServer, userId, ownerRole));
    }

}
