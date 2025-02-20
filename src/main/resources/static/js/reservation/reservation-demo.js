import { showAlertToast } from "/js/utils/utils.js";

async function createReservation() {
    const userId = document.getElementById('userId').value;
    const bookId = document.getElementById('bookId').value;

    if (!userId || !bookId) {
        alert("사용자 ID 또는 도서 ID가 입력되지 않았습니다.");
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
            throw new Error(data.message ||`${response.status} ${response.statusText}`);
        }
        showAlertToast(`"${data.book.title}" 도서의 예약이 완료되었습니다!`);
    } catch (error) {
        showAlertToast(error.message);
    }
}

window.createReservation = createReservation;