package com.raf.repository;

import com.raf.entity.Message;
import com.raf.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {


@Query("SELECT DISTINCT m FROM Message m " +
"LEFT JOIN FETCH m.sender " +
"LEFT JOIN FETCH m.receiver " +
"LEFT JOIN FETCH m.relatedInventory " +
"WHERE (m.sender = :user1 AND m.receiver = :user2) OR (m.sender = :user2 AND m.receiver = :user1) " +
"ORDER BY m.createdAt ASC")
List<Message> findConversationBetweenUsers(@Param("user1") User user1, @Param("user2") User user2);


@Query("SELECT DISTINCT m FROM Message m " +
"LEFT JOIN FETCH m.sender " +
"LEFT JOIN FETCH m.receiver " +
"LEFT JOIN FETCH m.relatedInventory " +
"WHERE m.sender = :user OR m.receiver = :user " +
"ORDER BY m.createdAt DESC")
Page<Message> findMessagesForUser(@Param("user") User user, Pageable pageable);


@Query("SELECT DISTINCT m FROM Message m " +
"LEFT JOIN FETCH m.sender " +
"LEFT JOIN FETCH m.receiver " +
"LEFT JOIN FETCH m.relatedInventory " +
"WHERE m.receiver = :user AND m.isRead = false " +
"ORDER BY m.createdAt DESC")
List<Message> findUnreadMessagesForUser(@Param("user") User user);


Optional<Message> findByMessageCode(String messageCode);


@Query("SELECT COUNT(m) FROM Message m WHERE m.receiver = :user AND m.isRead = false")
Long countUnreadMessagesForUser(@Param("user") User user);


List<Message> findByRelatedInventoryId(Long inventoryId);
}

