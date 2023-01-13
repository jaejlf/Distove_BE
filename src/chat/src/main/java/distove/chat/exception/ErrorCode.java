package distove.chat.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    SAMPLE_ERROR(HttpStatus.NOT_FOUND, "X0001", "샘플 예외입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}

