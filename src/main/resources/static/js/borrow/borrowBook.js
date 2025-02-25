import { fetchBookDetails } from "/js/book/detail.js";
import { attachReservationEvent } from "/js/reservation/reservation.js";
import { showAlertToast } from "../utils/showAlertToast.js";

const borrowButton = document.querySelector(".borrow-btn");

if (borrowButton) {
    borrowButton.addEventListener("click", function () {
        const bookId = this.dataset.bookId;
        borrowBook(bookId);
    });
}

async function borrowBook(bookId) {
    try {
        const response = await fetch(`/api/v1/borrow?bookId=${bookId}`, {
            method: "POST"
        });

        if (!response.ok) {
            const data = await response.json();
            showAlertToast(data.message);
            return;
        }

        updateBorrowBtn(bookId);
        showAlertToast("해당 도서가 대출되었습니다.");

        await fetchBookDetails(bookId);
    } catch (error) {
        console.error("Error:", error);
    }
}

function updateBorrowBtn(bookId) {
    const borrowButton = document.querySelector(".borrow-btn");

    if (borrowButton) {
        const parentDiv = borrowButton.parentNode;

        borrowButton.remove();

        const reserveButton = document.createElement("button");
        reserveButton.classList.add("btn", "btn-custom-1", "reserve-btn");
        reserveButton.textContent = "예약하기";
        reserveButton.dataset.bookId = bookId;

        parentDiv.insertBefore(reserveButton, parentDiv.children[0]);

        // 동적으로 추가된 버튼에 이벤트 부착
        attachReservationEvent();
    }
}
