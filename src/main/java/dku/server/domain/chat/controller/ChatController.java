package dku.server.domain.chat.controller;

import dku.server.domain.chat.dto.request.ChatCompletionRequest;
import dku.server.domain.chat.dto.response.ConversationCreateResponse;
import dku.server.domain.chat.dto.response.ConversationResponse;
import dku.server.domain.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @GetMapping
    public ResponseEntity<List<ConversationResponse>> getConversations() {
        return ResponseEntity.ok(chatService.getConversations());
    }

    @PostMapping
    public ResponseEntity<ConversationCreateResponse> createConversation() {
        return ResponseEntity.status(HttpStatus.CREATED).body(chatService.createConversation());
    }

    @PostMapping(value = "/completion", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatCompletionStream(
            @RequestBody ChatCompletionRequest request
    ) {
        return chatService.chatCompletionStream(request);
    }

    @DeleteMapping("/{conversationId}")
    public ResponseEntity<Void> deleteConversation(@PathVariable UUID conversationId) {
        chatService.deleteConversation(conversationId);
        return ResponseEntity.noContent().build();
    }
}
