document.addEventListener("DOMContentLoaded", function () {
    loadReviews();
});

let reviewToDelete = null; // 삭제할 한줄평 ID 저장

async function loadReviews() {
    const urlParams = new URLSearchParams(window.location.search);
    const type = urlParams.get("type");
    const userId = urlParams.get("userId");
    const bookId = urlParams.get("bookId");

    let apiUrl = "/api/review";
    let titleText = "전체 한줄평 목록";

    if (type === "user" && userId) {
        apiUrl = `/api/review/user/${userId}`;
        titleText = "내가 작성한 한줄평";
    } else if (type === "book" && bookId) {
        apiUrl = `/api/review/book/${bookId}`;
        titleText = "도서별 한줄평 목록";
    }

    document.getElementById("pageTitle").textContent = titleText;

    const response = await fetch(apiUrl);
    const reviews = await response.json();

    const container = document.getElementById("reviewContainer");
    container.innerHTML = reviews.map(review =>
        `<div class="review-card">
            <div class="book-image">이미지 준비중입니다.</div>
            <div class="review-content-box">
                <div class="review-header">도서ID(도서명으로 변경) ${review.bookId}</div>
                <hr class="review-divider">
                <div class="review-row">
                    <span class="review-label">한줄서평</span>
                    <span class="review-text">${review.content}</span>
                </div>
                <div class="review-row">
                    <span class="review-label">유저ID(유저명으로 변경) </span>
                    <span class="review-author">${review.userId}</span>
                </div>
                <div class="review-footer">
                    <button class="btn-delete" onclick="openDeleteModal(${review.id})">삭제</button>
                </div>
            </div>
        </div>`
    ).join("");
}

// 삭제 모달 열기
function openDeleteModal(reviewId) {
    reviewToDelete = reviewId;
    document.getElementById("deleteModal").style.display = "flex";
}

// 삭제 모달 닫기
function closeDeleteModal() {
    reviewToDelete = null;
    document.getElementById("deleteModal").style.display = "none";
}

// 삭제 확인 버튼 클릭 시 실행
async function confirmDelete() {
    if (!reviewToDelete) return;

    const response = await fetch(`/api/review/${reviewToDelete}`, { method: "DELETE" });

    if (response.ok) {
        loadReviews();
    } else {
        alert("한줄평 삭제에 실패했습니다.");
    }

    closeDeleteModal();
}

// ✅ 전역에서 호출 가능하도록 등록
window.openDeleteModal = openDeleteModal;
window.closeDeleteModal = closeDeleteModal;
window.confirmDelete = confirmDelete;
