package dku.server.domain.chat.dto.response;

public record TitleGenerationResponse(
        String title
) {
    public static TitleGenerationResponse of(String title) {
        return new TitleGenerationResponse(title);
    }
}
