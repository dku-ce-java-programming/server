package dku.server.global.config;

import com.google.genai.Client;
import com.google.genai.types.SafetySetting;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class GeminiConfig {

    public static List<SafetySetting> GEMINI_SAFETY_SETTINGS = List.of(
            SafetySetting.builder()
                    .category("HARM_CATEGORY_HARASSMENT")
                    .threshold("BLOCK_NONE")
                    .build(),
            SafetySetting.builder()
                    .category("HARM_CATEGORY_HATE_SPEECH")
                    .threshold("BLOCK_NONE")
                    .build(),
            SafetySetting.builder()
                    .category("HARM_CATEGORY_SEXUALLY_EXPLICIT")
                    .threshold("BLOCK_NONE")
                    .build(),
            SafetySetting.builder()
                    .category("HARM_CATEGORY_DANGEROUS_CONTENT")
                    .threshold("BLOCK_NONE")
                    .build(),
            SafetySetting.builder()
                    .category("HARM_CATEGORY_CIVIC_INTEGRITY")
                    .threshold("BLOCK_NONE")
                    .build()
    );

    @Value("${key.gemini}")
    private String geminiApiKey;

    @Bean
    public Client geminiClient() {
        return Client.builder()
                .apiKey(geminiApiKey)
                .build();
    }
}
