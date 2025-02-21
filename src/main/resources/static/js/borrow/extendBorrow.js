import { formatDate } from "./utils.js";

export async function extendBorrow(borrowId) {
    try {
        const response = await fetch(`/api/v1/extend/${borrowId}`, {
            method: "PATCH"
        });

        if (!response.ok) {
            throw new Error("연장 요청 실패");
        }

        const data = await response.json();
        updateBorrow(data);
        alert("대출 기간이 7일 연장되었습니다.");
    } catch (error) {
        console.error("Error:", error);
    }
}

function updateBorrow(borrow) {
    const button = document.querySelector(`#extend-btn-${borrow.id}`);
    if (button) {
        button.remove();
    }

    const dueDate = document.querySelector(`#duedate-${borrow.id}`);
    if (dueDate) {
        dueDate.textContent = formatDate(borrow.dueDate);
    }

    const extendedStatus = document.querySelector(`#extended-status-${borrow.id}`);
    if (extendedStatus) {
        extendedStatus.textContent = "Y";
    }
}