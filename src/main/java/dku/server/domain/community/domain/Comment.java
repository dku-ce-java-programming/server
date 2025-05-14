package dku.server.domain.community.domain;

import dku.server.domain.common.BaseEntity;
import dku.server.domain.member.domain.Member;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id")
    private Article article;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @OneToMany(
            mappedBy = "parent",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            fetch = FetchType.LAZY
    )
    @Builder.Default
    private List<Comment> children = new ArrayList<>();

    @Column(length = 8192)
    private String content;

    public static Comment create(
            Article article,
            Member member,
            String content) {
        return Comment.builder()
                .article(article)
                .member(member)
                .content(content)
                .build();
    }

    public static Comment createReply(
            Article article,
            Member member,
            String content,
            Comment parent) {
        return Comment.builder()
                .article(article)
                .member(member)
                .content(content)
                .parent(parent)
                .build();
    }
}
