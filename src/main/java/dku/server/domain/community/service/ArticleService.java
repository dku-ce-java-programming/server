package dku.server.domain.community.service;

import dku.server.domain.community.domain.Article;
import dku.server.domain.community.domain.Category;
import dku.server.domain.community.dto.request.ArticleCreateUpdateRequest;
import dku.server.domain.community.dto.response.ArticleCreateUpdateResponse;
import dku.server.domain.community.dto.response.ArticleDetailResponse;
import dku.server.domain.community.dto.response.ArticlePreviewResponse;
import dku.server.domain.community.repository.ArticleRepository;
import dku.server.domain.member.domain.Member;
import dku.server.domain.member.repository.MemberRepository;
import dku.server.global.exception.CustomException;
import dku.server.global.exception.ErrorCode;
import dku.server.global.security.oidc.UserInfo;
import dku.server.global.util.MemberUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public ArticleCreateUpdateResponse createArticle(ArticleCreateUpdateRequest request) {
        UserInfo userInfo = MemberUtil.getCurrentUserInfo();
        Member member = memberRepository.findById(userInfo.getMemberId())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Article article = articleRepository.save(Article.create(
                        request.category(),
                        member,
                        request.title(),
                        request.content()
                )
        );

        return ArticleCreateUpdateResponse.from(article);
    }

    public List<ArticlePreviewResponse> getArticleList(Category category) {
        if (category == null) {
            return articleRepository.findAllWithMember().stream()
                    .map(ArticlePreviewResponse::from)
                    .toList();
        }
        return articleRepository.findAllByCategoryWithMember(category).stream()
                .map(ArticlePreviewResponse::from)
                .toList();
    }

    public ArticleDetailResponse getArticleDetail(Long articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new CustomException(ErrorCode.ARTICLE_NOT_FOUND));
        return ArticleDetailResponse.from(article);
    }

    @Transactional
    public ArticleCreateUpdateResponse updateArticle(
            Long articleId,
            ArticleCreateUpdateRequest request
    ) {
        UserInfo userInfo = MemberUtil.getCurrentUserInfo();
        Member member = memberRepository.findById(userInfo.getMemberId())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new CustomException(ErrorCode.ARTICLE_NOT_FOUND));

        if (!article.getMember().getId().equals(member.getId())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        article.update(
                request.category(),
                request.title(),
                request.content()
        );

        return ArticleCreateUpdateResponse.from(article);
    }

    @Transactional
    public void deleteArticle(Long articleId) {
        UserInfo userInfo = MemberUtil.getCurrentUserInfo();
        Member member = memberRepository.findById(userInfo.getMemberId())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new CustomException(ErrorCode.ARTICLE_NOT_FOUND));
        if (!article.getMember().getId().equals(member.getId())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        articleRepository.delete(article);
    }
}
