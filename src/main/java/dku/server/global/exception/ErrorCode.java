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
    CONVERSATION_NOT_FOUND(HttpStatus.NOT_FOUND, "대화를 찾을 수 없습니다"),
    CONVERSATION_TITLE_ALREADY_EXISTS(HttpStatus.CONFLICT, "대화 제목이 이미 존재합니다"),
    CHAT_TITLE_GENERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "대화 제목 생성에 실패했습니다"),
    CHAT_TOOL_CALLING_CLASSIFY_QUESTION_TYPE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "질문 유형 분류 도구 호출에 실패했습니다"),
    CHAT_TOOL_CALLING_EXTRACT_SCHOOL_NAME_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "학교 이름 추출 도구 호출에 실패했습니다"),
    CHAT_TOOL_CALLING_EXTRACT_COLUMNS_MAPPING_FAILED(HttpStatus.INTERNAL_SERVER_ERROR,
            "열 추출 도구 호출중 문자열의 List 변환에 실패했습니다"),
    CHAT_TOOL_CALLING_EXTRACT_COLUMNS_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "열 추출 도구 호출에 실패했습니다"),
    CHAT_TOOL_CALLING_RETRIEVE_CONTEXT_MAPPING_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "추출한 맥락 데이터의 JSON 변환에 실패했습니다"),
    INVALID_MESSAGE_ROLE(HttpStatus.INTERNAL_SERVER_ERROR, "유효하지 않은 메시지 역할입니다"),

    // Community
    ARTICLE_NOT_FOUND(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다"),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다"),

    ;

    private final HttpStatus httpStatus;
    private final String message;
}
