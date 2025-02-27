import { showAlertToast } from "../utils/showAlertToast.js";
import { formatDate } from "./utils.js";
import { fetchBookDetails } from "../book/detail.js";
import {checkLoginStatus} from "../utils/auth.js";

document.addEventListener("DOMContentLoaded", () => {
    fetchReservations();
});

export async function fetchReservations() {
    const isLoggedIn = await checkLoginStatus();
    if (!isLoggedIn) return;

    try {
        const response = await fetch(`/api/v1/reservations`, {
            method: "GET",
        });
        const data = await response.json();

        if (!response.ok) {
            showAlertToast(data.message);
            return;
        }

        renderReservations(data);
    } catch (error) {
        console.error('Error:', error);
    }
}

function renderReservations(reservations) {
    const reservationContainer = document.querySelector(".reservation-list");
    reservationContainer.innerHTML = "";

    if (!reservations || reservations.length === 0) {
        const emptyMessage = document.createElement("p");
        emptyMessage.textContent = "현재 예약 중인 도서가 없습니다.";
        emptyMessage.classList.add("blank-message");
        reservationContainer.appendChild(emptyMessage);
        return;
    }

    reservations.forEach(reservation => {
        const reservationBox = document.createElement("div");
        reservationBox.classList.add("reservation-box");

        // 제목과 예약 정보를 포함하는 컨테이너 (세로 정렬)
        const reservationContentWrapper = document.createElement("div");
        reservationContentWrapper.classList.add("reservation-content-wrapper");

        // 도서 제목
        const bookTitle = document.createElement("p");
        bookTitle.classList.add("book-title");
        bookTitle.textContent = reservation.bookTitle;

        // 예약 정보 전체 컨테이너 (좌측 정렬)
        const reservationInfoContainer = document.createElement("div");
        reservationInfoContainer.classList.add("reservation-info-container");

        // 예약일 컨테이너
        const reservedDateContainer = document.createElement("div");
        reservedDateContainer.classList.add("reservation-item");
        reservedDateContainer.textContent = `예약일: ${formatDate(reservation.reservedDate)}`;

        // 만료일 컨테이너
        const expirationDateContainer = document.createElement("div");
        expirationDateContainer.classList.add("reservation-item");
        expirationDateContainer.textContent = `만료일: ${reservation.expirationDate ? formatDate(reservation.expirationDate) : "-"}`;

        // 대기 순번 컨테이너
        const queueOrderContainer = document.createElement("div");
        queueOrderContainer.classList.add("reservation-item");
        queueOrderContainer.textContent = `대기 순번: ${reservation.totalQueueSize}명 중 ${reservation.queueOrder}번째`;

        // 상태 컨테이너 추가
        const statusContainer = document.createElement("div");
        statusContainer.classList.add("reservation-item", "reservation-status");
        statusContainer.textContent = `상태: ${reservation.status}`;

        // 컨테이너 간격 유지
        reservationInfoContainer.appendChild(reservedDateContainer);
        reservationInfoContainer.appendChild(expirationDateContainer);
        reservationInfoContainer.appendChild(queueOrderContainer);
        reservationInfoContainer.appendChild(statusContainer);

        // 버튼 컨테이너 (우측 정렬)
        const buttonContainer = document.createElement("div");
        buttonContainer.classList.add("buttons");

        // 대출하기 버튼 (1순위 + 대출 가능 상태일 때만 표시)
        if (reservation.canBorrow) {
            const borrowButton = document.createElement("button");
            borrowButton.classList.add("btn", "borrow-btn");
            borrowButton.textContent = "대출하기";
            borrowButton.dataset.bookId = reservation.bookId;
            borrowButton.addEventListener("click", () => borrowBook(reservation.bookId, reservation.reservationId, borrowButton));
            buttonContainer.appendChild(borrowButton);
        }

        // 취소 버튼
        const cancelButton = document.createElement("button");
        cancelButton.classList.add("btn", "cancel-btn");
        cancelButton.textContent = "취소";
        cancelButton.dataset.id = reservation.reservationId;
        cancelButton.addEventListener("click", () => cancelReservation(reservation.reservationId));
        buttonContainer.appendChild(cancelButton);

        // 예약 제목 + 예약 정보 컨테이너를 세로 정렬 컨테이너에 추가
        reservationContentWrapper.appendChild(bookTitle);
        reservationContentWrapper.appendChild(reservationInfoContainer);

        // 예약 박스에 컨텐츠 추가
        reservationBox.appendChild(reservationContentWrapper);
        reservationBox.appendChild(buttonContainer);

        // 최종적으로 컨테이너에 추가
        reservationContainer.appendChild(reservationBox);
    });
}

async function borrowBook(bookId, reservationId, borrowButton) {
    try {
        const response = await fetch(`/api/v1/borrow?bookId=${bookId}`, {
            method: "POST",
            credentials: "include", // 인증 정보 포함
            headers: {
                "Content-Type": "application/json",
            },
        });

        if (!response.ok) {
            const data = await response.json();
            showAlertToast(data.message);
            return;
        }

        showAlertToast("해당 도서가 대출되었습니다.");

        // "대출하기" 버튼 제거
        removeBorrowBtn(borrowButton);

        // 대출된 도서 정보 갱신
        await fetchBookDetails(bookId);
    } catch (error) {
        console.error("대출 요청 중 오류:", error);
        showAlertToast("대출 요청 중 오류가 발생했습니다.");
    }

    // 예약 취소 처리
    try {
        const response = await fetch(`/api/v1/reservations/${reservationId}`, {
            method: "DELETE",
        });

        if (!response.ok) {
            throw new Error(`예약 취소 실패: ${response.status}`);
        }
    } catch (error) {
        console.error("예약 취소 중 오류 발생:", error);
    }
}


/**
 * "대출하기" 버튼 제거 함수
 */
function removeBorrowBtn(borrowButton) {
    if (!borrowButton) return;
    borrowButton.remove();
}



