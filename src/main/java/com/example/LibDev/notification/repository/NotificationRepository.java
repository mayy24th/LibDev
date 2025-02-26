package com.example.LibDev.notification.repository;

import com.example.LibDev.notification.entity.Notification;
import com.example.LibDev.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // 특정 사용자에게 해당하는 알림 조회
    List<Notification> findByUser(User user);
}
