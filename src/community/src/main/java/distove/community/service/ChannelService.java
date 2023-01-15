package distove.community.service;

import distove.community.dto.request.ChannelRequest;
import distove.community.entity.*;
import distove.community.exception.DistoveException;
import distove.community.repository.CategoryChannelRepository;
import distove.community.repository.CategoryRepository;
import distove.community.repository.ChannelRepository;
import distove.community.repository.ServerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static distove.community.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class ChannelService {
    private final ChannelRepository channelRepository;
    private final ServerRepository serverRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryChannelRepository categoryChannelRepository;




    public void postNewChannel(ChannelRequest channelRequest){

        Server server = serverRepository.findById(channelRequest.getServerId())
                .orElseThrow(() -> new DistoveException(SERVER_NOT_FOUND_ERROR));

        Channel newChannel = new Channel(
                channelRequest.getName(),
                channelRequest.getChannelTypeId(),
                serverRepository.findById(channelRequest.getServerId()).get()
        );
        CategoryChannel newCategoryChannel = new CategoryChannel(
                categoryRepository.findById(channelRequest.getCategoryId()).get(),
                newChannel
        );
        channelRepository.save(newChannel);
        categoryChannelRepository.save(newCategoryChannel);



    }

//    public List<Channel> getChannelsByServerId(Long serverId){
//        Server server = serverRepository.findById(serverId)
//                .orElseThrow(()-> new DistoveException((SERVER_NOT_FOUND_ERROR)));
//
//        List<Category> categorys = categoryRepository.findAllByServerId(serverId);
//
//
//    }

}
