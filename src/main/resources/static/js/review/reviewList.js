import { openModifyModal } from "./reviewModify.js";
import { openDeleteModal } from "./reviewDelete.js";

export async function loadReviews() {
    const response = await fetch("/api/review");
    const reviews = await response.json();

    const container = document.getElementById("reviewContainer");
    container.innerHTML = reviews.map(review =>
        `<div class="review-card">
            <div class="book-image">이미지 준비중입니다.</div>
            <div class="review-content-box">
                <div class="review-header">${review.bookName}</div>
                <hr class="review-divider">
                <div class="review-row">
                    <span class="review-label">한줄서평</span>
                    <span class="review-text">${review.content}</span>
                </div>
                <div class="review-row">
                    <span class="review-label">작성자</span>
                    <span class="review-author">${review.userName}</span>
                </div>
                <div class="review-footer">
                    <button class="btn-edit edit-btn" data-id="${review.id}" data-content="${review.content}">수정</button>
                    <button class="btn-delete delete-btn" data-id="${review.id}">삭제</button>
                </div>
            </div>
        </div>`
    ).join("");

    document.querySelectorAll(".edit-btn").forEach(button => {
        button.addEventListener("click", (event) => {
            const reviewId = event.target.getAttribute("data-id");
            const content = event.target.getAttribute("data-content");
            openModifyModal(reviewId, content);
        });
    });

    document.querySelectorAll(".delete-btn").forEach(button => {
        button.addEventListener("click", (event) => {
            const reviewId = event.target.getAttribute("data-id");
            openDeleteModal(reviewId);
        });
    });
}
