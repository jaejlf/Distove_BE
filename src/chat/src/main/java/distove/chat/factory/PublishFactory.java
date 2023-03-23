package distove.chat.factory;

import distove.chat.exception.DistoveException;
import distove.chat.service.PublishService;
import distove.chat.service.impl.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static distove.chat.enumerate.MessageType.MessageStatus;
import static distove.chat.exception.ErrorCode.MESSAGE_TYPE_ERROR;

@Component
@RequiredArgsConstructor
public class PublishFactory {

    private final CreatedStatusService createdStatusService;
    private final ModifiedStatusService modifiedStatusService;
    private final DeletedStatusService deletedStatusService;
    private final ReactedStatusService reactedStatusService;
    private final TypingStatusService typingStatusService;

    public PublishService getServiceByStatus(MessageStatus status) {
        switch (status) {
            case CREATED:
                return createdStatusService;
            case MODIFIED:
                return modifiedStatusService;
            case DELETED:
                return deletedStatusService;
            case REACTED:
                return reactedStatusService;
            case TYPING:
                return typingStatusService;
            default:
                throw new DistoveException(MESSAGE_TYPE_ERROR);
        }
    }

}
