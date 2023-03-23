package distove.chat.service.impl;

import distove.chat.dto.request.MessageRequest;
import distove.chat.dto.response.MessageResponse;
import distove.chat.entity.Message;
import distove.chat.service.PublishService;
import org.springframework.stereotype.Service;

@Service
public class ReactedStatusService implements PublishService {

    @Override
    public Message publishMessage(Long userId, Long channelId, MessageRequest request) {
        return null;
    }

}
