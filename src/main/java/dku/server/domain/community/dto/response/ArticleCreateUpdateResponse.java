package dku.server.domain.community.dto.response;

import dku.server.domain.community.domain.Article;

public record ArticleCreateUpdateResponse(
        Long id
) {
    public static ArticleCreateUpdateResponse from(Article article) {
        return new ArticleCreateUpdateResponse(
                article.getId()
        );
    }

}
