package distove.auth.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 회원가입
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "A0001", "이미 존재하는 이메일입니다."),

    // 로그인
    ACCOUNT_NOT_FOUND(HttpStatus.UNAUTHORIZED, "A0002", "계정이 존재하지 않습니다."),
    PASSWORD_ERROR(HttpStatus.UNAUTHORIZED, "A0003","패스워드가 다릅니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}

