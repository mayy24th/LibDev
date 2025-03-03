package com.example.LibDev.notification.entity;

import com.example.LibDev.global.entity.BaseEntity;
import com.example.LibDev.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE) // 빌더 패턴 적용
@Builder
@Table(name = "notification")
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 500)
    private String message;

    public static Notification create(User user, String message) {
        return Notification.builder()
                .user(user)
                .message(message)
                .build();
    }
}

