import { loadReviews } from "./reviewList.js";
import { getReviewApiEndpoint } from "../utils/pathUtils.js";
import { showAlertToast } from "../utils/showAlertToast.js"

export function openDeleteModal(reviewId) {
    const modal = document.getElementById("deleteModal");
    modal.dataset.reviewId = reviewId; // 삭제할 reviewId를 dataset에 저장
    modal.style.display = "flex";
}

export function closeDeleteModal() {
    const modal = document.getElementById("deleteModal");
    modal.style.display = "none";
    modal.dataset.reviewId = ""; // reviewId 초기화
}

export async function confirmDelete() {
    const modal = document.getElementById("deleteModal");
    const reviewId = modal.dataset.reviewId; // dataset에서 reviewId 가져오기

    if (!reviewId) return;

    try {
        const response = await fetch(`/api/review/${reviewId}`, {
            method: "DELETE"
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message);
        }

        showAlertToast("한줄평이 삭제되었습니다.")
        loadReviews(getReviewApiEndpoint());
        closeDeleteModal();
    } catch (error) {
        console.error(error);
        showAlertToast(error.message);
    }
}
