package dku.server.domain.chat.repository;

import dku.server.domain.chat.domain.ProcessedArticle;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Selection;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ProcessedArticleDynamicRepository {

    private static final List<String> validFieldNames = List.of(
            "applicationPrep",
            "preDeparture",
            "afterArrival",
            "academicLife",
            "accommodationAndMeal",
            "otherTips",
            "verdict"
    );

    @PersistenceContext
    private EntityManager em;

    /**
     * 학교 이름과 요청된 필드에 따라 Map 형태로 결과를 반환합니다.
     *
     * @param schoolName 학교 이름
     * @param fieldNames 요청된 필드 이름 목록
     * @return Map 리스트 (각 항목은 필드명-값 쌍을 포함)
     */
    public List<Map<String, Object>> findProcessedArticleBySchoolName(String schoolName, List<String> fieldNames) {
        if (!validateFieldName(fieldNames)) {
            return List.of();
        }

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();

        Root<ProcessedArticle> root = query.from(ProcessedArticle.class);

        List<Selection<?>> selections = new ArrayList<>();

        // 항상 articleId, schoolName 추가
        selections.add(root.get("articleId").alias("articleId"));
        selections.add(root.get("schoolName").alias("schoolName"));

        // 사용자가 요청한 필드 추가
        for (String fieldName : fieldNames) {
            selections.add(root.get(fieldName).alias(fieldName));
        }

        query.multiselect(selections);
        query.where(cb.equal(root.get("schoolName"), schoolName));

        List<Tuple> tuples = em.createQuery(query).getResultList();

        // Tuple을 Map으로 변환
        return tuples.stream()
                .map(tuple -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("articleId", tuple.get("articleId"));
                    result.put("schoolName", tuple.get("schoolName"));

                    for (String fieldName : fieldNames) {
                        result.put(fieldName, tuple.get(fieldName));
                    }

                    return result;
                })
                .toList();
    }

    private boolean validateFieldName(List<String> fieldNames) {
        for (String fieldName : fieldNames) {
            if (!validFieldNames.contains(fieldName)) {
                return false;
            }
        }
        return true;
    }
}
