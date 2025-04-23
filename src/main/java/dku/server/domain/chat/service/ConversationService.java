package dku.server.domain.chat.service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import dku.server.domain.chat.domain.Conversation;
import dku.server.domain.chat.domain.Message;
import dku.server.domain.chat.dto.request.ConversationCreateRequest;
import dku.server.domain.chat.dto.response.TitleGenerationResponse;
import dku.server.domain.chat.repository.ConversationRepository;
import dku.server.domain.chat.repository.MessageRepository;
import dku.server.global.exception.CustomException;
import dku.server.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static dku.server.domain.chat.prompt.Prompt.GENERATE_CONVERSATION_TITLE;
import static dku.server.global.config.GeminiConfig.GEMINI_SAFETY_SETTINGS;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final Client geminiClient;

    @Transactional
    public void createConversation(ConversationCreateRequest request) {
        if (conversationRepository.existsById(request.conversationId())) {
            throw new CustomException(ErrorCode.CONVERSATION_ALREADY_EXISTS);
        }
        conversationRepository.save(
                Conversation.create(request.conversationId(), null, request.content())
        );
    }

    @Transactional
    public TitleGenerationResponse generateTitle(UUID conversationId) {
        System.out.println(conversationId);
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new CustomException(ErrorCode.CONVERSATION_NOT_FOUND));

        // 대화 제목이 이미 존재하는 경우, 제목을 생성하지 않고 기존 제목을 반환합니다.
        if (conversation.getTitle() != null) {
            return TitleGenerationResponse.of(conversation.getTitle());
        }

        List<Message> messageList = messageRepository.findAllByConversation(
                conversation,
                Sort.by(Sort.Direction.ASC, "id"),
                Limit.of(2)
        );

        GenerateContentConfig config = GenerateContentConfig.builder()
                .temperature(GENERATE_CONVERSATION_TITLE.temperature())
                .maxOutputTokens(GENERATE_CONVERSATION_TITLE.maxOutputTokens())
                .safetySettings(GEMINI_SAFETY_SETTINGS)
                .build();

        GenerateContentResponse response = geminiClient.models.generateContent(
                GENERATE_CONVERSATION_TITLE.model(),
                String.format(
                        GENERATE_CONVERSATION_TITLE.template(),
                        messageList.get(0).getContent(),
                        messageList.get(1).getContent()
                ),
                config
        );

        if (response.text() == null) {
            throw new CustomException(ErrorCode.CONVERSATION_FAILED_TO_GENERATE_TITLE);
        }

        conversation.updateTitle(
                response.text().replace("```", "").replace("\n", "")
        );

        return TitleGenerationResponse.of(conversation.getTitle());
    }
}
