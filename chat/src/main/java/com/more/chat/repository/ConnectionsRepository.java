package com.more.chat.repository;

import com.more.chat.entity.Connections;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConnectionsRepository extends JpaRepository<Connections, Long> {

    List<Connections> findBySenderIdAndStatus(Long senderId, String status);

    List<Connections> findByRecipientIdAndStatus(Long recipientId, String status);

    @Query("SELECT c FROM Connections c WHERE c.sender.id = :userId1 AND c.recipient.id = :userId2 AND c.status = :status")
    Optional<Connections> findBySenderIdAndRecipientIdAndStatus(@Param("userId1") Long userId1, @Param("userId2") Long userId2, @Param("status") String status);

    @Query("SELECT c FROM Connections c WHERE c.sender.id = :userId2 AND c.recipient.id = :userId1 AND c.status = :status")
    Optional<Connections> findByRecipientIdAndSenderIdAndStatus(@Param("userId2") Long userId2, @Param("userId1") Long userId1, @Param("status") String status);

    @Query("SELECT c FROM Connections c WHERE (c.sender.id = :userId OR c.recipient.id = :userId) AND c.status = :status")
    List<Connections> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") String status);

}
