import { formatDate, statusText, statusColor } from "./utils.js";
import { renderPagination } from "./renderPagination.js";
import { cancelReservation } from "./cancelReservation.js";

document.addEventListener("DOMContentLoaded", async () => {
    loadReservationList(0);
});

async function loadReservationList(page) {
    try {
        const response = await fetch(`/api/v1/reservations?page=${page}`);

        if (!response.ok) {
            console.error(`예약 내역을 불러오는 데 실패했습니다. 상태 코드: ${response.status}`);
            return [];
        }
        const data = await response.json();


        if (Array.isArray(data)) {
            displayReservationList(data, page);
        } else if (data.content) {
            displayReservationList(data.content, page);
        } else {
            console.error("데이터 구조가 예상과 다릅니다.", data);
        }

        renderPagination(data.totalPages || 1, data.number || 0, loadReservationList);
    } catch (error) {
        console.error(error.message);
    }
}

function displayReservationList(reservationList, page) {
    const reservationListContainer = document.querySelector(".admin-reservation-list");
    reservationListContainer.innerHTML = ""; // 기존 내용 초기화

    reservationList.forEach((reservation, index) => {
        const reservationItem = document.createElement("tr");

        const reservationNumber = document.createElement("td");
        reservationNumber.textContent = page * 10 + index + 1;

        const bookTitle = document.createElement("td");
        bookTitle.textContent = reservation.bookTitle;

        const userEmail = document.createElement("td");
        userEmail.textContent = reservation.userEmail || "-";

        const reservedDate = document.createElement("td");
        reservedDate.textContent = formatDate(reservation.reservedDate);

        const expirationDate = document.createElement("td");
        expirationDate.textContent = formatDate(reservation.expirationDate);

        const queueOrder = document.createElement("td");
        queueOrder.textContent = `${reservation.totalQueueSize}명 중 ${reservation.queueOrder}번째`;

        const reservationStatus = document.createElement("td");
        reservationStatus.textContent = statusText(reservation.status);
        reservationStatus.style.color = statusColor(reservationStatus.textContent);

        const cancelBtn = document.createElement("td");

        const btn = document.createElement("button");
        btn.textContent = "예약 취소";
        btn.classList.add("btn", "cancel-reservation-btn");
        btn.addEventListener("click", () => cancelReservation(reservation.reservationId));
        cancelBtn.appendChild(btn);

        reservationItem.appendChild(reservationNumber);
        reservationItem.appendChild(bookTitle);
        reservationItem.appendChild(userEmail);
        reservationItem.appendChild(reservedDate);
        reservationItem.appendChild(expirationDate);
        reservationItem.appendChild(queueOrder);
        reservationItem.appendChild(reservationStatus);
        reservationItem.appendChild(cancelBtn);

        reservationListContainer.appendChild(reservationItem);
    });
}
