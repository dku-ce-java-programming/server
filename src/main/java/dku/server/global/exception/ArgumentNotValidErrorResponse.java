package dku.server.global.exception;

import java.util.List;

public record ArgumentNotValidErrorResponse(String name, List<FieldErrorDetail> fieldErrors) {

    public static ArgumentNotValidErrorResponse of(ErrorCode errorCode, List<FieldErrorDetail> fieldErrors) {
        return new ArgumentNotValidErrorResponse(errorCode.name(), fieldErrors);
    }
}
