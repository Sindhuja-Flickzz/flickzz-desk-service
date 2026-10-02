package com.flickzz.desk.repo;

import com.flickzz.desk.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findAllByRecipientUserIdAndIsActiveTrueOrderByNotificationIdDesc(Long recipientId);

    List<Notification> findAllByRecipientUserIdAndIsActiveTrueAndIsReadTrueOrderByNotificationIdDesc(Long recipientId);
}
