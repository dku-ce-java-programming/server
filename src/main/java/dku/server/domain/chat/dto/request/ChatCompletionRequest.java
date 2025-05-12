package dku.server.domain.chat.dto.request;

import javax.validation.constraints.NotNull;
import java.util.UUID;

public record ChatCompletionRequest(
        @NotNull
        UUID conversationId,
        @NotNull
        String content
) {
}
