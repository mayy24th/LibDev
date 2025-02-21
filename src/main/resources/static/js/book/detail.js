import { createReservation } from "/js/reservation/reservation.js";

document.addEventListener("DOMContentLoaded", function () {
    const bookInfo = document.querySelector(".book-detail-info");
    if (!bookInfo) {
        alert("도서 정보를 로드할 수 없습니다.");
        return;
    }

    // 예약자 수 조회 및 UI 업데이트 함수
    async function updateReservationCount() {
        try {
            const response = await fetch(`/api/v1/reservations/count/${bookId}`);
            if (!response.ok) throw new Error("예약자 수를 불러오는 데 실패했습니다.");

            const reservationCount = await response.json();
            document.querySelector(".reservation-count").textContent = `${reservationCount}명`;
        } catch (error) {
            console.error("예약자 수 조회 중 오류 발생:", error);
        }
    }

    // 페이지 로드 시 예약자 수 조회
    await updateReservationCount();

    // 예약 버튼 가져오기
    const reserveButton = document.querySelector(".btn.btn-custom-1");
    if (!reserveButton) {
        console.error("예약 버튼을 찾을 수 없습니다.");
        return;
    }

    // 예약 버튼 클릭 시 예약 함수 호출 + 예약자 수 업데이트
    reserveButton.addEventListener("click", async () => {
        try {
            await createReservation(bookId);
            await updateReservationCount();
        } catch (error) {
            console.error("예약 중 오류 발생:", error);
        }
    });
});

    // 예약 버튼 클릭 시 예약 함수 호출
    reserveButton.addEventListener("click", () => createReservation(bookId));

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

