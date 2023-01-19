package distove.auth.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    SAMPLE_ERROR(HttpStatus.NOT_FOUND, "X0001", "샘플 예외입니다."),

    DUPLICATE_RESOURCE(HttpStatus.CONFLICT, "A0001", "데이터가 이미 존재합니다."),

    EMAIL_OR_PASSWORD_ERROR(HttpStatus.BAD_REQUEST, "A0002", "계정이 존재하지 않거나 아이디 또는 비밀번호가 맞지 않습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}

