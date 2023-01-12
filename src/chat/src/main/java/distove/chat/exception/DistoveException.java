package distove.chat.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class DistoveException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String message;

    public DistoveException(ErrorCodeEnum errorCodeEnum) {
        this.httpStatus = errorCodeEnum.getHttpStatus();
        this.errorCode = errorCodeEnum.getErrorCode();
        this.message = errorCodeEnum.getMessage();
    }

}
