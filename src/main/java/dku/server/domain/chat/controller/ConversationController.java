package dku.server.domain.chat.controller;

import dku.server.domain.chat.dto.request.ConversationCreateRequest;
import dku.server.domain.chat.dto.response.TitleGenerationResponse;
import dku.server.domain.chat.service.ConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ConversationController {

    private final ConversationService conversationService;

    @PostMapping("/conversation")
    public ResponseEntity<Void> createConversation(
            @RequestBody ConversationCreateRequest request
    ) {
        conversationService.createConversation(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/conversation/{conversationId}/title")
    public ResponseEntity<TitleGenerationResponse> generateTitle(
            @PathVariable("conversationId") UUID conversationId
    ) {
        return ResponseEntity.ok(conversationService.generateTitle(conversationId));
    }
}
