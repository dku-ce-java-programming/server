package dku.server.domain.chat.controller;

import dku.server.domain.chat.dto.request.ConversationCreateRequest;
import dku.server.domain.chat.service.ConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
