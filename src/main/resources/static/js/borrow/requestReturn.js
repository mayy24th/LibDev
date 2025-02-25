import { statusColor } from "./utils.js";
import { showAlertToast } from "../utils/showAlertToast.js";

export async function requestReturn(borrowId) {
    try {
        const response = await fetch(`/api/v1/return/${borrowId}`, {
            method: "PATCH"
        });
        const data = await response.json();

        if (!response.ok) {
            showAlertToast(data.message);
            return;
        }

        updateBorrowStatus(data);
        showAlertToast("반납 신청이 완료되었습니다.");
    } catch (error) {
        console.error("Error:", error);
    }
}

function updateBorrowStatus(borrow) {
    const requestReturnBtn = document.querySelector(`#request-return-btn-${borrow.id}`);
    if (requestReturnBtn) {
        requestReturnBtn.textContent = "신청완료";
        requestReturnBtn.disabled = true;
    }

    const extendBtn = document.querySelector(`#extend-btn-${borrow.id}`);
    if (extendBtn) {
        extendBtn.remove();
    }

    const borrowStatus = document.querySelector(`#borrow-status-${borrow.id}`);
    if (borrowStatus) {
        borrowStatus.textContent = borrow.status;
        borrowStatus.style.color = statusColor(borrow.status);
    }
}