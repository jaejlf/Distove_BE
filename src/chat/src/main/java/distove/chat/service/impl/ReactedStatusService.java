package distove.chat.service.impl;

import distove.chat.dto.request.MessageRequest;
import distove.chat.entity.Message;
import distove.chat.service.MessageService;
import org.springframework.stereotype.Service;

@Service
public class ReactedStatusService implements MessageService {

    @Override
    public Message publishMessage(Long userId, Long channelId, MessageRequest request) {
        return null;
    }

}
