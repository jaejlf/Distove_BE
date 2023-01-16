package distove.chat.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    MESSAGE_NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, "S0001", "존재하지 않는 메시지입니다."),
    MESSAGE_TYPE_ERROR(HttpStatus.BAD_REQUEST, "S0002", "잘못된 메시지 타입입니다."),
    NO_AUTH_ERROR(HttpStatus.BAD_REQUEST, "S0003", "수정/삭제 권한이 없습니다."),
    FILE_EXTENTION_ERROR(HttpStatus.BAD_REQUEST, "S0004", "잘못된 파일 확장자입니다."),
    FILE_UPLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S0005", "파일 업로드에 실패했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}

