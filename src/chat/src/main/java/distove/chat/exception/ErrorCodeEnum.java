package distove.chat.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCodeEnum {

    SAMPLE_ERROR(HttpStatus.NOT_FOUND, "X0001", "샘플 예외입니다.");

    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String message;

}
