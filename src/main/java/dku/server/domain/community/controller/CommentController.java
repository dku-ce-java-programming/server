package dku.server.domain.community.controller;

import dku.server.domain.community.dto.request.CommentCreateRequest;
import dku.server.domain.community.dto.response.CommentResponse;
import dku.server.domain.community.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/articles/{articleId}/comments")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponse> createComment(
            @PathVariable Long articleId,
            @RequestBody CommentCreateRequest request
    ) {
        CommentResponse response = commentService.createComment(articleId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{parentId}/reply")
    public ResponseEntity<CommentResponse> createReply(
            @PathVariable Long articleId,
            @PathVariable Long parentId,
            @RequestBody CommentCreateRequest request
    ) {
        CommentResponse response = commentService.createReply(articleId, parentId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable Long articleId,
            @PathVariable Long commentId,
            @RequestBody CommentCreateRequest request
    ) {
        CommentResponse response = commentService.updateComment(commentId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long articleId,
            @PathVariable Long commentId
    ) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}
