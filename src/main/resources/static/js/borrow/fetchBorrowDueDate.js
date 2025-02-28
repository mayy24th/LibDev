import { formatDate } from "./utils.js";
import { showAlertToast } from "../utils/showAlertToast.js";

const dueDateElement = document.querySelector("#bookReturnDueDate");
const bookId = dueDateElement.dataset.bookId;

fetchBorrowDueDate();

async function fetchBorrowDueDate() {
    try {
        const response = await fetch(`/book/due-date?bookId=${bookId}`, {
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

function renderCurrentBorrows(data) {
    const dueDate = document.querySelector("#bookReturnDueDate");
    dueDate.textContent = data.dueDate ? formatDate(data.dueDate) : "-";
}