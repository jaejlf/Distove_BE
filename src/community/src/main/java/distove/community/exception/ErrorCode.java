package distove.community.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    SAMPLE_ERROR(HttpStatus.NOT_FOUND, "X0001", "샘플 예외입니다."),
    SERVER_NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, "C0001", "존재하지 않는 서버입니다."),
    CHANNEL_NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, "C0002", "존재하지 않는 채널입니다."),
    CATEGORY_NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, "C0003", "존재하지 않는 카테고리입니다."),
    IMAGE_UPLOAD_FAILED(HttpStatus.EXPECTATION_FAILED, "C0004", "이미지 업로드 실패"),
    EMPTY_FILE(HttpStatus.NO_CONTENT, "C0005", "파일이 비어있습니다."),
    ROLE_NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, "C0006","존재하지 않는 역할입니다."),
    MEMBER_NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, "C0007","존재하지 않는 멤버입니다."),
    MEMBER_ALREADY_EXIST_ERROR(HttpStatus.NOT_FOUND, "C0008","이미 서버에 참여 중인 멤버입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}

