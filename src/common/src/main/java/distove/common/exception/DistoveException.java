package distove.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class DistoveException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    public DistoveException(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }

}
