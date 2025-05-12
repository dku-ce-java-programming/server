package dku.server.domain.chat.dto.response;

import dku.server.domain.chat.domain.Conversation;

import java.util.UUID;

public record ConversationResponse(
        UUID conversationId,
        String title
) {
    public static ConversationResponse from(Conversation conversation) {
        return new ConversationResponse(
                conversation.getId(),
                conversation.getTitle()
        );
    }
}
