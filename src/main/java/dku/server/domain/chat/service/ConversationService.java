package dku.server.domain.chat.service;

import dku.server.domain.chat.domain.Conversation;
import dku.server.domain.chat.dto.request.ConversationCreateRequest;
import dku.server.domain.chat.repository.ConversationRepository;
import dku.server.global.exception.CustomException;
import dku.server.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ConversationService {

    private final ConversationRepository conversationRepository;

    @Transactional
    public void createConversation(ConversationCreateRequest request) {
        if (conversationRepository.existsById(request.conversationId())) {
            throw new CustomException(ErrorCode.CONVERSATION_ALREADY_EXISTS);
        }
        conversationRepository.save(
                Conversation.create(request.conversationId(), null, request.content()));
    }
}
