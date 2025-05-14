package dku.server.domain.community.dto.response;

import dku.server.domain.common.BaseEntity;
import dku.server.domain.community.domain.Comment;

import java.util.Comparator;
import java.util.List;

public record CommentResponse(
        Long id,
        String author,
        String content,
        List<CommentResponse> replies
) {
    public static CommentResponse fromAsc(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getMember().getName(),
                comment.getContent(),
                comment.getChildren().stream()
                        .sorted(Comparator.comparing(BaseEntity::getCreatedAt))
                        .map(CommentResponse::fromAsc)
                        .toList());
    }
}
