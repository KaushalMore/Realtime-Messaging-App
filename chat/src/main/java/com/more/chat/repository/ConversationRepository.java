package com.more.chat.repository;

import com.more.chat.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    @Query("SELECT c FROM Conversation c JOIN c.users u WHERE u.id = :userId ORDER BY c.lastMessageTimestamp DESC")
    List<Conversation> findConversationsByUserId(@Param("userId") Long userId);
}
