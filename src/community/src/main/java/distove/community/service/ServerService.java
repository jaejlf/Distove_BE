package distove.community.service;

import distove.community.dto.response.ChannelDto;
import distove.community.dto.response.ServerDto;
import distove.community.entity.*;
import distove.community.exception.DistoveException;
import distove.community.repository.CategoryRepository;
import distove.community.repository.ChannelRepository;
import distove.community.repository.MemberRepository;
import distove.community.repository.ServerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
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

    public List<ServerDto> getServersByUserId(Long userId){

        List<Member> members = memberRepository.findMembersByUserId(userId);
        List<ServerDto> servers = new ArrayList<>();
        for(Member m : members){
            List<Category.CategoryIdAndName> categories = categoryRepository.findCategoriesByServerId(m.getServer().getId());
            List<Channel.ChannelNameAndChannelTypeId> channelsWithCategory = channelRepository.findAllByServerId(m.getServer().getId());
//            List<Channel> channels = channelRepository.findAllByServerId(m.getServer().getId());
            servers.add(new ServerDto(m.getServer().getId(),m.getServer().getName(),categories,channelsWithCategory));
        }
        return servers;

    }
}
