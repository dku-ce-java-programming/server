package dku.server.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // General
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증되지 않은 사용자입니다"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다"),

    // GlobalExceptionHandler
    INVALID_JSON(HttpStatus.BAD_REQUEST, "유효하지 않은 JSON 형식입니다"),
    INVALID_ENUM(HttpStatus.BAD_REQUEST, "유효하지 않은 Enum 값입니다"),
    DATA_INTEGRITY_VIOLATION(HttpStatus.CONFLICT, "데이터 무결성 위반입니다"),

    // Member
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다"),

    // Chat
    CONVERSATION_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 대화입니다"),

    ;

    private final HttpStatus httpStatus;
    private final String message;
}
