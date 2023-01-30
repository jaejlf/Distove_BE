package distove.voice.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    SAMPLE_ERROR(HttpStatus.NOT_FOUND, "X0001", "샘플 예외입니다."),
    CHANNEL_NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, "V0002", "채널 없음"),
    PARTICIPANT_NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, "V0003", "방에 있지 않은 유저"),
    ROOM_NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, "V0004", "방 없음");
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}

