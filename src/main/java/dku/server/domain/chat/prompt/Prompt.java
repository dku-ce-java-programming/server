package dku.server.domain.chat.prompt;

import static dku.server.global.constant.GeminiConstant.GEMINI_FLASH_LITE;

public record Prompt(
        String model,
        Float temperature,
        Integer maxOutputTokens,
        String template
) {
    public static final Prompt GENERATE_CONVERSATION_TITLE = new Prompt(
            GEMINI_FLASH_LITE,
            0.7f,
            64,
            Template.GENERATE_CONVERSATION_TITLE_TEMPLATE
    );
}
