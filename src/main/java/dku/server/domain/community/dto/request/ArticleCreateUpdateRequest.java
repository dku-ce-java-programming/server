package dku.server.domain.community.dto.request;

import dku.server.domain.community.domain.Category;

public record ArticleCreateUpdateRequest(
        Category category,
        String title,
        String content
) {
}
