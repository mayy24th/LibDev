package com.example.LibDev.notification.controller;

import com.example.LibDev.notification.dto.NotificationResponseDto;
import com.example.LibDev.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @MessageMapping("/sendNotification")
    @SendTo("/topic/notifications")
    public NotificationResponseDto sendNotification(NotificationResponseDto message) {
        return message;
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<?> deleteNotification(@PathVariable Long notificationId) {
        notificationService.deleteNotification(notificationId);
        return ResponseEntity.ok("알림이 삭제되었습니다.");
    }

    // 읽지 않은 알림 조회 API
    @GetMapping("/unread/{userId}")
    public ResponseEntity<List<NotificationResponseDto>> getUnreadNotifications(@PathVariable Long userId) {
        List<NotificationResponseDto> unreadNotifications = notificationService.getUnreadNotifications(userId);
        return ResponseEntity.ok(unreadNotifications);
    }
}
