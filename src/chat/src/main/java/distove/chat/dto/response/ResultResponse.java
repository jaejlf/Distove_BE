package distove.chat.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mongodb.lang.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.validation.constraints.Null;

import static org.springframework.http.HttpStatus.*;

@Data
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResultResponse<T> {

    private int statusCode;
    private String errorCode;
    private String message;
    private T data;

    public static <T> ResponseEntity<Object> success(HttpStatus httpStatus, String message, T data) {
        return ResponseEntity
                .status(httpStatus)
                .body(ResultResponse.builder()
                        .statusCode(httpStatus.value())
                        .message(message)
                        .data(data)
                        .build());
    }
    public static <T> ResponseEntity<Object> fail(HttpStatus httpStatus, String errorCode, String message) {
        return ResponseEntity
                .status(httpStatus)
                .body(ResultResponse.builder()
                        .statusCode(httpStatus.value())
                        .errorCode(errorCode)
                        .message(message)
                        .build());
    }

}
