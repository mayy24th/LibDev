import { formatDate, statusColor } from "./utils.js";
import { showAlertToast } from "../utils/showAlertToast.js";
import { showConfirmToast } from "../utils/showConfirmToast.js";

const returnApproveBtn = document.querySelector("#return-approve-btn");
returnApproveBtn.addEventListener("click", approveReturn);

async function approveReturn() {
    const checkedBoxes = document.querySelectorAll(".return-checkbox:checked");

    if (checkedBoxes.length === 0) {
        showAlertToast("반납 처리할 대출을 선택해주세요.");
        return;
    }

    const borrowIds = Array.from(checkedBoxes).map(checkbox => checkbox.dataset.borrowId);

    showConfirmToast(`선택한 ${borrowIds.length}건을 반납 처리하시겠습니까?`, async () => {
        try {
            const response = await fetch("/api/v1/approve-return", {
                method: "PATCH",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ borrowIds })
            });

            const data = await response.json();

            if (!response.ok) {
                showAlertToast(data.message);
                return;
            }

            data.forEach(updateReturnStatus);
            showAlertToast(`${borrowIds.length}건이 반납 처리되었습니다.`);
        } catch (error) {
            console.error("Error:", error);
        }
    });
}

function updateReturnStatus(borrow) {
    const borrowItem = document.querySelector(`#borrow-${borrow.id}`);

    const checkbox = borrowItem.querySelector(".return-checkbox");
    checkbox.remove();

    const returnDate = borrowItem.querySelector(".return-date");
    returnDate.textContent = formatDate(borrow.returnDate);

    const borrowStatus = borrowItem.querySelector(".borrow-status");
    borrowStatus.textContent = borrow.status;
    borrowStatus.style.color = statusColor(borrow.status);
}