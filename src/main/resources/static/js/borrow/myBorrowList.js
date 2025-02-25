import { formatDate, statusColor } from "./utils.js";
import { extendBorrow } from "./extendBorrow.js";
import { requestReturn } from "./requestReturn.js";
import { showAlertToast } from "../utils/showAlertToast.js";

fetchCurrentBorrows();

async function fetchCurrentBorrows() {
    try {
        const response = await fetch("/api/v1/my/borrow-status", {
            method: "GET",
        });
        const data = await response.json();

        if (!response.ok) {
            showAlertToast(data.message);
            return;
        }

        renderCurrentBorrows(data);
    } catch (error) {
        console.error('Error:', error);
    }
}

function renderCurrentBorrows(borrowList) {
    const borrowListContainer = document.querySelector(".borrow-list");

    // 데이터가 없을 경우
    if (!borrowList || borrowList.length === 0) {
        const blankMessage = document.createElement("p");
        blankMessage.textContent = "현재 대출 중인 도서가 없습니다.";
        blankMessage.classList.add("blank-message");
        borrowListContainer.appendChild(blankMessage);
        return;
    }
    borrowList.forEach((borrow) => {
        const borrowBox = document.createElement("div");
        borrowBox.classList.add("borrow-box");

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

        const extended = document.createElement("p");
        extended.classList.add("borrow-content", "extended-text");
        extended.textContent = "연장: ";
        const extendedData = document.createElement("span");
        extendedData.id = `extended-status-${borrow.id}`;
        extendedData.textContent = borrow.extended ? "Y" : "N";
        extended.appendChild(extendedData);

        const borrowStatus = document.createElement("p");
        borrowStatus.classList.add("borrow-content");
        borrowStatus.textContent = "상태: ";
        const borrowStatusData = document.createElement("span");
        borrowStatusData.id = `borrow-status-${borrow.id}`;
        borrowStatusData.textContent = borrow.status;
        borrowStatusData.style.color = statusColor(borrow.status);
        borrowStatus.appendChild(borrowStatusData);

        borrowInfo.appendChild(borrowDate);
        borrowInfo.appendChild(dueDate);
        borrowInfo.appendChild(extended);
        borrowInfo.appendChild(borrowStatus);

        borrowContentBox.appendChild(bookTitle);
        borrowContentBox.appendChild(borrowInfo);

        const btnContainer = document.createElement("div");
        btnContainer.classList.add("btn-container");

        if(!borrow.extended && borrow.borrowAvailable && borrow.status !== "반납 신청") {
            const extendBtn = document.createElement("button");
            extendBtn.classList.add("btn", "extend-btn");
            extendBtn.id = `extend-btn-${borrow.id}`;
            extendBtn.textContent = "연장하기";
            extendBtn.addEventListener("click", () => extendBorrow(borrow.id));
            btnContainer.appendChild(extendBtn);
        }

        const requestReturnBtn = document.createElement("button");
        requestReturnBtn.classList.add("btn", "request-return-btn");
        requestReturnBtn.id = `request-return-btn-${borrow.id}`;
        requestReturnBtn.textContent = "반납신청";
        if (borrow.status === "반납 신청") {
            requestReturnBtn.disabled = true;
            requestReturnBtn.textContent = "신청완료";
        } else {
            requestReturnBtn.addEventListener("click", () => requestReturn(borrow.id));
        }
        btnContainer.appendChild(requestReturnBtn);

        borrowBox.appendChild(borrowContentBox);
        borrowBox.appendChild(btnContainer);

        borrowListContainer.appendChild(borrowBox);
    });
}
