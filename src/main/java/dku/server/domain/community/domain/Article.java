package dku.server.domain.community.domain;

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
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "article_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String title;

    @Lob
    private String content;

    @OneToMany(
            mappedBy = "article",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();

    public static Article create(
            Category category,
            Member member,
            String title,
            String content
    ) {
        return Article.builder()
                .category(category)
                .member(member)
                .title(title)
                .content(content)
                .build();
    }

    public void update(
            Category category,
            String title,
            String content
    ) {
        this.category = category;
        this.title = title;
        this.content = content;
    }
}
