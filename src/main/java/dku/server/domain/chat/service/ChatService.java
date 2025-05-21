package dku.server.domain.chat.service;

import dku.server.domain.chat.domain.Conversation;
import dku.server.domain.chat.domain.Role;
import dku.server.domain.chat.dto.request.ChatCompletionRequest;
import dku.server.domain.chat.dto.response.ConversationCreateResponse;
import dku.server.domain.chat.dto.response.ConversationResponse;
import dku.server.domain.chat.repository.ConversationRepository;
import dku.server.domain.chat.tool.ContextRetrievalTool;
import dku.server.domain.chat.tool.QuestionAnalysisTool;
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
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SignalType;
import reactor.core.scheduler.Schedulers;

import java.util.List;
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

        return chatClient.prompt()
                .options(standardStreamModelChatOptions)
                .messages(messageHistory)
                .user(request.content())
                .tools(questionAnalysisTool, contextRetrievalTool)
                .stream()
                .content()
                .doOnNext(sb::append)
                .publishOn(Schedulers.boundedElastic())
                .doFinally(signalType -> {
                    if (signalType == SignalType.ON_COMPLETE) {
                        conversationPersistenceService.saveConversationMessage(
                                conversation.getId(),
                                request.content(),
                                sb.toString()
                        );
                    }
                });

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
