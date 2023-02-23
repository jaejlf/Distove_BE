package distove.community.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class InvitationException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    public InvitationException(ErrorCode errorCode) {
        this.httpStatus = errorCode.getHttpStatus();
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }
}

