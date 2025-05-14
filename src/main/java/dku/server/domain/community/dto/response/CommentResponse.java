package dku.server.domain.community.dto.response;

import dku.server.domain.community.domain.Comment;

import java.util.List;

public record CommentResponse(
        Long id,
        String author,
        String content,
        List<CommentResponse> replies
) {
    public static CommentResponse from(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getMember().getName(),
                comment.getContent(),
                comment.getChildren().stream()
                        .map(CommentResponse::from)
                        .toList()
        );
    }
}
