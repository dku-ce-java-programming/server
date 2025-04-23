package dku.server.domain.chat.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ConversationCreateRequest(
        @NotNull UUID conversationId,
        @NotBlank String content
) {
}
