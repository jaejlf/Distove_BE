package distove.chat.factory;

import distove.chat.exception.DistoveException;
import distove.chat.service.MessageGenerator;
import distove.chat.service.impl.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static distove.chat.enumerate.MessageType.MessageStatus;
import static distove.chat.exception.ErrorCode.MESSAGE_TYPE_ERROR;

@Component
@RequiredArgsConstructor
public class MessageFactory {

    private final CreateMessageGenerator messageCreateService;
    private final ModifyMessageGenerator messageModifyService;
    private final DeleteMessageGenerator deletedStatusService;
    private final ReactMessageGenerator messageReactService;

    public MessageGenerator getServiceByStatus(MessageStatus status) {
        switch (status) {
            case CREATED:
                return messageCreateService;
            case MODIFIED:
                return messageModifyService;
            case DELETED:
                return deletedStatusService;
            case REACTED:
                return messageReactService;
            default:
                throw new DistoveException(MESSAGE_TYPE_ERROR);
        }
    }

}
