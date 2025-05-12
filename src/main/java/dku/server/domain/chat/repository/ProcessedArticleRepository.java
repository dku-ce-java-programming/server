package dku.server.domain.chat.repository;

import dku.server.domain.chat.domain.ProcessedArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProcessedArticleRepository extends JpaRepository<ProcessedArticle, Long> {
    
    @Query("SELECT DISTINCT pa.schoolName FROM ProcessedArticle pa")
    List<String> findDistinctSchoolNames();
}
