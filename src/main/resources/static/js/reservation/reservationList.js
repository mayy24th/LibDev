document.addEventListener("DOMContentLoaded", () => {
    fetchReservations();
});

async function fetchReservations() {
    try {
        const userId = 1;  // 실제 로그인된 사용자의 ID를 가져와야 함
        const response = await fetch(`/api/v1/reservations?userId=${userId}`, {
            method: "GET",
            credentials: "include",
            headers: {
                "Content-Type": "application/json"
            }
        });

        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }

        const data = await response.json();
        console.log("예약 내역 응답:", data);

        const reservationList = document.getElementById("reservationList");
        reservationList.innerHTML = "";

        if (!Array.isArray(data)) {
            console.error("API 응답이 배열이 아닙니다:", data);
            return;
        }

        data.forEach(reservation => {
            const row = document.createElement("tr");
            row.innerHTML = `
                <td>${reservation.reservationId}</td>
                <td>${reservation.bookTitle}</td>
                <td>${reservation.author}</td>
                <td>${convertStatus(reservation.status)}</td>
                <td>${formatDate(reservation.reservedDate)}</td>
                <td>${reservation.expirationDate ? formatDate(reservation.expirationDate) : "-"}</td>
                <td>${reservation.totalQueueSize}명 중 ${reservation.queueOrder}번째</td>
                <td>
                    <button class="btn btn-outline-dark btn-sm cancel-btn" data-id="${reservation.reservationId}">취소</button>
                </td>
            `;
            reservationList.appendChild(row);
        });


        // 취소 버튼 이벤트 할당
        document.querySelectorAll(".cancel-btn").forEach(button => {
            button.addEventListener("click", () => cancelReservation(button.dataset.id));
        });

    } catch (error) {
        console.error("예약 내역을 불러오는 중 오류 발생:", error);
    }
}

function convertStatus(status) {
    const statusMap = {
        "WAITING": "대기중",
        "READY": "예약 확정",
        "CANCELLED": "취소됨"
    };
    return statusMap[status] || status;
}

function formatDate(isoString) {
    const date = new Date(isoString);
    const year = date.getFullYear();
    const month = date.getMonth() + 1;
    const day = date.getDate();
    const hours = date.getHours();
    const minutes = date.getMinutes();

    const period = hours >= 12 ? "오후" : "오전";
    const formattedHours = hours % 12 || 12; // 12시간제로 변환

    return `${year}년 ${month}월 ${day}일 ${period} ${formattedHours}시 ${minutes}분`;
}


async function cancelReservation(reservationId) {
    try {
        const response = await fetch(`/api/v1/reservations/${reservationId}`, {
            method: "DELETE",
            credentials: "include"
        });

        if (!response.ok) {
            throw new Error(`예약 취소 실패: ${response.status}`);
        }

        const message = await response.text();
        alert(message);
        fetchReservations();  // 취소 후 예약 목록 갱신

    } catch (error) {
        alert("예약 취소 실패: " + error.message);
    }
}
