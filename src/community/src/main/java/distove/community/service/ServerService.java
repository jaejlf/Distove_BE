package distove.community.service;

import distove.community.entity.Category;
import distove.community.entity.Channel;
import distove.community.entity.Member;
import distove.community.entity.Server;
import distove.community.exception.DistoveException;
import distove.community.repository.CategoryRepository;
import distove.community.repository.ChannelRepository;
import distove.community.repository.MemberRepository;
import distove.community.repository.ServerRepository;
import distove.community.web.ChatClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

import static distove.community.entity.Category.newCategory;
import static distove.community.entity.Member.newMember;
import static distove.community.entity.Server.newServer;
import static distove.community.exception.ErrorCode.SERVER_NOT_FOUND_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional

public class ServerService {

    private final ChatClient chatClient;
    private final ServerRepository serverRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final ChannelRepository channelRepository;
    private final StorageService storageService;
    private final ChannelService channelService;

    public Server createNewServer(Long userId, String name, MultipartFile image) {
        String imgUrl = null;
        if (!image.isEmpty()) {
            imgUrl = storageService.upload(image);
        }
        Server newServer = serverRepository.save(newServer(name, imgUrl));
        categoryRepository.save(newCategory(null, newServer));
        Category defaultChatCategory = categoryRepository.save(newCategory("채팅 채널", newServer));
        Category defaultVoiceCategory = categoryRepository.save(newCategory("음성 채널", newServer));
        channelService.createNewChannel(userId, "일반", defaultChatCategory.getId(), 1);
        channelService.createNewChannel(userId, "일반", defaultVoiceCategory.getId(), 2);
        memberRepository.save(newMember(newServer, userId));

        return newServer;

    }

    public Server updateServer(Long serverId, String name, String imgUrl, MultipartFile image) {
        Server server = serverRepository.findById(serverId)
                .orElseThrow(() -> new DistoveException(SERVER_NOT_FOUND_ERROR));
        if (!image.isEmpty()) {
            imgUrl = storageService.upload(image);
        }
        server.updateServer(name, imgUrl);

        return server;
    }


    public List<Server> getServersByUserId(Long userId) {

        List<Member> members = memberRepository.findMembersByUserId(userId);
        List<Server> servers = new ArrayList<>();
        for (Member m : members) {
            servers.add(m.getServer());
        }
        return servers;

    }

    public void deleteServerById(Long serverId) {
        Server server = serverRepository.findById(serverId)
                .orElseThrow(() -> new DistoveException(SERVER_NOT_FOUND_ERROR));
        List<Category> categories = categoryRepository.findCategoriesByServerId(serverId);
        for (Category category : categories) {
            for (Channel channel : channelRepository.deleteAllByCategoryId(category.getId())) {
                chatClient.clearAll(channel.getId());
            }
        }
        memberRepository.deleteAllByServerId(serverId);
        categoryRepository.deleteAllByServerId(serverId);
        serverRepository.deleteById(serverId);
    }
}
