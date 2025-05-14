package dku.server.domain.community.service;

import dku.server.domain.community.domain.Article;
import dku.server.domain.community.domain.Comment;
import dku.server.domain.community.dto.response.CommentResponse;
import dku.server.domain.community.repository.ArticleRepository;
import dku.server.domain.community.repository.CommentRepository;
import dku.server.domain.member.domain.Member;
import dku.server.domain.member.repository.MemberRepository;
import dku.server.global.exception.CustomException;
import dku.server.global.exception.ErrorCode;
import dku.server.global.security.oidc.UserInfo;
import dku.server.global.util.MemberUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public CommentResponse createComment(Long articleId,
                                         dku.server.domain.community.dto.request.CommentCreateRequest request) {
        UserInfo userInfo = MemberUtil.getCurrentUserInfo();
        Member member = memberRepository.findById(userInfo.getMemberId())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new CustomException(ErrorCode.ARTICLE_NOT_FOUND));

        Comment comment = commentRepository.save(
                Comment.create(article, member, request.content()));
        return CommentResponse.fromAsc(comment);
    }

    @Transactional
    public CommentResponse createReply(Long articleId, Long parentId,
                                       dku.server.domain.community.dto.request.CommentCreateRequest request) {
        UserInfo userInfo = MemberUtil.getCurrentUserInfo();
        Member member = memberRepository.findById(userInfo.getMemberId())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new CustomException(ErrorCode.ARTICLE_NOT_FOUND));
        Comment parent = commentRepository.findById(parentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        Comment reply = commentRepository.save(
                Comment.createReply(article, member, request.content(), parent));
        return CommentResponse.fromAsc(reply);
    }

    @Transactional
    public CommentResponse updateComment(Long commentId,
                                         dku.server.domain.community.dto.request.CommentCreateRequest request) {
        UserInfo userInfo = MemberUtil.getCurrentUserInfo();
        Member member = memberRepository.findById(userInfo.getMemberId())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
        if (!comment.getMember().getId().equals(member.getId())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        comment.update(request.content());
        return CommentResponse.fromAsc(comment);
    }

    @Transactional
    public void deleteComment(Long commentId) {
        UserInfo userInfo = MemberUtil.getCurrentUserInfo();
        Member member = memberRepository.findById(userInfo.getMemberId())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
        if (!comment.getMember().getId().equals(member.getId())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        commentRepository.delete(comment);
    }
}
