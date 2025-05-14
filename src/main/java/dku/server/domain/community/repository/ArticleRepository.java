package dku.server.domain.community.repository;

import dku.server.domain.community.domain.Article;
import dku.server.domain.community.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    @Query("SELECT a FROM Article a JOIN FETCH a.member")
    List<Article> findAllWithMember();

    @Query("SELECT a FROM Article a JOIN FETCH a.member WHERE a.category = :category")
    List<Article> findAllByCategoryWithMember(@Param("category") Category category);
}
