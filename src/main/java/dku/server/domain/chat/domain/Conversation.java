package dku.server.domain.chat.domain;

import dku.server.domain.common.BaseEntity;
import dku.server.domain.member.domain.Member;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Conversation extends BaseEntity {

    @Id
    @Column(name = "coversation_id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String title;

    @OneToMany(
            mappedBy = "conversation",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<ConversationMessage> conversationMessages = new ArrayList<>();

    public static Conversation createEmpty(
            Member member
    ) {
        return Conversation.builder()
                .id(UUID.randomUUID())
                .member(member)
                .build();
    }

    public void addSystemMessage() {
        ConversationMessage systemMessage = ConversationMessage.createSystemMessage();
        this.conversationMessages.add(systemMessage);
        systemMessage.setConversationInternal(this);
    }

    public void addMessage(ConversationMessage message) {
        this.conversationMessages.add(message);
        message.setConversationInternal(this);
    }

    public void updateTitle(String title) {
        this.title = title;
    }
}
