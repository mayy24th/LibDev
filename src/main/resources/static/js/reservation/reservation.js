import { showAlertToast } from "/js/utils/utils.js";

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
        return data.userId; // API에서 userId 반환한다고 가정
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

        alert(`"${data.book.title}" 도서의 예약이 완료되었습니다!`);
        showAlertToast(`"${data.book.title}" 도서의 예약이 완료되었습니다!`);
    } catch (error) {
        showAlertToast(error.message);
    }
}

