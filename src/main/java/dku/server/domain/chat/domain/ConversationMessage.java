package dku.server.domain.chat.domain;

import dku.server.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

import static dku.server.domain.chat.prompt.Template.SYSTEM_PROMPT;

@Entity
@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ConversationMessage extends BaseEntity {

    @Id
    @Column(name = "conversation_message_id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Lob
    private String content;

    public static ConversationMessage createUserMessage(String content) {
        return ConversationMessage.builder()
                .id(UUID.randomUUID())
                .role(Role.USER)
                .content(content)
                .build();
    }

    public static ConversationMessage createAssistantMessage(String content) {
        return ConversationMessage.builder()
                .id(UUID.randomUUID())
                .role(Role.ASSISTANT)
                .content(content)
                .build();
    }

    public static ConversationMessage createSystemMessage() {
        return ConversationMessage.builder()
                .id(UUID.randomUUID())
                .role(Role.SYSTEM)
                .content(SYSTEM_PROMPT)
                .build();
    }

    /**
     * Message 생성 시 Conversation에 추가하는 메서드<br/>
     * 단독 호출 금지
     */
    public void setConversationInternal(Conversation conversation) {
        this.conversation = conversation;
    }
}
