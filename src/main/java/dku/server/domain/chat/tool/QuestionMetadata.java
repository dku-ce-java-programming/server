package dku.server.domain.chat.tool;

import java.util.List;

public record QuestionMetadata(
        String schoolName,
        List<String> columnNames
) {
}
