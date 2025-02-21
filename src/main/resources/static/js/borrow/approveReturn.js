import { formatDate, statusColor } from "./utils.js";

export async function approveReturn(borrowId) {
    if (!confirm("해당 도서를 반납 처리 하시겠습니까?")) {
        return;
    }

    try {
        const response = await fetch(`/api/v1/approve-return/${borrowId}`, {
            method: "PATCH"
        });

        if (!response.ok) {
            alert("반납 승인 실패");
            throw new Error("반납 승인 실패");
        }

        const data = await response.json();
        updateReturnStatus(data);
        alert("해당 도서가 반납 처리 되었습니다.");
    } catch (error) {
        console.error("Error:", error);
    }
}

function updateReturnStatus(borrow) {
    const borrowItem = document.querySelector(`#borrow-${borrow.id}`);

    const returnBtn = borrowItem.querySelector(".return-approve-btn");
    returnBtn.remove();

    const returnDate = borrowItem.querySelector(".return-date");
    returnDate.textContent = formatDate(borrow.returnDate);

    const borrowStatus = borrowItem.querySelector(".borrow-status");
    borrowStatus.textContent = borrow.status;
    borrowStatus.style.color = statusColor(borrow.status);
}