package dku.server.domain.community.dto.response;

import dku.server.domain.community.domain.Article;
import dku.server.domain.community.domain.Category;

import java.util.List;

public record ArticleDetailResponse(
        Long articleId,
        Category category,
        String author,
        String title,
        String content,
        List<CommentResponse> comments) {
    public static ArticleDetailResponse from(Article article) {
        return new ArticleDetailResponse(
                article.getId(),
                article.getCategory(),
                article.getMember().getName(),
                article.getTitle(),
                article.getContent(),
                article.getRootCommentsAsc().stream()
                        .map(CommentResponse::fromAsc)
                        .toList());
    }
}
