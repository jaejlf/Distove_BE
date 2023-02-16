package distove.presence.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    SAMPLE_ERROR(HttpStatus.NOT_FOUND, "X0001", "샘플 예외입니다."),
    EVENT_HANDLE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "P0000", "이벤트 처리에 실패했습니다.");


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}

