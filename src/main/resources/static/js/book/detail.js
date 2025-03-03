import { attachReservationEvent, updateReservationCount } from "../reservation/reservation.js";

document.addEventListener("DOMContentLoaded", async function () {
    const bookInfo = document.querySelector(".book-detail-info");
    if (!bookInfo) {
        return;
    }

    const bookId = document.querySelector(".btn.btn-custom-1")?.dataset.bookId;
    if (!bookId) {
        console.error("책 정보를 찾을 수 없습니다.");
        return;
    }

    // 페이지 로드 시 도서 정보 업데이트
    await fetchBookDetails(bookId);

    // 예약자 수 조회
    await updateReservationCount(bookId);

    // 예약 버튼 이벤트 연결
    attachReservationEvent();

    // 한줄평 버튼
    const reviewBtn = document.getElementById("reviewBtn");
    if (reviewBtn) {
        reviewBtn.addEventListener("click", function () {
            location.href = `/review/book/${bookId}`;
        });
    }

    // 목록 버튼
    const backToListBtn = document.getElementById("backToListBtn");
    if (backToListBtn) {
        backToListBtn.addEventListener("click", function () {
            location.href = "/books/search-simple";
        });
    }
});

// 도서 세부 정보 fetch
export async function fetchBookDetails(bookId) {
    if (!bookId) {
        console.error("fetchBookDetails 호출 실패: bookId가 없음.");
        return;
    }

    try {
        const response = await fetch(`/api/v1/books/${bookId}`, {
            method: "GET",
            credentials: "include",
            headers: { "Content-Type": "application/json" }
        });

        if (!response.ok) throw new Error("도서 정보를 불러오는 데 실패했습니다.");

        const book = await response.json();

        document.getElementById("bookStatus").innerHTML = book.isAvailable
            ? "대출가능<br>[비치중]"
            : `대출불가<br>[대출중]<br>(예약: <span class="reservation-count">${book.reservationCount || 0}명</span>)`;
        document.getElementById("reservationStatusButton").textContent = book.isAvailable ? "예약불가" : "예약가능";
        document.getElementById("reservationStatusButton").disabled = book.isAvailable;
    } catch (error) {
        console.error("도서 정보 업데이트 중 오류 발생:", error);
    }
}


