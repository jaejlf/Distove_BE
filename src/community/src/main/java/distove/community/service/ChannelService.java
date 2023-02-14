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

import java.util.Objects;

import static distove.community.dto.response.ChannelResponse.newChannelResponse;
import static distove.community.entity.Channel.newChannel;
import static distove.community.exception.ErrorCode.CATEGORY_NOT_FOUND;
import static distove.community.exception.ErrorCode.CHANNEL_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
public class ChannelService {
    private final ChannelRepository channelRepository;
    private final CategoryRepository categoryRepository;
    private final ChatClient chatClient;

    public ChannelResponse updateChannelName(Long channelId, Long serverId, ChannelUpdateRequest channelUpdateRequest) {
        Channel channel = checkChannelExist(channelId, serverId);
        channel.updateChannel(channelUpdateRequest.getName());
        channelRepository.save(channel);
        return newChannelResponse(channel.getId(), channel.getName(), channel.getChannelTypeId());
    }

    public void deleteChannelById(Long channelId, Long serverId) {
        checkChannelExist(channelId, serverId);
        channelRepository.deleteById(channelId);
        chatClient.clearAll(channelId);
    }

    public Channel createNewChannel(Long serverId, String name, Long categoryId, Integer channelTypeId) {
        Category category = categoryRepository.findByIdAndServerId(categoryId, serverId)
                .orElseThrow(() -> new DistoveException(CATEGORY_NOT_FOUND));
        Channel newChannel = channelRepository.save(newChannel(
                name,
                channelTypeId,
                category
        ));
        if (newChannel.getChannelTypeId() == 1) chatClient.createConnection(serverId, newChannel.getId());

        return newChannel;
    }

    private Channel checkChannelExist(Long channelId, Long serverId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new DistoveException(CHANNEL_NOT_FOUND));

        if (!Objects.equals(channel.getCategory().getServer().getId(), serverId))
            throw new DistoveException(CHANNEL_NOT_FOUND);

        return channel;
    }

}
