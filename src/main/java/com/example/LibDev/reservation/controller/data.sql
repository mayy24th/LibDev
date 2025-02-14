SET FOREIGN_KEY_CHECKS = 0; -- 외래 키 제약 조건 비활성화

DROP TABLE IF EXISTS reservation;
DROP TABLE IF EXISTS book;
DROP TABLE IF EXISTS user;

SET FOREIGN_KEY_CHECKS = 1; -- 외래 키 제약 조건 활성화


CREATE TABLE user (
                      id BIGINT PRIMARY KEY AUTO_INCREMENT,            -- 엔티티의 id 필드와 일치
                      name VARCHAR(50) NOT NULL,
                      email VARCHAR(100) NOT NULL UNIQUE,
                      password VARCHAR(255) NOT NULL,
                      phone VARCHAR(20),
                      borrow_available BOOLEAN NOT NULL DEFAULT TRUE,  -- ✅ 대출 가능 여부 컬럼 추가
                      penalty_expiration TIMESTAMP NULL,               -- ✅ 패널티 만료일 추가
                      withdraw BOOLEAN NOT NULL DEFAULT FALSE,         -- ✅ 탈퇴 여부 컬럼 추가
                      role ENUM('ADMIN', 'USER') NOT NULL,             -- 엔티티의 role 필드와 일치
                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- BaseEntity의 createdAt과 매핑
                      updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP -- BaseEntity의 updatedAt과 매핑
);

CREATE TABLE topic (
                       topic_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       topic_name VARCHAR(255) NOT NULL
);


CREATE TABLE book (
                      book_id BIGINT PRIMARY KEY AUTO_INCREMENT,  -- 엔티티의 bookId와 매핑
                      topic_id BIGINT NOT NULL,                   -- 주제 ID (외래키)
                      title VARCHAR(255) NOT NULL,                -- 책 제목
                      author VARCHAR(100) NOT NULL,               -- 저자 이름
                      publisher VARCHAR(100) NOT NULL,            -- 출판사
                      published_date DATE,                         -- 출판 날짜
                      isbn VARCHAR(50) UNIQUE,                    -- ISBN 코드
                      contents TEXT,                               -- 책 내용 요약
                      is_available BOOLEAN NOT NULL DEFAULT TRUE, -- 대출 가능 여부
                      call_number VARCHAR(50),                    -- 청구 기호
                      thumbnail VARCHAR(500),                     -- 책 썸네일 이미지 URL
                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                      updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                      FOREIGN KEY (topic_id) REFERENCES topic(topic_id) -- topic 테이블과 연결
);


CREATE TABLE reservation (
                             id BIGINT PRIMARY KEY AUTO_INCREMENT,               -- 기본 키, 엔티티의 id 필드와 매핑
                             user_id BIGINT NOT NULL,                            -- 사용자 ID (외래 키)
                             book_id BIGINT NOT NULL,                            -- 도서 ID (외래 키)
                             status ENUM('WAITING', 'READY', 'CANCELLED') NOT NULL, -- 예약 상태 (ENUM 타입)
                             reserved_date DATE NOT NULL,                        -- 예약 날짜
                             expiration_date DATE,                               -- 만료 날짜
                             queue_order INT NOT NULL,                           -- 대기 순번
                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,     -- 생성 시간 (BaseEntity와 매핑)
                             updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 수정 시간 (BaseEntity와 매핑)
                             FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,  -- 외래 키 제약 조건
                             FOREIGN KEY (book_id) REFERENCES book(book_id) ON DELETE CASCADE  -- 외래 키 제약 조건
);


INSERT INTO user (id, name, email, password, phone, borrow_available, penalty_expiration, withdraw, role, created_at, updated_at)
VALUES
    (1, '김철수', 'chulsu@example.com', 'password123', '010-1234-5678', TRUE, NULL, FALSE, 'USER', NOW(), NOW()),
    (2, '이영희', 'younghee@example.com', 'password456', '010-5678-1234', TRUE, NULL, FALSE, 'USER', NOW(), NOW()),
    (3, '박민수', 'minsu@example.com', 'password789', '010-2345-6789', FALSE, '2025-02-20 00:00:00', FALSE, 'USER', NOW(), NOW()),  -- 패널티 적용
    (4, '정하나', 'hana@example.com', 'securepass', '010-8765-4321', TRUE, NULL, FALSE, 'USER', NOW(), NOW()),
    (5, '오세훈', 'sehoon@example.com', 'pass54321', '010-1111-2222', TRUE, NULL, FALSE, 'USER', NOW(), NOW()),
    (6, '최은비', 'eunbi@example.com', 'ilovejava', '010-2222-3333', FALSE, '2025-02-18 00:00:00', FALSE, 'USER', NOW(), NOW()), -- 패널티 적용
    (7, '이강민', 'kangmin@example.com', 'springboot', '010-3333-4444', TRUE, NULL, FALSE, 'USER', NOW(), NOW()),
    (8, '배윤아', 'youna@example.com', 'mypassword', '010-4444-5555', TRUE, NULL, FALSE, 'USER', NOW(), NOW()),
    (9, '홍길동', 'hong@example.com', 'supersecure', '010-5555-6666', TRUE, NULL, FALSE, 'ADMIN', NOW(), NOW()),  -- 관리자 계정 추가
    (10, '손흥민', 'heungmin@example.com', 'football', '010-6666-7777', TRUE, NULL, FALSE, 'USER', NOW(), NOW());

INSERT INTO topic (topic_id, topic_name) VALUES
                                             (1, '객체지향'),
                                             (2, '소프트웨어 개발');


INSERT INTO book (book_id, topic_id, title, author, publisher, published_date, isbn, contents, is_available, call_number, thumbnail, created_at, updated_at)
VALUES
    (1, 1, '객체지향의 사실과 오해', '조영호', '위키북스', '2015-07-01', '9788998139766', '객체지향 설계에 대한 개념을 쉽게 설명한 책', TRUE, 'QA76.9.O35 조영호', 'https://example.com/image1.jpg', NOW(), NOW()),
    (2, 2, '클린 코드', '로버트 C. 마틴', '인사이트', '2013-12-24', '9788966260959', '소프트웨어 개발자가 반드시 읽어야 할 책', TRUE, 'QA76.76.M42 마틴', 'https://example.com/image2.jpg', NOW(), NOW());

/*INSERT INTO reservation (id, user_id, book_id, status, reserved_date, expiration_date, queue_order, created_at, updated_at)
VALUES
    (1, 1, 1, 'WAITING', NOW(), DATE_ADD(NOW(), INTERVAL 3 DAY), 1, NOW(), NOW()),
    (2, 2, 2, 'READY', NOW(), DATE_ADD(NOW(), INTERVAL 3 DAY), 2, NOW(), NOW());
*/