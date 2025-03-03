import { fetchBookDetails } from "/js/book/detail.js";
import { attachReservationEvent } from "/js/reservation/reservation.js";
import { showAlertToast } from "../utils/showAlertToast.js";
import { formatDate } from "./utils.js";
import { checkLoginStatus } from "../utils/auth.js";

const borrowButton = document.querySelector(".borrow-btn");

if (borrowButton) {
    borrowButton.addEventListener("click", function () {
        if (this.classList.contains("reserve-btn")) { return; }

        const bookId = this.dataset.bookId;
        borrowBook(bookId);
    });
}

async function borrowBook(bookId) {
    const isLoggedIn = await checkLoginStatus();
    if (!isLoggedIn) return;

    try {
        const response = await fetch(`/api/v1/borrow?bookId=${bookId}`, {
            method: "POST"
        });
        const data = await response.json();

        if (!response.ok) {
            showAlertToast(data.message);
            return;
        }

        updateBorrowElement(data);
        showAlertToast("해당 도서가 대출되었습니다.");

        await fetchBookDetails(bookId);
    } catch (error) {
        console.error("Error:", error);
    }
}

function updateBorrowElement(data) {
    const borrowButton = document.querySelector(".borrow-btn");

    if (borrowButton) {
        borrowButton.classList.remove("borrow-btn");

        borrowButton.classList.add("reserve-btn");
        borrowButton.textContent = "예약하기";

        // 동적으로 추가된 버튼에 이벤트 부착
        attachReservationEvent();
    }

    const dueDate = document.querySelector("#bookReturnDueDate");
    dueDate.textContent = formatDate(data.dueDate);
}
