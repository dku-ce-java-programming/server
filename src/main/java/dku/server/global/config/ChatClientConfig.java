package dku.server.global.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {

    @Value("${llm.model.standard.name}")
    private String standardModelName;

    @Value("${llm.model.standard.temperature}")
    private Double standardModelTemperature;

    @Value("${llm.model.lite.name}")
    private String liteModelName;

    @Value("${llm.model.lite.temperature}")
    private Double liteModelTemperature;

    @Bean
    public ChatClient chatClient(ChatClient.Builder chatClientBuilder) {
        return chatClientBuilder.build();
    }

    @Bean
    public OpenAiChatOptions standardModelChatOptions() {
        return OpenAiChatOptions.builder()
                .model(standardModelName)
                .temperature(standardModelTemperature)
                .build();
    }

    @Bean
    public OpenAiChatOptions standardStreamModelChatOptions() {
        return OpenAiChatOptions.builder()
                .model(standardModelName)
                .temperature(standardModelTemperature)
                .streamUsage(true)
                .build();
    }

    @Bean
    public OpenAiChatOptions liteModelChatOptions() {
        return OpenAiChatOptions.builder()
                .model(liteModelName)
                .temperature(liteModelTemperature)
                .build();
    }
}
