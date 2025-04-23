package dku.server.domain.chat.domain;

import dku.server.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Message extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(length = 1048575)
    private String content;

    public static Message create(
            Conversation conversation,
            Role role,
            String content
    ) {
        return Message.builder()
                .conversation(conversation)
                .role(role)
                .content(content)
                .build();
    }
}
