package distove.chat.service;

import distove.chat.dto.request.MessageRequest;
import distove.chat.entity.Message;

public interface MessageGenerator {

    Message createMessage(Long userId, Long channelId, MessageRequest request);

}
