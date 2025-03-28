# LibDev
<img width="900" height="300" alt="스크린샷 2025-02-27 오후 7 07 17" src="https://github.com/user-attachments/assets/caec0f03-420c-46a4-8b3a-c885fd8f80ba"/>

## 💬 설명

> 도서 데이터 및 추천 알고리즘을 활용한 도서관 대출 서비스 LibDev입니다.
<br>

## ⚙️ 기술 스택

### 백엔드

<img src="https://img.shields.io/badge/Spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white"> <img src="https://img.shields.io/badge/SpringBoot-6DB33F?style=for-the-badge&logo=spring&logoColor=white">
<img src="https://img.shields.io/badge/springsecurity-6DB33F?style=for-the-badge&logo=spring&logoColor=white">
<img src="https://img.shields.io/badge/Spring%20Data%20JPA-6DB33F?style=for-the-badge&logo=spring&logoColor=white">
<img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white">
<img src="https://img.shields.io/badge/OAUTH2-4285F4?style=for-the-badge&logo=google&logoColor=white">
<img src="https://img.shields.io/badge/JWT-003545?style=for-the-badge&logo=jsonwebtokens&logoColor=white">
<img src="https://img.shields.io/badge/REDIS-FF4438?style=for-the-badge&logo=redis&logoColor=white">
<img src="https://img.shields.io/badge/WEB SOCKET-F3702A?style=for-the-badge&logoColor=white">
<img src="https://img.shields.io/badge/nginx-%23009639?style=for-the-badge&logo=nginx&logoColor=white">


### 프론트엔드

<img src="https://img.shields.io/badge/HTML-239120?style=for-the-badge&logo=html5&logoColor=white"> <img src="https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=JavaScript&logoColor=white">
<img src="https://img.shields.io/badge/HTML5-E34F26?style=for-the-badge&logo=HTML5&logoColor=white"/>
<img src="https://img.shields.io/badge/css-1572B6?style=for-the-badge&logo=css3&logoColor=white">
<img src="https://img.shields.io/badge/Bootstrap-563D7C?style=for-the-badge&logo=bootstrap&logoColor=white">
<img src="https://img.shields.io/badge/thymeleaf-005F0F?style=for-the-badge&logo=thymeleaf&logoColor=white"/>

<br><br>

## 🧩 ERD

![LibDevERDERD](./assets/LibDevERD.png)
<br><br>

## ⛓️ 와이어 프레임

![LibDevWAF](./assets/LibDevWAF.png)
<br><br>

## 🔥 기능
### 📌 Review API 작성
```
review
├── controller
│   ├── ReviewAPIController.java
│   └── ReviewViewController.java
├── dto
│   └── ReviewDto.java
├── entity
│   └── Review.java
├── filter
│   └── ProfanityFilter.java
├── mapper
│   └── ReviewMapper.java
├── repository
│   └── ReviewRepository.java
└── service
    └── ReviewService.java
```
### 한줄평 작성/수정
| 항목 | 내용 |
| --- | --- |
| URL | `POST /api/review`  `PUT /api/review/{reviewId}` |
| 접근 조건 | 로그인 필요 |
| 권한 조건 | 작성자 본인 OR ADMIN |
| 위치 제한 | 도서 상세 페이지에서만 작성 가능 |
| 입력 제한 | 100자까지 입력 가능 |
| 비속어 필터링 | Profanity Filter API 사용, 비속어 포함 시 등록/수정 불가 |
| 이미지 | <img width="400" alt="스크린샷 2025-02-27 오후 7 08 06" src="https://github.com/user-attachments/assets/cabfe0ea-d332-418b-ae30-3a2ed8e26cff" /> <img width="400" alt="스크린샷 2025-02-27 오후 7 07 47" src="https://github.com/user-attachments/assets/6adb13b8-39eb-44d3-ba53-90e2fd56fa4a" /> <img width="400" alt="스크린샷 2025-02-27 오후 7 07 08" src="https://github.com/user-attachments/assets/981a9c1b-7b36-4e36-af02-ed2af6693c52" /> |

