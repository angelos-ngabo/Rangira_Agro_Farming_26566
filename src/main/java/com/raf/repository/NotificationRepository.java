package com.raf.repository;

import com.raf.entity.Notification;
import com.raf.enums.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

@Query("SELECT n FROM Notification n WHERE n.user.id = :userId")
List<Notification> findByUserId(@Param("userId") Long userId);

@Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.isRead = :isRead")
List<Notification> findByUserIdAndIsRead(@Param("userId") Long userId, @Param("isRead") Boolean isRead);

List<Notification> findByType(NotificationType type);

@Query("SELECT n FROM Notification n WHERE n.transaction.id = :transactionId")
List<Notification> findByTransactionId(@Param("transactionId") Long transactionId);

@Query("SELECT COUNT(n) FROM Notification n WHERE n.user.id = :userId AND n.isRead = false")
long countUnreadByUserId(@Param("userId") Long userId);

@Query("SELECT n FROM Notification n WHERE n.user.id = :userId ORDER BY n.createdAt DESC")
List<Notification> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

@Query("SELECT DISTINCT n FROM Notification n LEFT JOIN FETCH n.user WHERE n.user.id = :userId ORDER BY n.createdAt DESC")
List<Notification> findByUserIdOrderByCreatedAtDescWithUser(@Param("userId") Long userId);

@Query("SELECT DISTINCT n FROM Notification n LEFT JOIN FETCH n.user WHERE n.user.id = :userId AND n.isRead = false ORDER BY n.createdAt DESC")
List<Notification> findByUserIdAndIsReadFalseWithUser(@Param("userId") Long userId);
}

