import { formatDate, statusColor } from "./utils.js";
import { showAlertToast } from "../utils/showAlertToast.js";
import { showConfirmToast } from "../utils/showConfirmToast.js";

export async function approveReturn(borrowId) {
    showConfirmToast("해당 도서를 반납 처리 하시겠습니까?", async () => {
        try {
            const response = await fetch(`/api/v1/approve-return/${borrowId}`, {
                method: "PATCH"
            });
            const data = await response.json();

            if (!response.ok) {
                showAlertToast(data.message);
                return;
            }

            updateReturnStatus(data);
            showAlertToast("해당 도서가 반납 처리 되었습니다.");
        } catch (error) {
            console.error("Error:", error);
        }
    });
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