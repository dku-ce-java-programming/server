package dku.server.domain.chat.tool;

import com.fasterxml.jackson.databind.ObjectMapper;
import dku.server.domain.chat.repository.ProcessedArticleDynamicRepository;
import dku.server.global.exception.CustomException;
import dku.server.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContextRetrievalTool {

    private final ProcessedArticleDynamicRepository processedArticleDynamicRepository;
    private final ObjectMapper objectMapper;

    @Tool(
            name = "retrieveProcessedArticle",
            description = "교환학생 관련 질문에 대한 답변을 위해, 사용자가 요청한 학교명과 질문 주제에 따라 적절한 맥락 정보를 데이터베이스에서 쿼리하여 제공합니다."
    )
    public String retrieveContext(
            @ToolParam(description = "학교명") String schoolName,
            @ToolParam(description = "질문 주제행") List<String> columnNames
    ) {
        return convertToJson(processedArticleDynamicRepository.findProcessedArticleBySchoolName(schoolName, columnNames));
    }

    /**
     * Map 리스트를 JSON 문자열로 변환합니다.
     *
     * @param mapList 필드명-값 쌍을 포함하는 Map 리스트
     * @return JSON 문자열
     */
    private String convertToJson(List<Map<String, Object>> mapList) {
        if (mapList == null || mapList.isEmpty()) {
            return "[]";
        }

        try {
            return objectMapper.writeValueAsString(mapList);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.CHAT_TOOL_CALLING_RETRIEVE_CONTEXT_MAPPING_FAILED);
        }
    }
}

