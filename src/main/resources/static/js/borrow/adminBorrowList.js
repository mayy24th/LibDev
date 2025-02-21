import {formatDate, statusColor} from "./utils.js";

document.addEventListener("DOMContentLoaded", async () => {
    try {
        const response = await fetch("/api/v1/borrow-list");

        if (!response.ok) {
            throw new Error("대출 내역을 불러오는데 실패했습니다.");
        }

        const borrowList = await response.json();
        displayBorrowList(borrowList);
    } catch (error) {
        console.error(error.message);
    }
});

function displayBorrowList(borrowList) {
    const borrowListContainer = document.querySelector(".borrow-list");

    borrowList.forEach((borrow) => {
        const borrowItem = document.createElement("tr");

        const borrowId = document.createElement("td");
        borrowId.textContent = borrow.id;

        const bookTitle = document.createElement("td");
        bookTitle.textContent = borrow.bookTitle;

        const userEmail = document.createElement("td");
        userEmail.textContent = borrow.userEmail;

        const borrowDate = document.createElement("td");
        borrowDate.textContent = formatDate(borrow.borrowDate);

        const dueDate = document.createElement("td");
        dueDate.textContent = formatDate(borrow.dueDate);

        const returnDate = document.createElement("td");
        returnDate.textContent = borrow.status == "반납 완료" ? formatDate(borrow.returnDate) : "-";

        const extended = document.createElement("td");
        extended.textContent = borrow.extended ? "Y" : "N";

        const overdueDays = document.createElement("td");
        overdueDays.textContent = borrow.overdue ? `${borrow.overdueDays}일` : "-";

        const borrowStatus = document.createElement("td");
        borrowStatus.textContent = borrow.status;
        borrowStatus.style.color = statusColor(borrow.status);

        const returnbtn = document.createElement("td");
        if (borrow.status == "반납 신청") {
            const btn = document.createElement("button");
            btn.textContent = "반납 확인";
            btn.classList.add("btn", "return-approve-btn");
            returnbtn.appendChild(btn);
        }

        borrowItem.appendChild(borrowId);
        borrowItem.appendChild(bookTitle);
        borrowItem.appendChild(userEmail);
        borrowItem.appendChild(borrowDate);
        borrowItem.appendChild(dueDate);
        borrowItem.appendChild(returnDate);
        borrowItem.appendChild(extended);
        borrowItem.appendChild(overdueDays);
        borrowItem.appendChild(borrowStatus);
        borrowItem.appendChild(returnbtn);

        borrowListContainer.appendChild(borrowItem);
    });
}

