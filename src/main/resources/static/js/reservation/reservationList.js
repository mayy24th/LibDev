import { showAlertToast } from "/js/utils/utils.js";

document.addEventListener("DOMContentLoaded", () => {
    fetchReservations();
});

async function fetchReservations() {
    try {
        const response = await fetch(`/api/v1/reservations`, {
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

        renderReservations(data);

    } catch (error) {
        console.error("예약 내역을 불러오는 중 오류 발생:", error);
    }
}

function renderReservations(reservations) {
    const reservationContainer = document.querySelector(".reservation-list");
    reservationContainer.innerHTML = ""; // 기존 목록 초기화

    if (!reservations || reservations.length === 0) {
        const emptyMessage = document.createElement("p");
        emptyMessage.textContent = "현재 예약 중인 도서가 없습니다.";
        reservationContainer.appendChild(emptyMessage);
        return;
    }

    reservations.forEach(reservation => {
        // 예약 박스 생성
        const reservationBox = document.createElement("div");
        reservationBox.classList.add("reservation-box");

        // 예약 내용 박스
        const contentBox = document.createElement("div");
        contentBox.classList.add("reservation-content-box");

        // 도서 제목
        const bookTitle = document.createElement("p");
        bookTitle.classList.add("book-title");
        bookTitle.textContent = reservation.bookTitle;

        // 예약 정보 박스
        const infoBox = document.createElement("div");
        infoBox.classList.add("reservation-info");

        // 개별 정보 항목 추가 (innerHTML 없이)
        const author = document.createElement("p");
        author.textContent = `저자: ${reservation.author}`;

        const status = document.createElement("p");
        status.textContent = `예약 상태: ${convertStatus(reservation.status)}`;

        const reservedDate = document.createElement("p");
        reservedDate.textContent = `예약일: ${formatDate(reservation.reservedDate)}`;

        const expirationDate = document.createElement("p");
        expirationDate.textContent = `만료일: ${reservation.expirationDate ? formatDate(reservation.expirationDate) : "-"}`;

        const queueOrder = document.createElement("p");
        queueOrder.textContent = `대기 순번: ${reservation.totalQueueSize}명 중 ${reservation.queueOrder}번째`;

        // infoBox 에 정보 추가
        infoBox.appendChild(author);
        infoBox.appendChild(status);
        infoBox.appendChild(reservedDate);
        infoBox.appendChild(expirationDate);
        infoBox.appendChild(queueOrder);

        // 버튼 컨테이너
        const buttonBox = document.createElement("div");
        buttonBox.classList.add("buttons");

        // 취소 버튼 생성
        const cancelButton = document.createElement("button");
        cancelButton.classList.add("btn", "cancel-btn");
        cancelButton.textContent = "취소";
        cancelButton.dataset.id = reservation.reservationId;
        cancelButton.addEventListener("click", () => cancelReservation(reservation.reservationId));

        // 요소 추가
        contentBox.appendChild(bookTitle);
        contentBox.appendChild(infoBox);
        buttonBox.appendChild(cancelButton);

        reservationBox.appendChild(contentBox);
        reservationBox.appendChild(buttonBox);

        reservationContainer.appendChild(reservationBox);
    });
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
    const formattedHours = hours % 12 || 12;

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

        await fetchReservations();

        showAlertToast("예약이 취소되었습니다.");
        /*showAlertToast(`"${data.book.title}" 도서의 예약이 취소되었습니다!`);*/


    } catch (error) {
        showAlertToast("예약 취소 실패: " + error.message);
    }
}
