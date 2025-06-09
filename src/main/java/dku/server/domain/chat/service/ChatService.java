package dku.server.domain.chat.service;

import dku.server.domain.chat.domain.Conversation;
import dku.server.domain.chat.domain.Role;
import dku.server.domain.chat.dto.request.ChatCompletionRequest;
import dku.server.domain.chat.dto.response.ConversationCreateResponse;
import dku.server.domain.chat.dto.response.ConversationResponse;
import dku.server.domain.chat.dto.response.MessageResponse;
import dku.server.domain.chat.prompt.Template;
import dku.server.domain.chat.repository.ConversationRepository;
import dku.server.domain.chat.tool.ContextRetrievalTool;
import dku.server.domain.chat.tool.QuestionAnalysisTool;
import dku.server.domain.common.BaseEntity;
import dku.server.domain.member.domain.Member;
import dku.server.domain.member.repository.MemberRepository;
import dku.server.global.exception.CustomException;
import dku.server.global.exception.ErrorCode;
import dku.server.global.security.oidc.UserInfo;
import dku.server.global.util.MemberUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {

    private final ChatClient chatClient;
    private final ContextRetrievalTool contextRetrievalTool;
    private final QuestionAnalysisTool questionAnalysisTool;
    private final OpenAiChatOptions standardStreamModelChatOptions;
    private final MemberRepository memberRepository;
    private final ConversationRepository conversationRepository;
    private final ConversationPersistenceService conversationPersistenceService;

    public List<ConversationResponse> getConversations() {
        UserInfo userInfo = MemberUtil.getCurrentUserInfo();
        Member member = memberRepository.findById(userInfo.getMemberId())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        return conversationRepository.findAllByMemberId(member.getId()).stream()
                .map(ConversationResponse::from)
                .toList();
    }

    public ConversationResponse getConversation(UUID conversationId) {
        UserInfo userInfo = MemberUtil.getCurrentUserInfo();
        Member member = memberRepository.findById(userInfo.getMemberId())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Conversation conversation = conversationRepository.findByIdAndMemberId(conversationId, member.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.CONVERSATION_NOT_FOUND));

        return ConversationResponse.from(conversation);
    }

    public List<MessageResponse> getChatHistory(UUID conversationId) {
        UserInfo userInfo = MemberUtil.getCurrentUserInfo();
        Member member = memberRepository.findById(userInfo.getMemberId())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Conversation conversation = conversationRepository.findByIdAndMemberId(conversationId, member.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.CONVERSATION_NOT_FOUND));

        return conversation.getConversationMessages().stream()
                .filter(message -> message.getRole() == Role.USER || message.getRole() == Role.ASSISTANT)
                .sorted(Comparator.comparing(BaseEntity::getCreatedAt))
                .map(MessageResponse::from)
                .toList();
    }

    @Transactional
    public ConversationCreateResponse createConversation() {
        UserInfo userInfo = MemberUtil.getCurrentUserInfo();
        Member member = memberRepository.findById(userInfo.getMemberId())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Conversation conversation = Conversation.createEmpty(member);
        conversation.addSystemMessage();
        conversationRepository.save(conversation);

        return ConversationCreateResponse.from(conversation);
    }

    @SuppressWarnings("BlockingMethodInNonBlockingContext")
    public Flux<String> chatCompletionStream(ChatCompletionRequest request) {
        UserInfo userInfo = MemberUtil.getCurrentUserInfo();
        Member member = memberRepository.findById(userInfo.getMemberId())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        Conversation conversation = conversationRepository.findByIdAndMemberId(request.conversationId(), member.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.CONVERSATION_NOT_FOUND));
        conversation.updateTitle(request.content());
        List<Message> messageHistory = convertToMessageHistory(conversation);

        StringBuilder sb = new StringBuilder();
        Flux<String> llmResponseStream = chatClient.prompt()
                .options(standardStreamModelChatOptions)
                .messages(messageHistory)
                .user(request.content())
                .tools(questionAnalysisTool, contextRetrievalTool)
                .stream()
                .content()
                .doOnNext(sb::append)
                .publishOn(Schedulers.boundedElastic())
                .doOnComplete(() -> {
                    conversationPersistenceService.saveConversationMessage(
                            conversation.getId(),
                            request.content(),
                            sb.toString());
                })
                .onErrorReturn("[ERROR]");

        return Flux.just("[CONNECTED]")
                .concatWith(llmResponseStream)
                .concatWith(Flux.just("[DONE]"))
                .onErrorReturn("[ERROR]");
    }

    @Transactional
    public void deleteConversation(UUID conversationId) {
        UserInfo userInfo = MemberUtil.getCurrentUserInfo();
        Member member = memberRepository.findById(userInfo.getMemberId())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        Conversation conversation = conversationRepository.findByIdAndMemberId(conversationId, member.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.CONVERSATION_NOT_FOUND));
        conversationRepository.delete(conversation);
    }

    @Transactional
    public ConversationResponse generateConversationTitle(UUID conversationId) {
        UserInfo userInfo = MemberUtil.getCurrentUserInfo();
        Member member = memberRepository.findById(userInfo.getMemberId())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Conversation conversation = conversationRepository.findByIdAndMemberId(conversationId, member.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.CONVERSATION_NOT_FOUND));

        if (conversation.getTitle() != null) {
            throw new CustomException(ErrorCode.CONVERSATION_TITLE_ALREADY_EXISTS);
        }

        String conversationContent = buildConversationContent(conversation);

        PromptTemplate promptTemplate = PromptTemplate.builder()
                .template(Template.GENERATE_CONVERSATION_TITLE_TEMPLATE)
                .variables(Map.of("conversation", conversationContent))
                .build();
        Prompt prompt = promptTemplate.create(standardStreamModelChatOptions);
        String generatedTitle;

        try {
            generatedTitle = chatClient.prompt(prompt)
                    .call()
                    .chatResponse()
                    .getResult()
                    .getOutput()
                    .getText();
        } catch (Exception e) {
            throw new CustomException(ErrorCode.CHAT_TITLE_GENERATION_FAILED);
        }

        conversation.updateTitle(refineOutput(generatedTitle));

        return ConversationResponse.from(conversation);
    }

    private String buildConversationContent(Conversation conversation) {
        return conversation.getConversationMessages().stream()
                .filter(message -> message.getRole() == Role.USER || message.getRole() == Role.ASSISTANT)
                .sorted(Comparator.comparing(BaseEntity::getCreatedAt))
                .map(message -> {
                    String roleLabel = message.getRole() == Role.USER ? "USER" : "ASSISTANT";
                    return roleLabel + ": " + message.getContent();
                })
                .collect(Collectors.joining("\n"));
    }

    private String refineOutput(String text) {
        text = text.trim();
        if (text.startsWith("```json")) {
            return text.substring(7, text.length() - 3);
        } else if (text.startsWith("```")) {
            return text.substring(3, text.length() - 3);
        } else {
            return text;
        }
    }

    private List<Message> convertToMessageHistory(Conversation conversation) {
        return conversation.getConversationMessages().stream()
                .map(message -> {
                    if (message.getRole() == Role.USER) {
                        return new UserMessage(message.getContent());
                    } else if (message.getRole() == Role.ASSISTANT) {
                        return new AssistantMessage(message.getContent());
                    } else if (message.getRole() == Role.SYSTEM) {
                        return new SystemMessage(message.getContent());
                    } else {
                        throw new CustomException(ErrorCode.INVALID_MESSAGE_ROLE);
                    }
                })
                .collect(Collectors.toList());
    }
}
