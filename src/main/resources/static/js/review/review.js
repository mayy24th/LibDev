document.addEventListener("DOMContentLoaded", function () {
    loadReviews(); // 리뷰 조회
});

async function loadReviews() {
    const urlParams = new URLSearchParams(window.location.search);
    const type = urlParams.get("type"); // 조회 유형: all, user, book
    const userId = urlParams.get("userId");
    const bookId = urlParams.get("bookId");

    let apiUrl = "/api/review"; // 기본값: 전체 리뷰 조회
    let titleText = "전체 리뷰 목록";

    if (type === "user" && userId) {
        apiUrl = `/api/review/user/${userId}`;
        titleText = "내가 작성한 리뷰";
    } else if (type === "book" && bookId) {
        apiUrl = `/api/review/book/${bookId}`;
        titleText = "도서별 리뷰 목록";
    }

    document.getElementById("pageTitle").textContent = titleText;

    const response = await fetch(apiUrl);
    const reviews = await response.json();

    const table = document.getElementById("reviewTable");
    table.innerHTML = reviews.map(review =>
        `<tr>
            <td>${review.id}</td>
            <td>${review.userId}</td>
            <td>${review.bookId}</td>
            <td>${review.content}</td>
            <td>${new Date(review.createdAt).toLocaleString()}</td>
            <td><button class="btn-delete" onclick="deleteReview(${review.id})">삭제</button></td>
        </tr>`
    ).join("");
}

async function deleteReview(reviewId) {
    if (!confirm("리뷰를 삭제하시겠습니까?")) return;

    const response = await fetch(`/api/review/${reviewId}`, { method: "DELETE" });

    if (response.ok) {
        alert("리뷰가 삭제되었습니다.");
        loadReviews(); // 삭제 후 목록 다시 불러오기
    } else {
        alert("리뷰 삭제에 실패했습니다.");
    }
}
