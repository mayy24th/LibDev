import { loadReviews } from "./reviewList.js";
import { openModifyModal, closeModifyModal, submitModifyReview } from "./reviewModify.js";
import { openDeleteModal, closeDeleteModal, confirmDelete } from "./reviewDelete.js";

document.addEventListener("DOMContentLoaded", function () {
    loadReviews();

    document.getElementById("openModifyModalBtn").addEventListener("click", () => openModifyModal());
    document.getElementById("closeModifyModalBtn").addEventListener("click", closeModifyModal);
    document.getElementById("submitModifyBtn").addEventListener("click", submitModifyReview);

    document.getElementById("confirmDeleteBtn").addEventListener("click", confirmDelete);
    document.getElementById("closeDeleteModalBtn").addEventListener("click", closeDeleteModal);
});
