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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static distove.community.exception.ErrorCode.SERVER_NOT_FOUND_ERROR;
@Slf4j
@Service
@RequiredArgsConstructor
public class ServerService {
    private final ServerRepository serverRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final ChannelRepository channelRepository;
    private final StorageService storageService;

    public Server createNewServer(String name, MultipartFile image) {
        String imgUrl=null;
        if(!image.isEmpty()){
            imgUrl= storageService.upload(image);
        }
        Server newServer = serverRepository.save(new Server(name,imgUrl));
        categoryRepository.save(new Category(null,newServer));
        Category defaultChatCategory = categoryRepository.save(new Category("채팅 채널",newServer));
        Category defaultVoiceCategory = categoryRepository.save(new Category("음성 채널",newServer));
        channelRepository.save(new Channel("일반",1,defaultChatCategory));
        channelRepository.save(new Channel("일반",2,defaultVoiceCategory));

        return serverRepository.findById(newServer.getId())
                .orElseThrow(() -> new DistoveException(SERVER_NOT_FOUND_ERROR));

    }

    public List<Server> getServersByUserId(Long userId){

        List<Member> members = memberRepository.findMembersByUserId(userId);
        List<Server> servers = new ArrayList<>();
        for(Member m : members){
            servers.add(m.getServer());
        }
        return servers;

    }
}
