package distove.presence.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    SAMPLE_ERROR(HttpStatus.NOT_FOUND, "X0001", "샘플 예외입니다."),
    EVENT_HANDLE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "P0001", "이벤트 처리에 실패했습니다."),
    SERVICE_INFO_TYPE_ERROR(HttpStatus.BAD_REQUEST, "P0002", "존재하지 않는 ServiceInfo type 입니다."),
    JWT_INVALID(HttpStatus.FORBIDDEN, "A0004", "토큰이 유효하지 않습니다."),
    JWT_EXPIRED(HttpStatus.UNAUTHORIZED, "A0005", "토큰이 만료되었습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}

