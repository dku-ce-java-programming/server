package dku.server.domain.chat.tool;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dku.server.domain.chat.repository.ProcessedArticleRepository;
import dku.server.global.exception.CustomException;
import dku.server.global.exception.ErrorCode;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static dku.server.domain.chat.prompt.Template.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class QuestionAnalysisTool {

    private static final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    private final ChatClient chatClient;
    private final ProcessedArticleRepository processedArticleRepository;
    private final ObjectMapper objectMapper;
    private final OpenAiChatOptions standardModelChatOptions;

    @PreDestroy
    public void shutdown() {
        executor.shutdown();
    }

    @Tool(
            name = "classifyQuestion",
            description = "사용자의 질문 유형을 분류합니다"
    )
    public String classifyQuestion(
            @ToolParam(description = "분석할 사용자의 질문") String question
    ) {
        PromptTemplate promptTemplate = new PromptTemplate(
                CLASSIFY_QUESTION_TYPE_TEMPLATE,
                Map.of("question", question)
        );

        Prompt prompt = promptTemplate.create(standardModelChatOptions);

        try {
            String outputText = chatClient.prompt(prompt)
                    .call()
                    .chatResponse()
                    .getResult()
                    .getOutput()
                    .getText();
            log.info("classifyQuestion: {}", refineOutput(outputText));
            return refineOutput(outputText);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.CHAT_TOOL_CALLING_CLASSIFY_QUESTION_TYPE_FAILED);
        }
    }

    @Tool(
            name = "extractQuestionMetadata",
            description = "사용자의 질문에서 메타데이터(학교명, 질문의 주제)를 추출합니다."
    )
    public QuestionMetadata extractQuestionMetadata(
            @ToolParam(description = "분석할 사용자의 질문") String question
    ) {
        CompletableFuture<String> schoolNameFuture = CompletableFuture.supplyAsync(
                () -> extractSchoolName(question),
                executor
        );

        CompletableFuture<List<String>> columnsFuture = CompletableFuture.supplyAsync(
                () -> extractColumns(question),
                executor
        );

        return CompletableFuture.allOf(schoolNameFuture, columnsFuture)
                .thenApply(v -> {
                    String schoolName = schoolNameFuture.join();
                    List<String> columns = columnsFuture.join();
                    log.info("extractQuestionMetadata: {}, {}", refineOutput(schoolName), columns);
                    return new QuestionMetadata(schoolName, columns);
                })
                .join();
    }

    /**
     * 사용자의 질문에서 학교명을 추출합니다.<br/>
     * 조회 전용이므로 @Transactional을 사용하지 않습니다.
     *
     * @param question 분석할 사용자의 질문
     * @return 추출된 학교명 또는 null
     */
    private String extractSchoolName(String question) {
        List<String> distinctSchoolNames = processedArticleRepository.findDistinctSchoolNames();

        PromptTemplate promptTemplate = new PromptTemplate(
                EXTRACT_SCHOOL_NAME_TEMPLATE,
                Map.of("question", question, "distinct_school_name_list", distinctSchoolNames)
        );

        Prompt prompt = promptTemplate.create(standardModelChatOptions);

        try {
            String outputText = chatClient.prompt(prompt)
                    .call()
                    .chatResponse()
                    .getResult()
                    .getOutput()
                    .getText();
            if (outputText.equals("None") || outputText.isEmpty()) {
                return null;
            }
            return refineOutput(outputText);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.CHAT_TOOL_CALLING_EXTRACT_SCHOOL_NAME_FAILED);
        }
    }

    /**
     * 사용자의 질문에서 열(column) 이름을 추출합니다.<br/>
     *
     * @param question 분석할 사용자의 질문
     * @return 추출된 열 이름 리스트
     */
    private List<String> extractColumns(String question) {
        PromptTemplate promptTemplate = new PromptTemplate(
                EXTRACT_COLUMN_TEMPLATE,
                Map.of("question", question)
        );

        Prompt prompt = promptTemplate.create(standardModelChatOptions);

        try {
            String outputText = chatClient.prompt(prompt)
                    .call()
                    .chatResponse()
                    .getResult()
                    .getOutput()
                    .getText();

            return objectMapper.readValue(
                    refineOutput(outputText),
                    new TypeReference<List<String>>() {
                    }
            );
        } catch (JsonProcessingException e) {
            throw new CustomException(ErrorCode.CHAT_TOOL_CALLING_EXTRACT_COLUMNS_MAPPING_FAILED);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.CHAT_TOOL_CALLING_EXTRACT_COLUMNS_FAILED);
        }
    }

    private String refineOutput(String text) {
        text = text.trim();
        if (text.startsWith("```json")) {
            return text.substring(7, text.length() - 3);
        } else if (text.startsWith("```")) {
            return text.substring(3, text.length() - 3);
        } else {
            return text;
        }
    }
}
