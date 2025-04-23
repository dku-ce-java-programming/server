package dku.server.global.exception;

public record FieldErrorDetail(String field, String rejectedValue, String message) {
    public static FieldErrorDetail of(String field, Object rejectedValue, String message) {
        return new FieldErrorDetail(field, rejectedValue != null ? rejectedValue.toString() : null, message);
    }
}
