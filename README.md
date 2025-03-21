# LibDev

![header](https://capsule-render.vercel.app/api?type=waving&color=gradient&height=300&section=header&text=LibDev&fontSize=90&fontAlignY=40&desc=Elice%20Cloud%20Track%205기&descAlign=70)

## 💬 설명

> 도서 데이터 및 추천 알고리즘을 활용한 도서관 대출 서비스 LibDev입니다.
<br>

## ⚙️ 기술 스택

### 백엔드

<img src="https://img.shields.io/badge/Spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white">
<img src="https://img.shields.io/badge/SpringBoot-6DB33F?style=for-the-badge&logo=spring&logoColor=white">
<img src="https://img.shields.io/badge/springsecurity-6DB33F?style=for-the-badge&logo=spring&logoColor=white">
<img src="https://img.shields.io/badge/Spring%20Data%20JPA-6DB33F?style=for-the-badge&logo=spring&logoColor=white">
<img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white">
<img src="https://img.shields.io/badge/OAUTH2-4285F4?style=for-the-badge&logo=google&logoColor=white">
<img src="https://img.shields.io/badge/JWT-003545?style=for-the-badge&logo=jsonwebtokens&logoColor=white">
<img src="https://img.shields.io/badge/REDIS-FF4438?style=for-the-badge&logo=redis&logoColor=white">
<img src="https://img.shields.io/badge/WEB SOCKET-F3702A?style=for-the-badge&logoColor=white">
<img src="https://img.shields.io/badge/nginx-%23009639?style=for-the-badge&logo=nginx&logoColor=white">


### 프론트엔드

<img src="https://img.shields.io/badge/HTML-239120?style=for-the-badge&logo=html5&logoColor=white">
<img src="https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=JavaScript&logoColor=white">
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
### **한줄평 Review**

- **한줄평 작성**
    - 로그인한 유저만 작성 가능
    - 도서 별 한줄평 페이지에서만 작성 가능
- **한줄평 수정**
    - 본인이 작성한 한줄평만 수정 가능
    - 관리자(ADMIN) 권한을 가진 경우 모든 한줄평 수정 가능
- **한줄평 삭제**
    - 본인이 작성한 한줄평만 삭제 가능
    - 관리자(ADMIN) 권한을 가진 경우 모든 한줄평 삭제 가능
- **한줄평 조회**
    - 전체 한줄평, 도서 별 한줄평은 로그인 없이 조회 가능
    - 본인이 작성한 한줄평은 로그인 후 마이페이지를 통해 조회 가능
- **비속어 필터링**
    - Profanity Filter API를 활용하여 필터링 수행
        - https://github.com/Whale0928/profanity-filter-api
    - 한줄평 작성 및 수정 시 비속어 포함 여부 검사
    - 비속어가 포함된 경우 등록/수정 불가

### **도서 추천 Recommendation**

- **유사 도서 추천**
    - 특정 도서의 상세 페이지에서 해당 도서와 유사한 도서를 추천
- **사용자 기반 추천**
    - 사용자의 대출 내역을 분석하여 적합한 도서를 추천
    - 사용자가 한 번도 대출한 적이 없는 경우, **대출 횟수가 높은 인기 도서**를 추천
    - 새로운 대출 정보가 생길 경우 캐시 삭제
- **인기 도서 추천**
- **Redis를 활용하여 추천 결과를 캐싱하여 성능 최적화**
    - 동일한 도서 또는 사용자가 반복적으로 요청할 경우 DB를 직접 조회하지 않고 캐시된 데이터를 반환