### 한줄평 삭제
| 항목 | 내용 |
| --- | --- |
| URL | `DELETE /api/review/{reviewId}` |
| 접근 조건 | 로그인 필요 |
| 권한 조건 | 작성자 본인 OR ADMIN |
| 이미지 | <img width="400" alt="스크린샷 2025-02-27 오후 7 07 17" src="https://github.com/user-attachments/assets/abed86ba-10aa-4850-b52d-79d452f56711" /> |

### 전체 한줄평 조회
| 항목 | 내용 |
| --- | --- |
| URL | `GET /api/review` |
| 접근 조건 | 로그인 불필요 |

#### 도서별 한줄평 조회
| 항목 | 내용 |
| --- | --- |
| URL | `GET /api/review/book/{bookId}` |
| 접근 조건 | 로그인 불필요 |

### 유저별 한줄평 조회
| 항목 | 내용 |
| --- | --- |
| URL | `GET /api/review/user` |
| 접근 조건 | 로그인한 유저만 가능 |

#
### 📌 Recommendation API 작성
```
recommendation
├── controller
│   ├── RecommendationAPIController.java
│   └── RecommendationViewController.java
├── dto
│   └── RecommendationResponseDto.java
├── mapper
│   ├── PopularBookMapper.java
│   ├── SimilarBookMapper.java
│   ├── UserActivityMapper.java
│   └── UserBaseBookMapper.java
├── service
│   ├── RecommendationCacheService.java
│   └── RecommendationService.java
├── strategy
│   ├── PopularBookRecommendation.java
│   ├── RecommendationStrategy.java
│   ├── SimilarBookRecommendation.java
│   └── UserBaseBookRecommendation.java
└── vo
    ├── RecommendedBookVO.java
    └── UserBorrowActivityVO.java
```

```
resources
└── mybatis
    └── mapper
        └── recommendation
            ├── PopularBookMapper.xml
            ├── SimilarBookMapper.xml
            ├── UserActivityMapper.xml
            └── UserBaseBookMapper.xml
```
            
### 유사 도서 추천
| 항목 | 내용 |
| --- | --- |
| URL | `GET /api/recommendation/similar/{bookId}` |
| 사용 위치 | 도서 상세 페이지 |
| 추천 로직 | 기준 요소별 가중치를 적용한 유사도 계산 |
| 성능 최적화 | Redis 캐시 사용 |
| 이미지 | <img width="400" alt="스크린샷 2025-02-27 오후 7 08 48" src="https://github.com/user-attachments/assets/22a5b31e-c459-4b34-8167-8bf029250b25" /> |

### 사용자 기반 도서 추천
| 항목 | 내용 |
| --- | --- |
| URL | `GET /api/recommendation/user` |
| 사용 위치 | 메인 페이지, 추천 도서 페이지 |
| 추천 로직 | 대출 이력 기반 (대출 이력이 없거나 비로그인 상태면 인기 도서 추천 |
| 성능 최적화 | Redis 캐시 사용 (실시간성이 중요하여 새로운 대출 내역 발생 시 캐시 데이터 삭제) |
| 이미지 | <img width="400" alt="스크린샷 2025-02-27 오후 7 08 39" src="https://github.com/user-attachments/assets/10bb6a8e-6e6e-4b8f-8274-844fcd74cbe7" /> |

### 인기 도서 추천
| 항목 | 내용 |
| --- | --- |
| URL | `GET /api/recommendation/popular` |
| 사용 위치 | 사용자 기반 추천과 동일 |
| 성능 최적화 | Redis 캐시 사용 |
| 이미지 | <img width="400" alt="스크린샷 2025-02-27 오후 7 09 00" src="https://github.com/user-attachments/assets/67c55574-d277-4cd7-9037-abb91fd05345" /> |

#
### 📌 공통 정책
### 비속어 필터링
| 항목 | 내용 |
| --- | --- |
| 사용 API | [Profanity Filter API](https://github.com/Whale0928/profanity-filter-api) |
| 적용 위치 | 한줄평 작성 및 수정 |
| 필터링 동작 | 비속어 포함 시 등록/수정 불가 |

### 권한 정책
| 사용자 유형 | 권한 |
| --- | --- |
| USER | 본인 작성 리뷰만 수정/삭제 가능 |
| ADMIN | 전체 리뷰 수정/삭제 가능 |
