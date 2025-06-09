package dku.server.domain.chat.repository;

import dku.server.domain.chat.domain.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConversationRepository extends JpaRepository<Conversation, UUID> {

    @Query("SELECT c FROM Conversation c WHERE c.member.id = :memberId ORDER BY c.createdAt DESC")
    List<Conversation> findAllByMemberId(Long memberId);

    @Query("SELECT c FROM Conversation c LEFT JOIN FETCH c.conversationMessages m WHERE c.id = :id AND c.member.id = :memberId ORDER BY c.createdAt ASC")
    Optional<Conversation> findByIdAndMemberId(UUID id, Long memberId);
}
