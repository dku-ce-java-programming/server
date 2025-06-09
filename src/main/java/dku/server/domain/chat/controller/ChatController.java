package dku.server.domain.chat.controller;

import dku.server.domain.chat.dto.request.ChatCompletionRequest;
import dku.server.domain.chat.dto.response.ConversationCreateResponse;
import dku.server.domain.chat.dto.response.ConversationResponse;
import dku.server.domain.chat.dto.response.MessageResponse;
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

    @GetMapping("/{conversationId}")
    public ResponseEntity<ConversationResponse> getConversation(@PathVariable UUID conversationId) {
        return ResponseEntity.ok(chatService.getConversation(conversationId));
    }

    @GetMapping("/{conversationId}/history")
    public ResponseEntity<List<MessageResponse>> getChatHistory(@PathVariable UUID conversationId) {
        return ResponseEntity.ok(chatService.getChatHistory(conversationId));
    }

    @PostMapping
    public ResponseEntity<ConversationCreateResponse> createConversation() {
        return ResponseEntity.status(HttpStatus.CREATED).body(chatService.createConversation());
    }

    @GetMapping(value = "/completion", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatCompletionStream(
            @RequestParam UUID conversationId,
            @RequestParam String content) {
        ChatCompletionRequest request = new ChatCompletionRequest(conversationId, content);
        return chatService.chatCompletionStream(request);
    }

    @PutMapping("/{conversationId}/generate-title")
    public ResponseEntity<ConversationResponse> generateConversationTitle(@PathVariable UUID conversationId) {
        ConversationResponse response = chatService.generateConversationTitle(conversationId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{conversationId}")
    public ResponseEntity<Void> deleteConversation(@PathVariable UUID conversationId) {
        chatService.deleteConversation(conversationId);
        return ResponseEntity.noContent().build();
    }
}
