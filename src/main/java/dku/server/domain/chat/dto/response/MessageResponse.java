package dku.server.domain.chat.dto.response;

import dku.server.domain.chat.domain.ConversationMessage;
import dku.server.domain.chat.domain.Role;

import java.util.UUID;

public record MessageResponse(
        UUID messageId,
        Role role,
        String content
) {
    public static MessageResponse from(ConversationMessage conversationMessage) {
        return new MessageResponse(
                conversationMessage.getId(),
                conversationMessage.getRole(),
                conversationMessage.getContent()
        );
    }
}
