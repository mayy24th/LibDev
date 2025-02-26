import { formatDate, statusColor } from "./utils.js";
import { renderPagination } from "./renderPagination.js";
import { showAlertToast } from "../utils/showAlertToast.js";
import {checkLoginStatus} from "../utils/auth";

const statusFilter = document.querySelector("#status-filter");

statusFilter.addEventListener("change", () => {
    loadBorrowList(0, statusFilter.value);
});

loadBorrowList(0, statusFilter.value); // 첫 페이지 로드

async function loadBorrowList(page, status) {
    const isLoggedIn = await checkLoginStatus();
    if (!isLoggedIn) return;

    try {
        const response = await fetch(`/api/admin/v1/borrow-list?page=${page}&status=${status}`);

        const data = await response.json();

        if (!response.ok) {
            showAlertToast(data.message);
            return;
        }

        displayBorrowList(data.content);

        if (data.content.length === 0) {
            document.querySelector("#pagination").style.display = "none";
        } else {
            document.querySelector("#pagination").style.removeProperty("display");
            renderPagination(data.totalPages, data.number, (newPage) => loadBorrowList(newPage, statusFilter.value));
        }
    } catch (error) {
        console.error(error.message);
    }
}

function displayBorrowList(borrowList) {
    const tableContainer = document.querySelector(".table-container");
    const existingBlankMessage = document.querySelector(".blank-message");
    if(existingBlankMessage) {
        existingBlankMessage.remove();
    }

    const borrowListContainer = document.querySelector(".borrow-list");
    borrowListContainer.innerHTML = ""; // 기존 내용 초기화

    // 데이터가 없을 경우
    if (!borrowList || borrowList.length === 0) {
        const blankMessage = document.createElement("p");
        blankMessage.textContent = "대출 정보가 없습니다.";
        blankMessage.classList.add("blank-message");

        tableContainer.appendChild(blankMessage);
        return;
    }

    borrowList.forEach((borrow) => {
        const borrowItem = document.createElement("tr");
        borrowItem.id = `borrow-${borrow.id}`;

        const borrowId = document.createElement("td");
        borrowId.textContent = borrow.id;

        const bookTitle = document.createElement("td");
        bookTitle.textContent = borrow.bookTitle;

        const callNumber = document.createElement("td");
        callNumber.textContent = borrow.callNumber;

        const userEmail = document.createElement("td");
        userEmail.textContent = borrow.userEmail;

        const borrowDate = document.createElement("td");
        borrowDate.textContent = formatDate(borrow.borrowDate);

        const dueDate = document.createElement("td");
        dueDate.textContent = formatDate(borrow.dueDate);

        const returnDate = document.createElement("td");
        returnDate.textContent = borrow.status === "반납 완료" ? formatDate(borrow.returnDate) : "-";
        returnDate.classList.add("return-date");

        const extended = document.createElement("td");
        extended.textContent = borrow.extended ? "Y" : "N";

        const overdueDays = document.createElement("td");
        overdueDays.textContent = borrow.overdue ? `${borrow.overdueDays}일` : "-";

        const borrowStatus = document.createElement("td");
        borrowStatus.textContent = borrow.status;
        borrowStatus.style.color = statusColor(borrow.status);
        borrowStatus.classList.add("borrow-status");

        const checkboxTd = document.createElement("td");
        if (borrow.status === "반납 신청") {
            const checkbox = document.createElement("input");
            checkbox.type = "checkbox";
            checkbox.classList.add("return-checkbox");
            checkbox.dataset.borrowId = borrow.id;
            checkboxTd.appendChild(checkbox);
        }

        borrowItem.appendChild(borrowId);
        borrowItem.appendChild(bookTitle);
        borrowItem.appendChild(callNumber);
        borrowItem.appendChild(userEmail);
        borrowItem.appendChild(borrowDate);
        borrowItem.appendChild(dueDate);
        borrowItem.appendChild(returnDate);
        borrowItem.appendChild(extended);
        borrowItem.appendChild(overdueDays);
        borrowItem.appendChild(borrowStatus);
        borrowItem.appendChild(checkboxTd);

        borrowListContainer.appendChild(borrowItem);
    });
}

