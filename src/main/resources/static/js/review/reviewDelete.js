import { loadReviews } from "./reviewList.js";
export let reviewToDelete = null;

export function openDeleteModal(reviewId) {
    reviewToDelete = reviewId;
    document.getElementById("deleteModal").style.display = "flex";
}

export function closeDeleteModal() {
    reviewToDelete = null;
    document.getElementById("deleteModal").style.display = "none";
}

export async function confirmDelete() {
    if (!reviewToDelete) return;

    const response = await fetch(`/api/review/${reviewToDelete}`, {
        method: "DELETE"
    });

    if (response.ok) {
        alert("한줄평이 삭제되었습니다.");
        loadReviews();
        closeDeleteModal();
    } else {
        alert("한줄평 삭제에 실패했습니다.");
    }
}
