package distove.community.service;

import distove.community.dto.request.ChannelRequest;
import distove.community.dto.request.ChannelUpdateRequest;
import distove.community.entity.Category;
import distove.community.entity.Channel;
import distove.community.exception.DistoveException;
import distove.community.repository.CategoryRepository;
import distove.community.repository.ChannelRepository;
import distove.community.repository.ServerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static distove.community.exception.ErrorCode.CHANNEL_NOT_FOUND_ERROR;
import static distove.community.exception.ErrorCode.SERVER_NOT_FOUND_ERROR;

@Service
@RequiredArgsConstructor
public class ChannelService {
    private final ChannelRepository channelRepository;
    private final ServerRepository serverRepository;
    private final CategoryRepository categoryRepository;

    public Channel updateChannelName(Long channelId, ChannelUpdateRequest channelUpdateRequest){
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new DistoveException(CHANNEL_NOT_FOUND_ERROR));
        return channel.updateChannel(channelUpdateRequest.getName());
    }

    public void deleteChannelById(Long channelId){
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new DistoveException(CHANNEL_NOT_FOUND_ERROR));
        channelRepository.deleteById(channelId);
    }
    public Channel createNewChannel(ChannelRequest channelRequest){

        Category category = categoryRepository.findById(channelRequest.getCategoryId())
                .orElseThrow(() -> new DistoveException(SERVER_NOT_FOUND_ERROR));

        Channel newChannel = new Channel(
                channelRequest.getName(),
                channelRequest.getChannelTypeId(),
                category
        );
        return channelRepository.save(newChannel);
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
