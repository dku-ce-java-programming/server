package dku.server.domain.community.repository;

import dku.server.domain.community.domain.Article;
import dku.server.domain.community.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    List<Article> findAllByCategory(Category category);
}
