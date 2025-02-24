import { showAlertToast } from "/js/utils/utils.js";

export function attachReservationEvent() {
    const reserveButtons = document.querySelectorAll(".btn.btn-custom-1.reserve-btn");

    reserveButtons.forEach((button) => {
        button.removeEventListener("click", handleReservationClick); // 기존 이벤트 제거
        button.addEventListener("click", handleReservationClick);
    });
}

// 클릭 이벤트 핸들러 함수 분리
async function handleReservationClick(event) {
    const bookId = event.target.dataset.bookId;
    await createReservation(bookId);
}

// MutationObserver를 이용하여 동적으로 생성된 예약 버튼에 이벤트 추가
const observer = new MutationObserver(() => {
    attachReservationEvent(); // 새로 추가된 예약 버튼에도 이벤트를 부착
});
observer.observe(document.body, { childList: true, subtree: true });

// 페이지 로딩 시 기본 예약 버튼 이벤트 부착
document.addEventListener("DOMContentLoaded", () => {
    attachReservationEvent();
});



// 로그인된 사용자 ID 가져오기
export async function fetchUserId() {
    try {
        const response = await fetch("/api/v1/auths/me", {
            method: "GET",
            credentials: "include",
            headers: {
                "Content-Type": "application/json"
            }
        });

        if (!response.ok) {
            throw new Error("사용자 정보를 가져오지 못했습니다.");
        }

        const data = await response.json();
        return data.userId;
    } catch (error) {
        console.error("사용자 ID 조회 실패:", error);
        showAlertToast("로그인이 필요합니다.");
        return null;
    }
}

// 예약하기 함수
export async function createReservation(bookId) {
    const userId = await fetchUserId();
    if (!userId) {
        showAlertToast("로그인이 필요합니다.");
        return;
    }

    try {
        const response = await fetch("/api/v1/reservations", {
            method: "POST",
            credentials: "include",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ userId, bookId })
        });

        const data = await response.json();

        if (!response.ok) {
            throw new Error(data.message || `${response.status} ${response.statusText}`);
        }

        showAlertToast(`"${data.book.title}" 도서의 예약이 완료되었습니다!`);
        await updateReservationCount(bookId);
    } catch (error) {
        showAlertToast(error.message);
    }
}

// 예약자 수 조회 및 UI 업데이트 함수
export async function updateReservationCount(bookId) {
    try {
        const response = await fetch(`/api/v1/reservations/count/${bookId}`);
        if (!response.ok) throw new Error("예약자 수를 불러오는 데 실패했습니다.");

        const reservationCount = await response.json();
        const reservationCountElement = document.querySelector(".reservation-count");

        if (reservationCountElement) {
            reservationCountElement.textContent = `${reservationCount}명`;
        }
    } catch (error) {
        console.error("예약자 수 조회 중 오류 발생:", error);
    }
}

