package distove.chat.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // common
    JWT_INVALID_ERROR(HttpStatus.FORBIDDEN, "A0004", "토큰이 유효하지 않습니다."),
    JWT_EXPIRED_ERROR(HttpStatus.UNAUTHORIZED, "A0005", "토큰이 만료되었습니다."),
    EVENT_HANDLE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "X0001", "이벤트 처리에 실패했습니다."),
    FILE_UPLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "X0002", "파일 업로드에 실패했습니다."),

    // chat
    MESSAGE_NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, "S0001", "존재하지 않는 메시지입니다."),
    INVALID_MESSAGE_REQUEST_ERROR(HttpStatus.BAD_REQUEST, "S0002", "잘못된 메시지 타입/상태가 잘못되었습니다."),
    NO_AUTH_ERROR(HttpStatus.BAD_REQUEST, "S0003", "수정/삭제 권한이 없습니다."),
    CHANNEL_NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, "S0004", "존재하지 않는 채널입니다."),
    MEMBER_NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, "S0005", "채널에 속하지 않은 멤버입니다."),
    INVALID_SCROLL_ERROR(HttpStatus.BAD_REQUEST, "S0006", "스크롤 값은 0 또는 1이어야 합니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}
