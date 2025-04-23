package dku.server.domain.chat.domain;

import dku.server.domain.common.BaseEntity;
import dku.server.domain.member.domain.Member;
import jakarta.persistence.*;
import lombok.*;

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

    public static Conversation create(
            UUID id,
            Member member,
            String title
    ) {
        return Conversation.builder()
                .id(id)
                .member(member)
                .title(title)
                .build();
    }

    public void updateTitle(String title) {
        this.title = title;
    }
}
