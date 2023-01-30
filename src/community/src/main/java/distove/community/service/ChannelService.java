package distove.community.service;

import distove.community.dto.request.ChannelUpdateRequest;
import distove.community.dto.response.ChannelResponse;
import distove.community.entity.Category;
import distove.community.entity.Channel;
import distove.community.exception.DistoveException;
import distove.community.repository.CategoryRepository;
import distove.community.repository.ChannelRepository;
import distove.community.web.ChatClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static distove.community.dto.response.ChannelResponse.newChannelResponse;
import static distove.community.entity.Channel.newChannel;
import static distove.community.exception.ErrorCode.CATEGORY_NOT_FOUND_ERROR;
import static distove.community.exception.ErrorCode.CHANNEL_NOT_FOUND_ERROR;

@Service
@RequiredArgsConstructor
@Transactional
public class ChannelService {
    private final ChannelRepository channelRepository;
    private final CategoryRepository categoryRepository;
    private final ChatClient chatClient;

    public ChannelResponse updateChannelName(Long channelId, ChannelUpdateRequest channelUpdateRequest) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new DistoveException(CHANNEL_NOT_FOUND_ERROR));
        channel.updateChannel(channelUpdateRequest.getName());
        channelRepository.save(channel);
        return newChannelResponse(channel.getId(), channel.getName(), channel.getChannelTypeId());
    }

    public void deleteChannelById(Long channelId) {
        channelRepository.findById(channelId)
                .orElseThrow(() -> new DistoveException(CHANNEL_NOT_FOUND_ERROR));
        channelRepository.deleteById(channelId);
        chatClient.clearAll(channelId);
    }

    public Channel createNewChannel(Long userId, String name, Long categoryId, Integer channelTypeId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new DistoveException(CATEGORY_NOT_FOUND_ERROR));
        Channel newChannel = channelRepository.save(newChannel(
                name,
                channelTypeId,
                category
        ));
        if (newChannel.getChannelTypeId() == 1) chatClient.createConnection(userId, newChannel.getId());
        chatClient.createConnection(userId, newChannel.getId());

        return newChannel;
    }

}
