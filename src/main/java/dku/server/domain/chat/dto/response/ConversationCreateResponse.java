package dku.server.domain.chat.dto.response;

import dku.server.domain.chat.domain.Conversation;

import java.util.UUID;

public record ConversationCreateResponse(
        UUID conversationId
) {
    public static ConversationCreateResponse from(Conversation conversation) {
        return new ConversationCreateResponse(conversation.getId());
    }
}
