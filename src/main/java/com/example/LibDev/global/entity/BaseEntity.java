package com.example.LibDev.global.entity;


import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PreUpdate;
import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {
    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt; // 생성 시간

    @CreatedDate
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt; // 수정 시간

    // 수정 시간 갱신 메서드
    @PreUpdate
    public void setUpdatedAt(){this.updatedAt = LocalDateTime.now();}
}
