package distove.community.service;


import distove.community.dto.response.CategoryResponse;
import distove.community.entity.*;
import distove.community.enumerate.ChannelType;
import distove.community.exception.DistoveException;
import distove.community.repository.*;
import distove.community.web.ChatClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static distove.community.dto.response.CategoryResponse.newCategoryResponse;
import static distove.community.entity.Category.newCategory;
import static distove.community.entity.Member.newMember;
import static distove.community.entity.Server.newServer;
import static distove.community.enumerate.DefaultRoleName.OWNER;
import static distove.community.exception.ErrorCode.ROLE_NOT_FOUND_ERROR;
import static distove.community.exception.ErrorCode.SERVER_NOT_FOUND_ERROR;

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
    private final StorageService storageService;
    private final ChannelService channelService;

    private static final String defaultCategoryName = null;
    private static final String defaultChatCategoryName = "채팅 채널";
    private static final String defaultVoiceCategoryName = "음성 채널";
    private static final String defaultChannelName = "일반";

    //refact
    public List<CategoryResponse> getCategoriesWithChannelsByServerId(Long serverId) {
        List<Category> categories = categoryRepository.findCategoriesByServerId(serverId);
        List<Channel.Info> channels = channelRepository.findChannelsInfoByCategoryIn(categories);
        Map<Long, CategoryResponse> categoryHashMap = categories.stream()
                .collect(Collectors.toMap(
                        category -> category.getId(),
                        category -> newCategoryResponse(category.getId(), category.getName(), new ArrayList<>())
                ));
        for (Channel.Info channel : channels) {
            categoryHashMap.get(channel.getId()).getChannels().add(channel);
        }
        return new ArrayList<>(categoryHashMap.values());
    }

    @Transactional
    public Server createNewServer(Long userId, String name, MultipartFile image) {
        String imgUrl = null;
        if (!image.isEmpty()) {
            imgUrl = storageService.upload(image);
        }
        Server newServer = serverRepository.save(newServer(name, imgUrl));
        categoryRepository.save(newCategory(defaultCategoryName, newServer));

        Category defaultChatCategory = categoryRepository.save(newCategory(defaultChatCategoryName, newServer));
        Category defaultVoiceCategory = categoryRepository.save(newCategory(defaultVoiceCategoryName, newServer));
        channelService.createNewChannel(userId, newServer.getId(), defaultChannelName, defaultChatCategory.getId(), ChannelType.CHAT.getCode());
        channelService.createNewChannel(userId, newServer.getId(), defaultChannelName, defaultVoiceCategory.getId(), ChannelType.VOICE.getCode());

        setOwnerAndRole(userId, newServer);
        return newServer;
    }

    @Transactional
    public Server updateServer(Long serverId, String name, String imgUrl, MultipartFile image) {
        Server server = serverRepository.findById(serverId)
                .orElseThrow(() -> new DistoveException(SERVER_NOT_FOUND_ERROR));
        if (!image.isEmpty()) {
            imgUrl = storageService.upload(image);
        }
        server.updateServer(name, imgUrl);

        return server;
    }

    //
    public List<Server> getServersByUserId(Long userId) {

        List<Member> members = memberRepository.findMembersByUserId(userId);
        List<Server> servers = members.stream().map(member -> member.getServer()).collect(Collectors.toList());
        return servers;

    }

    @Transactional
    public void deleteServerById(Long serverId) {
//        Server server = serverRepository.findById(serverId)
//                .orElseThrow(() -> new DistoveException(SERVER_NOT_FOUND_ERROR));
        List<Category> categories = categoryRepository.findCategoriesByServerId(serverId);
        List<Channel> channels = channelRepository.findChannelsByCategoryInAndChannelTypeIdEquals(categories,ChannelType.CHAT.getCode());
//        chatClient.clearAllByList(channels);
        channelRepository.deleteAllByCategoryIn(categories);
        memberRepository.deleteAllByServerId(serverId);
        categoryRepository.deleteAllByServerId(serverId);
        serverRepository.deleteById(serverId);
    }


    private void setOwnerAndRole(Long userId, Server newServer) {
        memberRoleRepository.saveAll(MemberRole.createDefaultRoles(newServer));
        MemberRole ownerRole = memberRoleRepository.findByRoleNameAndServerId(OWNER.getName(), newServer.getId())
                .orElseThrow(() -> new DistoveException(ROLE_NOT_FOUND_ERROR));
        memberRepository.save(newMember(newServer, userId, ownerRole));
    }

}
