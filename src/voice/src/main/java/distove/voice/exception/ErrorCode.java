package distove.voice.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    PARTICIPANT_NOT_FOUND_ERROR(HttpStatus.BAD_REQUEST, "V0001", "존재하지 않는 참가자입니다."),
    ROOM_NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, "V0002", "존재하지 않는 화상 채팅 방입니다."),
    INVALID_MESSAGE_TYPE(HttpStatus.BAD_REQUEST, "V0003", "잘못된 요청 메시지 타입입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}

