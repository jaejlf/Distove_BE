package distove.chat.enumerate;

import distove.chat.exception.DistoveException;
import distove.chat.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

import static distove.chat.enumerate.MessageType.MessageStatus.*;

@Getter
@AllArgsConstructor
public enum MessageType {

    WELCOME(List.of(CREATED)),
    IMAGE(Arrays.asList(CREATED, DELETED)),
    FILE(Arrays.asList(CREATED, DELETED)),
    VIDEO(Arrays.asList(CREATED, DELETED)),
    TYPING(List.of(CREATED)),
    TEXT(Arrays.asList(CREATED, MODIFIED, DELETED));

    private final List<MessageStatus> status;

    public enum MessageStatus {
        CREATED, MODIFIED, DELETED;
    }

    public static void checkStatusCanChanged(MessageType type, MessageStatus status) {
        if (!type.getStatus().contains(status)) throw new DistoveException(ErrorCode.MESSAGE_TYPE_ERROR);
    }

    public static boolean isFileType(MessageType type) {
        return type == IMAGE || type == FILE || type == VIDEO;
    }

}