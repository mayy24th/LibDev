package com.example.LibDev.user.service;

import com.example.LibDev.user.entity.User;
import com.example.LibDev.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserPenaltyScheduler {

    private final UserRepository userRepository;

    @Scheduled(cron = "0 0 0 * * *") // 매일 자정에 실행
    @Transactional
    public void resetPenaltyForUsers() {
        LocalDateTime now = LocalDateTime.now();
        List<User> penalizedUsers = userRepository.findByPenaltyExpirationBeforeAndBorrowAvailableFalse(now);

        for (User user : penalizedUsers) {
            user.updateBorrowAvailable(true);
            user.setPenaltyExpiration(null);
            log.info("패널티 해제: User ID {}의 대출 가능 상태가 변경되었습니다.", user.getId());
        }

        userRepository.saveAll(penalizedUsers);
    }
}
