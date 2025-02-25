import { formatDate } from "./utils.js";
import { renderPagination } from "./renderPagination.js";
import { showAlertToast } from "../utils/showAlertToast.js";

const orderSelect = document.querySelector("#order-select");

orderSelect.addEventListener("change", () => {
    loadBorrowHistory(0, orderSelect.value);
});

loadBorrowHistory(0, orderSelect.value);

async function loadBorrowHistory(page, order) {
    try {
        const response = await fetch(`/api/v1/my/borrow-history?page=${page}&order=${order}`, {
            method: "GET",
        });
        const data = await response.json();

        if (!response.ok) {
            showAlertToast(data.message);
            return;
        }

        renderBorrowHistory(data.content, page);

        if (data.content.length === 0) {
            document.querySelector("#pagination").style.display = "none";
        } else {
            renderPagination(data.totalPages, data.number, loadBorrowHistory);
        }
    } catch (error) {
        console.error('Error:', error);
    }
}

function renderBorrowHistory(borrowList, currentPage) {
    const borrowListContainer = document.querySelector(".borrow-list");
    borrowListContainer.innerHTML = ""; // 기존 내용 초기화

    // 데이터가 없을 경우
    if (!borrowList || borrowList.length === 0) {
        const blankMessage = document.createElement("p");
        blankMessage.textContent = "대출 이력이 없습니다.";
        blankMessage.classList.add("blank-message");
        borrowListContainer.appendChild(blankMessage);
        return;
    }

    borrowList.forEach((borrow, index) => {
        const borrowBox = document.createElement("div");
        borrowBox.classList.add("borrow-box");

        const borrowNumber = document.createElement("div");
        borrowNumber.classList.add("borrow-number");
        borrowNumber.textContent = (currentPage * 10) + index + 1;

        const borrowContentBox = document.createElement("div");
        borrowContentBox.classList.add("borrow-content-box");

        const bookTitle = document.createElement("p");
        bookTitle.classList.add("book-title");
        bookTitle.textContent = borrow.bookTitle;

        const borrowInfo = document.createElement("div");
        borrowInfo.classList.add("borrow-info");
        borrowInfo.id = `borrow-info-${borrow.id}`;

        const borrowDate = document.createElement("p");
        borrowDate.classList.add("borrow-content");
        borrowDate.textContent = "대출일: ";
        const borrowDateData = document.createElement("span");
        borrowDateData.textContent = formatDate(borrow.borrowDate);
        borrowDate.appendChild(borrowDateData);

        const dueDate = document.createElement("p");
        dueDate.classList.add("borrow-content");
        dueDate.textContent = "반납 예정일: ";
        const dueDateData = document.createElement("span");
        dueDateData.id = `duedate-${borrow.id}`;
        dueDateData.textContent = formatDate(borrow.dueDate);
        dueDate.appendChild(dueDateData);

        const returnDate = document.createElement("p");
        returnDate.classList.add("borrow-content");
        returnDate.textContent = "반납일: ";
        const returnDateData = document.createElement("span");
        returnDateData.textContent = formatDate(borrow.returnDate);
        returnDate.appendChild(returnDateData);

        const extended = document.createElement("p");
        extended.classList.add("borrow-content", "extended-text");
        extended.textContent = "연장: ";
        const extendedData = document.createElement("span");
        extendedData.id = `extended-status-${borrow.id}`;
        extendedData.textContent = borrow.extended ? "Y" : "N";
        extended.appendChild(extendedData);

        borrowInfo.appendChild(borrowDate);
        borrowInfo.appendChild(dueDate);
        borrowInfo.appendChild(returnDate);
        borrowInfo.appendChild(extended);

        if(borrow.overdue == true) {
            const overdue = document.createElement("p");
            overdue.classList.add("borrow-content");
            overdue.textContent = `연체: ${borrow.overdueDays}일`;
            overdue.style.color = "#df0303";
            borrowInfo.appendChild(overdue);
        }

        borrowContentBox.appendChild(bookTitle);
        borrowContentBox.appendChild(borrowInfo);

        borrowBox.appendChild(borrowNumber);
        borrowBox.appendChild(borrowContentBox);

        borrowListContainer.appendChild(borrowBox);
    });
}
