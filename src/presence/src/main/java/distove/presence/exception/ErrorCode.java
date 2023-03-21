package distove.presence.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    JWT_INVALID(HttpStatus.FORBIDDEN, "A0004", "토큰이 유효하지 않습니다."),
    JWT_EXPIRED(HttpStatus.UNAUTHORIZED, "A0005", "토큰이 만료되었습니다."),
    EVENT_HANDLE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "X0000", "이벤트 처리에 실패했습니다."),

    PRESENCE_TYPE_ERROR(HttpStatus.BAD_REQUEST, "P0001", "존재하지 않는 활동상태 입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}

