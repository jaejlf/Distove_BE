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
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static distove.community.dto.response.CategoryResponse.newCategoryResponse;
import static distove.community.dto.response.ChannelResponse.newChannelResponse;
import static distove.community.entity.Category.newCategory;
import static distove.community.entity.Invitation.newInvitation;
import static distove.community.entity.Member.newMember;
import static distove.community.entity.Server.newServer;
import static distove.community.enumerate.DefaultRoleName.OWNER;
import static distove.community.exception.ErrorCode.*;

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
    private final MemberService memberService;

    private static final String defaultCategoryName = null;
    private static final String defaultChatCategoryName = "채팅 채널";
    private static final String defaultVoiceCategoryName = "음성 채널";
    private static final String defaultChannelName = "일반";

    //refact
    public List<CategoryResponse> getCategoriesWithChannelsByServerId(Long serverId) {
        List<Category> categories = categoryRepository.findCategoriesByServerId(serverId);
        List<Channel> channels = channelRepository.findChannelsByCategoryIn(categories);
        Map<Long, CategoryResponse> categoryHashMap = categories.stream()
                .collect(Collectors.toMap(
                        category -> category.getId(),
                        category -> newCategoryResponse(category.getId(), category.getName(), new ArrayList<>())
                ));
        for (Channel channel : channels) {
            categoryHashMap.get(channel.getCategory().getId()).getChannels().add(newChannelResponse(channel.getId(),channel.getName(), channel.getChannelTypeId()));
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
                .orElseThrow(() -> new DistoveException(SERVER_NOT_FOUND));
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
                .orElseThrow(() -> new DistoveException(ROLE_NOT_FOUND));
        memberRepository.save(newMember(newServer, userId, ownerRole));
    }

    public String createInvitation(Long memberId, Long serverId) {
        String inviteCode = UUID.randomUUID().toString().substring(0,8);
        Member member = memberRepository.findByUserIdAndServerId(memberId,serverId).orElseThrow(()-> new DistoveException(MEMBER_NOT_FOUND));
        Server server = serverRepository.findById(serverId).orElseThrow(() -> new DistoveException(SERVER_NOT_FOUND));
        Invitation invitation = newInvitation(inviteCode, server, member);
        invitationRepository.save(invitation);

        return inviteCode;
    }

    public void validateInviteCode(Long userId, String inviteCode) {
        Invitation invitation = invitationRepository.findInvitationByInviteCode(inviteCode)
                .orElseThrow(() -> new DistoveException(INVITE_CODE_NOT_FOUND));

        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(now, invitation.getExpiresAt());

        if (duration.getSeconds() < 0) {
            throw new DistoveException(INVITE_CODE_EXPIRED);
        }

        if (invitation.getUses() > 0) {
            invitation.decreaseInviteCodeUsage(invitation.getUses());
            invitationRepository.save(invitation);
        } else {

            invitationRepository.save(invitation);
            throw new DistoveException(INVITE_CODE_USES_EXCEEDED);
        }

        memberService.joinServer(userId, invitation.getServer().getId());

    }
}
