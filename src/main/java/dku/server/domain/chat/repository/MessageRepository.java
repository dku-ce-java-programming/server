package dku.server.domain.chat.repository;

import dku.server.domain.chat.domain.Conversation;
import dku.server.domain.chat.domain.ConversationMessage;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<ConversationMessage, UUID> {

    List<ConversationMessage> findAllByConversation(Conversation conversation, Sort sort, Limit limit);

    List<ConversationMessage> findAllByConversation(Conversation conversation, Sort sort);
}
