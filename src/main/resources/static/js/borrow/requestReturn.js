import { statusColor } from "./utils.js";

export async function requestReturn(borrowId) {
    try {
        const response = await fetch(`/api/v1/return/${borrowId}`, {
            method: "PATCH"
        });

        if (!response.ok) {
            alert("반납 신청 요청 실패");
            throw new Error("반납 신청 요청 실패");
        }

        const data = await response.json();
        updateBorrowStatus(data);
        alert("반납 신청이 완료되었습니다.");
    } catch (error) {
        console.error("Error:", error);
    }
}

function updateBorrowStatus(borrow) {
    const requestReturnBtn = document.querySelector(`#request-return-btn-${borrow.id}`);
    if (requestReturnBtn) {
        requestReturnBtn.disabled = true;
    }

    const extendBtn = document.querySelector(`#extend-btn-${borrow.id}`);
    if (extendBtn) {
        extendBtn.disabled = true;
    }

    const borrowStatus = document.querySelector(`#borrow-status-${borrow.id}`);
    if (borrowStatus) {
        borrowStatus.textContent = borrow.status;
        borrowStatus.style.color = statusColor(borrow.status);
    }
}