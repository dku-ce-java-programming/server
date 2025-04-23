package dku.server.domain.chat.repository;

import dku.server.domain.chat.domain.Conversation;
import dku.server.domain.chat.domain.Message;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findAllByConversation(Conversation conversation, Sort sort, Limit limit);
}
