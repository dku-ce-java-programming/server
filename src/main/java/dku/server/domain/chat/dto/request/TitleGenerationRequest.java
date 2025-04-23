package dku.server.domain.chat.dto.request;

import java.util.UUID;

public record TitleGenerationRequest(
        UUID conversationId
) {
}
