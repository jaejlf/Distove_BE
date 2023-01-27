package distove.community.service;

import distove.community.dto.request.ChannelRequest;
import distove.community.dto.request.ChannelUpdateRequest;
import distove.community.dto.response.ChannelUpdateResponse;
import distove.community.entity.Category;
import distove.community.entity.Channel;
import distove.community.exception.DistoveException;
import distove.community.repository.CategoryRepository;
import distove.community.repository.ChannelRepository;
import distove.community.repository.ServerRepository;
import distove.community.web.ChatClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static distove.community.exception.ErrorCode.CHANNEL_NOT_FOUND_ERROR;
import static distove.community.exception.ErrorCode.SERVER_NOT_FOUND_ERROR;

@Service
@RequiredArgsConstructor
@Transactional

public class ChannelService {
    private final ChannelRepository channelRepository;
    private final CategoryRepository categoryRepository;
    private final ChatClient chatClient;

    public ChannelUpdateResponse updateChannelName(Long channelId, ChannelUpdateRequest channelUpdateRequest){
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new DistoveException(CHANNEL_NOT_FOUND_ERROR));
        channel.updateChannel(channelUpdateRequest.getName());
        channelRepository.save(channel);
        return new ChannelUpdateResponse(channel.getId(),channel.getName(), channel.getChannelTypeId());
    }

    public void deleteChannelById(Long channelId){
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new DistoveException(CHANNEL_NOT_FOUND_ERROR));
        channelRepository.deleteById(channelId);
        chatClient.clearAll(channelId);
    }
    public Channel createNewChannel(Long userId, ChannelRequest channelRequest){

        Category category = categoryRepository.findById(channelRequest.getCategoryId())
                .orElseThrow(() -> new DistoveException(SERVER_NOT_FOUND_ERROR));

        Channel newChannel = channelRepository.save(new Channel(
                channelRequest.getName(),
                channelRequest.getChannelTypeId(),
                category
        ));

        if(newChannel.getChannelTypeId() == 1) chatClient.createConnection(userId, newChannel.getId());
        return newChannel;
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
