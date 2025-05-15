package dku.server.domain.community.controller;

import dku.server.domain.community.domain.Category;
import dku.server.domain.community.dto.request.ArticleCreateUpdateRequest;
import dku.server.domain.community.dto.response.ArticleCreateUpdateResponse;
import dku.server.domain.community.dto.response.ArticleDetailResponse;
import dku.server.domain.community.dto.response.ArticlePreviewResponse;
import dku.server.domain.community.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/articles")
public class ArticleController {

    private final ArticleService articleService;

    @PostMapping
    public ResponseEntity<ArticleCreateUpdateResponse> createArticle(
            @RequestBody ArticleCreateUpdateRequest request) {
        ArticleCreateUpdateResponse response = articleService.createArticle(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ArticlePreviewResponse>> getArticleList(
            @RequestParam(required = false) Category category
    ) {
        List<ArticlePreviewResponse> responses = articleService.getArticleList(category);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{articleId}")
    public ResponseEntity<ArticleDetailResponse> getArticleDetail(
            @PathVariable Long articleId
    ) {
        ArticleDetailResponse response = articleService.getArticleDetail(articleId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{articleId}")
    public ResponseEntity<ArticleCreateUpdateResponse> updateArticle(
            @PathVariable Long articleId,
            @RequestBody ArticleCreateUpdateRequest request
    ) {
        ArticleCreateUpdateResponse response = articleService.updateArticle(articleId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{articleId}")
    public ResponseEntity<Void> deleteArticle(@PathVariable Long articleId) {
        articleService.deleteArticle(articleId);
        return ResponseEntity.noContent().build();
    }
}
