import { openModifyModal } from "./reviewModify.js";
import { openDeleteModal } from "./reviewDelete.js";

export async function loadReviews(apiEndpoint) {
    try {
        const response = await fetch(apiEndpoint, {
            method: "GET"
        });

        if (!response.ok) {
            throw new Error(`서버 응답 오류: ${response.status} ${response.statusText}`);
        }

        const reviews = await response.json();
        reviews.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));

        const container = document.getElementById("reviewContainer");
        container.innerHTML = "";
        reviews.forEach(review => {
            const reviewHTML = `
        <div class="review-card">
            <div class="book-image">${review.thumbnail}</div>
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
                    ${review.owner ? `
                        <button class="btn-edit edit-btn" data-id="${review.id}" data-content="${review.content}">수정</button>
                        <button class="btn-delete delete-btn" data-id="${review.id}">삭제</button>
                    ` : ""}
                </div>
            </div>
        </div>
    `;

            container.insertAdjacentHTML("beforeend", reviewHTML);
        });


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

    } catch (error) {
        console.error("한줄평 목록 불러오기 실패:", error);
        alert("한줄평 목록을 불러오는 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
    }
}
