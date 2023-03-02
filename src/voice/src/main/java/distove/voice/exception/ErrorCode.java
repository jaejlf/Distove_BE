package distove.voice.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    PARTICIPANT_NOT_FOUND(HttpStatus.BAD_REQUEST, "V0001", "방에 존재하지 않는 유저입니다."),
    ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "V0002", "방이 존재하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}

