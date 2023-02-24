package distove.community.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    JWT_INVALID(HttpStatus.FORBIDDEN, "A0004", "토큰이 유효하지 않습니다."),
    JWT_EXPIRED(HttpStatus.UNAUTHORIZED, "A0005", "토큰이 만료되었습니다."),

    SERVER_NOT_FOUND(HttpStatus.NOT_FOUND, "C0001", "존재하지 않는 서버입니다."),
    CHANNEL_NOT_FOUND(HttpStatus.NOT_FOUND, "C0002", "존재하지 않는 채널입니다."),
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "C0003", "존재하지 않는 카테고리입니다."),
    IMAGE_UPLOAD_FAILED(HttpStatus.EXPECTATION_FAILED, "C0004", "이미지 업로드에 실패했습니다."),
    EMPTY_FILE(HttpStatus.NO_CONTENT, "C0005", "파일이 비어있습니다."),
    ROLE_NOT_FOUND(HttpStatus.NOT_FOUND, "C0006","존재하지 않는 역할입니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "C0007","존재하지 않는 멤버입니다."),
    MEMBER_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "C0008","이미 서버에 참여 중인 멤버입니다."),
    NO_AUTH(HttpStatus.BAD_REQUEST, "C0009","수정 권한이 없습니다."),
    CANNOT_CHANGE_ROLE(HttpStatus.BAD_REQUEST, "C0010","'소유자'가 유일하여 변경할 수 없습니다."),
    INVITE_CODE_NOT_FOUND(HttpStatus.NOT_FOUND, "C0011", "존재하지 않는 초대코드 입니다."),
    INVITE_CODE_EXPIRED(HttpStatus.BAD_REQUEST, "C0012", "초대코드가 만료 되었습니다."),
    INVITE_CODE_USAGE_EXCEEDED(HttpStatus.BAD_REQUEST, "C0013", "초대코드의 사용횟수가 초과되었습니다."),
    NO_ROLE_DELETE_INVITE_CODE(HttpStatus.BAD_REQUEST, "C0014", "초대코드 삭제 권한이 존재하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}

