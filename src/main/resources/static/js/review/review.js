import { loadReviews } from "./reviewList.js";
import { openModifyModal, closeModifyModal, submitModifyReview } from "./reviewModify.js";
import { openDeleteModal, closeDeleteModal, confirmDelete } from "./reviewDelete.js";
import { checkLoginStatus } from "../utils/auth.js";
import { getReviewApiEndpoint } from "../utils/pathUtils.js";

document.addEventListener("DOMContentLoaded", async function () {
    loadReviews(getReviewApiEndpoint());

    document.getElementById("openModifyModalBtn").addEventListener("click", async () => {
        const isLoggedIn = await checkLoginStatus();
        if (!isLoggedIn) return;

        openModifyModal();
    });

    document.getElementById("closeModifyModalBtn").addEventListener("click", closeModifyModal);
    document.getElementById("submitModifyBtn").addEventListener("click", submitModifyReview);

    document.getElementById("confirmDeleteBtn").addEventListener("click", confirmDelete);
    document.getElementById("closeDeleteModalBtn").addEventListener("click", closeDeleteModal);
});
