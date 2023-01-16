package distove.community.service;

import distove.community.dto.request.ServerRequest;
import distove.community.dto.response.ServerDto;
import distove.community.dto.response.ServerResponse;
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

    public Server createNewServer(ServerRequest serverRequest){
        log.debug("안녕",serverRequest);
        Server newServer = serverRepository.save(new Server(serverRequest.getName()));
        categoryRepository.save(new Category(null,newServer));
        Category defaultChatCategory = categoryRepository.save(new Category("채팅 채널",newServer));
        Category defaultVoiceCategory = categoryRepository.save(new Category("음성 채널",newServer));
        channelRepository.save(new Channel("일반",1,defaultChatCategory));
        channelRepository.save(new Channel("일반",2,defaultVoiceCategory));

        return serverRepository.findById(newServer.getId())
                .orElseThrow(() -> new DistoveException(SERVER_NOT_FOUND_ERROR));

    }
    public ServerResponse getServerById(Long id){
        Server server = serverRepository.findById(id).orElseThrow(() -> new DistoveException(SERVER_NOT_FOUND_ERROR));
        return new ServerResponse(server.getId(),server.getName(),categoryRepository.findCategoriesByServerId(server.getId()));

    }
    public List<ServerDto> getServersByUserId(Long userId){

        List<Member> members = memberRepository.findMembersByUserId(userId);
        List<ServerDto> servers = new ArrayList<>();
        for(Member m : members){
//            List<CategoryRepository.findCategoriesByServerId(m.getServer().getId());
//            List<Channel.ChannelNameAndChannelTypeIdtegory.CategoryIdAndName> categories = c> channelsWithCategory = channelRepository.findAllByServerId(m.getServer().getId());
////            List<Channel> channels = channelRepository.findAllByServerId(m.getServer().getId());
//            servers.add(new ServerDto(m.getServer().getId(),m.getServer().getName(),categories,channelsWithCategory));
        }
        return servers;

    }
}
