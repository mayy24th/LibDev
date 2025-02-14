import { loadReviews } from "./reviewList.js";
export let reviewToEdit = null;

export function openModifyModal(reviewId = null, content = "") {
    if (reviewId) {
        reviewToEdit = reviewId;
        document.getElementById("modalTitle").textContent = "한줄평 수정";
        document.getElementById("submitModifyBtn").textContent = "수정하기";
        document.getElementById("reviewContent").value = content;
    } else {
        reviewToEdit = null;
        document.getElementById("modalTitle").textContent = "한줄평 작성";
        document.getElementById("submitModifyBtn").textContent = "작성하기";
        document.getElementById("reviewContent").value = "";
    }

    document.getElementById("modifyModal").style.display = "flex";
}

export function closeModifyModal() {
    document.getElementById("modifyModal").style.display = "none";
    reviewToEdit = null;
}

export async function submitModifyReview() {
    const content = document.getElementById("reviewContent").value.trim();

    if (!content) {
        alert("한줄평을 입력해주세요.");
        return;
    }

    if (reviewToEdit) {
        await updateReview(reviewToEdit, content);
    } else {
        await createReview(content);
    }
}

async function createReview(content) {
    const response = await fetch("/api/review", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ content })
    });

    if (response.ok) {
        alert("한줄평을 등록했습니다.");
        loadReviews();
        closeModifyModal();
    } else {
        alert("한줄평 등록에 실패했습니다.");
    }
}

async function updateReview(reviewId, content) {
    const response = await fetch(`/api/review/${reviewId}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ content })
    });

    if (response.ok) {
        alert("한줄평을 수정하였습니다.");
        loadReviews();
        closeModifyModal();
    } else {
        alert("한줄평 수정에 실패했습니다.");
    }
}
