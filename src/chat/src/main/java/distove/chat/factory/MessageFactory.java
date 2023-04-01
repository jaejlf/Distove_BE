package distove.chat.factory;

import distove.chat.exception.DistoveException;
import distove.chat.service.MessageGenerator;
import distove.chat.service.impl.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static distove.chat.enumerate.MessageType.MessageStatus;
import static distove.chat.exception.ErrorCode.INVALID_MESSAGE_REQUEST_ERROR;

@Component
@RequiredArgsConstructor
public class MessageFactory {

    private final CreateMessageGenerator createMessageGenerator;
    private final ModifyMessageGenerator modifyMessageGenerator;
    private final DeleteMessageGenerator deleteMessageGenerator;
    private final ReactMessageGenerator reactMessageGenerator;

    public MessageGenerator getGeneratorByStatus(MessageStatus status) {
        switch (status) {
            case CREATED:
                return createMessageGenerator;
            case MODIFIED:
                return modifyMessageGenerator;
            case DELETED:
                return deleteMessageGenerator;
            case REACTED:
                return reactMessageGenerator;
            default:
                throw new DistoveException(INVALID_MESSAGE_REQUEST_ERROR);
        }
    }

}
