import { loadReviews } from "./reviewList.js";
import { getReviewApiEndpoint } from "../utils/pathUtils.js";

export function openModifyModal(reviewId = null, content = "") {
    const modal = document.getElementById("modifyModal");
    modal.dataset.reviewId = reviewId || ""; // reviewId를 dataset으로 저장

    document.getElementById("modalTitle").textContent = reviewId ? "한줄평 수정" : "한줄평 작성";
    document.getElementById("submitModifyBtn").textContent = reviewId ? "수정하기" : "작성하기";
    document.getElementById("reviewContent").value = content || "";

    modal.style.display = "flex";
}

export function closeModifyModal() {
    const modal = document.getElementById("modifyModal");
    modal.style.display = "none";
    modal.dataset.reviewId = ""; // reviewId 초기화
}

export async function submitModifyReview() {
    const modal = document.getElementById("modifyModal");
    const bookId = window.location.pathname.split("/").pop();
    const reviewId = modal.dataset.reviewId; // dataset에서 reviewId 가져오기
    const content = document.getElementById("reviewContent").value.trim();

    if (!content) {
        alert("한줄평을 입력해주세요.");
        return;
    }

    if (reviewId) {
        await updateReview(reviewId, content);
    } else {
        await createReview(bookId, content);
    }
}

async function createReview(bookId, content) {
    try {
        const response = await fetch("/api/review", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                bookId: bookId,
                content: content
            })
        });

        if (!response.ok) throw new Error("한줄평 등록 실패");

        alert("한줄평을 등록했습니다.");
        loadReviews(getReviewApiEndpoint());
        closeModifyModal();
    } catch (error) {
        console.error(error);
        alert("한줄평 등록에 실패했습니다.");
    }
}

async function updateReview(reviewId, content) {
    try {
        const response = await fetch(`/api/review/${reviewId}`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ content })
        });

        if (!response.ok) throw new Error("한줄평 수정 실패");

        alert("한줄평을 수정하였습니다.");
        loadReviews(getReviewApiEndpoint());
        closeModifyModal();
    } catch (error) {
        console.error(error);
        alert("한줄평 수정에 실패했습니다.");
    }
}
