package dku.server.domain.community.dto.response;

import dku.server.domain.community.domain.Article;
import dku.server.domain.community.domain.Category;

public record ArticlePreviewResponse(
        Long id,
        Category category,
        String author,
        String title
) {
    public static ArticlePreviewResponse from(Article article) {
        return new ArticlePreviewResponse(
                article.getId(),
                article.getCategory(),
                article.getMember().getName(),
                article.getTitle()
        );
    }
}
