package dku.server.domain.chat.service;

import dku.server.domain.chat.domain.Conversation;
import dku.server.domain.chat.domain.ConversationMessage;
import dku.server.domain.chat.repository.ConversationRepository;
import dku.server.global.exception.CustomException;
import dku.server.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConversationPersistenceService {

    private final ConversationRepository conversationRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveConversationMessage(UUID conversationId, String question, String answer) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new CustomException(ErrorCode.CONVERSATION_NOT_FOUND));

        conversation.addMessage(ConversationMessage.createUserMessage(question));
        conversation.addMessage(ConversationMessage.createAssistantMessage(answer));

        conversationRepository.save(conversation);
    }
}
