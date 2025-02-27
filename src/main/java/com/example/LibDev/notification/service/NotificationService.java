package com.example.LibDev.notification.service;

import com.example.LibDev.notification.dto.NotificationResponseDto;
import com.example.LibDev.notification.entity.Notification;
import com.example.LibDev.notification.repository.NotificationRepository;
import com.example.LibDev.user.entity.User;
import com.example.LibDev.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    //알림 저장 + 실시간 전송
    @Transactional
    public void sendReservationNotification(Long userId, String message) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // DB에 알림 저장
        Notification notification = Notification.create(user, message);
        notificationRepository.save(notification);

        // 웹소켓을 통해 실시간 알림 전송
        String destination = "/topic/reservations/" + userId;
        Map<String, Object> notificationData = new HashMap<>();
        notificationData.put("userId", userId);
        notificationData.put("message", message);

        messagingTemplate.convertAndSend(destination, notificationData);
    }

    // 읽지 않은 알림 가져오기
    @Transactional(readOnly = true)
    public List<NotificationResponseDto> getUnreadNotifications(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 엔티티 리스트를 바로 DTO 리스트로 변환 후 반환
        return notificationRepository.findByUser(user).stream()
                .map(NotificationResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 알림 삭제 (X 버튼 클릭 시)
    @Transactional
    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }
}

