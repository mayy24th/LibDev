package com.example.LibDev.user.entity;

import com.example.LibDev.global.entity.BaseEntity;
import com.example.LibDev.user.entity.type.Role;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Entity
@Builder
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String password;

    private String name;

    private String phone;

    /*대출가능상태*/
    private boolean borrowAvailable;

    /*패널티만료일*/
    @Setter
    @Column(name = "penalty_expiration")
    private LocalDateTime penaltyExpiration;

    /*탈퇴여부*/
    private boolean withdraw;

    /*소셜 로그인 제공자*/
    private String provider;

    @Enumerated(EnumType.STRING)
    private Role role;

    public void update( String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public void updatePassword(String password) {this.password = password;}

    public void updateBorrowAvailable(Boolean borrowAvailable) {this.borrowAvailable = borrowAvailable;}

    public void updatePenaltyExpiration(LocalDateTime penaltyExpiration) {this.penaltyExpiration = penaltyExpiration;}

    public void updateRole(Role role) {this.role = role;}

    public void deleteUser(){
        this.email = null;
        this.password = null;
        this.name = "알수없음";
        this.phone = null;
        this.role = Role.DEL;
        this.withdraw = true;
    }

    public boolean isSocial(String provider){
        return !(provider == null);
    }

}
