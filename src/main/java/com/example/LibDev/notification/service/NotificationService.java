package com.example.LibDev.notification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendReservationNotification(Long userId, String message) {
        // 특정 사용자에게 알림 전송

        /*public void sendReservationNotification(Long userId, String message) {
            Map<String, Object> notification = new HashMap<>();
            notification.put("userId", userId);
            notification.put("message", message);    */

        messagingTemplate.convertAndSend("/topic/reservations/" + userId, message);
    }
}
