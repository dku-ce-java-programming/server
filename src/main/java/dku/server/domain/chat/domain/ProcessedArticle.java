package dku.server.domain.chat.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder(access = lombok.AccessLevel.PRIVATE)
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class ProcessedArticle {

    @Id
    @Column(name = "processed_article_id")
    private Long id;

    private Long articleId;

    private String schoolName;

    private String semester;

    private String country;

    @Lob
    private String applicationPrep;

    @Lob
    private String preDeparture;

    @Lob
    private String afterArrival;

    @Lob
    private String academicLife;

    @Lob
    private String accommodationAndMeal;

    @Lob
    private String otherTips;

    @Lob
    private String verdict;
}
