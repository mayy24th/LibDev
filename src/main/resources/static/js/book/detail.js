import { createReservation } from "/js/reservation/reservation.js";

document.addEventListener("DOMContentLoaded", function () {
    const bookInfo = document.querySelector(".book-detail-info");
    if (!bookInfo) {
        alert("도서 정보를 로드할 수 없습니다.");
        return;
    }

    // 예약 버튼 가져오기
    const reserveButton = document.querySelector(".btn.btn-custom-1");
    if (!reserveButton) {
        console.error("예약 버튼을 찾을 수 없습니다.");
        return;
    }

    const bookId = reserveButton.getAttribute("data-book-id");
    if (!bookId) {
        console.error("책 정보를 찾을 수 없습니다.");
        return;
    }

    // 예약 버튼 클릭 시 예약 함수 호출
    reserveButton.addEventListener("click", () => createReservation(bookId));
});

