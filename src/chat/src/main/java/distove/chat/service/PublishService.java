package distove.chat.service;

import distove.chat.dto.request.MessageRequest;
import distove.chat.entity.Message;

public interface PublishService {

    Message publishMessage(Long userId, Long channelId, MessageRequest request);

}
