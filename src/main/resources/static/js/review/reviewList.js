import { openModifyModal } from "./reviewModify.js";
import { openDeleteModal } from "./reviewDelete.js";

export async function loadReviews(apiEndpoint) {
    try {
        const response = await fetch(apiEndpoint, { method: "GET" });

        if (!response.ok) {
            throw new Error(`서버 응답 오류: ${response.status} ${response.statusText}`);
        }

        const reviews = await response.json();
        reviews.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));

        updateReviewWriteButton();
        setupPagination(reviews, 1); // 첫 번째 페이지로 초기화

    } catch (error) {
        console.error("한줄평 목록 불러오기 실패:", error);
        alert("한줄평 목록을 불러오는 중 오류가 발생했습니다.");
    }
}

function displayReviews(reviews, page = 1, reviewsPerPage = 5) {
    const container = document.getElementById("reviewContainer");
    container.innerHTML = "";

    const startIndex = (page - 1) * reviewsPerPage;
    const selectedReviews = reviews.slice(startIndex, startIndex + reviewsPerPage);

    selectedReviews.forEach(review => {
        const reviewCard = document.createElement("div");
        reviewCard.classList.add("review-card", "d-flex");

        const bookImage = document.createElement("img");
        bookImage.classList.add("book-image");
        bookImage.src = review.thumbnail;
        bookImage.alt = "책 이미지";

        const contentBox = document.createElement("div");
        contentBox.classList.add("review-content-box");

        const header = document.createElement("div");
        header.classList.add("review-header");
        header.innerText = review.bookName;

        const divider = document.createElement("hr");
        divider.classList.add("review-divider");

        const reviewRow = document.createElement("div");
        reviewRow.classList.add("review-row");

        const label = document.createElement("span");
        label.classList.add("review-label");
        label.innerText = "한줄서평";

        const text = document.createElement("span");
        text.classList.add("review-text");
        text.innerText = review.content;

        reviewRow.appendChild(label);
        reviewRow.appendChild(text);

        const authorRow = document.createElement("div");
        authorRow.classList.add("review-row");

        const authorLabel = document.createElement("span");
        authorLabel.classList.add("review-label");
        authorLabel.innerText = "작성자";

        const authorText = document.createElement("span");
        authorText.classList.add("review-author");
        authorText.innerText = review.userName;

        authorRow.appendChild(authorLabel);
        authorRow.appendChild(authorText);

        const footer = document.createElement("div");
        footer.classList.add("review-footer");

        if (review.owner) {
            const editBtn = document.createElement("button");
            editBtn.classList.add("btn-edit", "edit-btn");
            editBtn.innerText = "수정";
            editBtn.dataset.id = review.id;
            editBtn.dataset.content = review.content;
            editBtn.addEventListener("click", () => openModifyModal(review.id, review.content));

            const deleteBtn = document.createElement("button");
            deleteBtn.classList.add("btn-delete", "delete-btn");
            deleteBtn.innerText = "삭제";
            deleteBtn.dataset.id = review.id;
            deleteBtn.addEventListener("click", () => openDeleteModal(review.id));

            footer.appendChild(editBtn);
            footer.appendChild(deleteBtn);
        }

        contentBox.appendChild(header);
        contentBox.appendChild(divider);
        contentBox.appendChild(reviewRow);
        contentBox.appendChild(authorRow);
        contentBox.appendChild(footer);

        reviewCard.appendChild(bookImage);
        reviewCard.appendChild(contentBox);

        container.appendChild(reviewCard);
    });

    window.scrollTo({ top: 0, behavior: "smooth" }); // 페이지 이동 시 최상단으로 스크롤 이동
}

function setupPagination(reviews, currentPage) {
    const paginationContainer = document.getElementById("paginationContainer");
    paginationContainer.innerHTML = "";

    const reviewsPerPage = 5;
    const totalPages = Math.ceil(reviews.length / reviewsPerPage);

    const createPageItem = (text, page, disabled = false) => {
        const pageItem = document.createElement("li");
        pageItem.classList.add("page-item");
        if (disabled) pageItem.classList.add("disabled");

        const pageLink = document.createElement("a");
        pageLink.classList.add("page-link");
        pageLink.href = "#";
        pageLink.innerText = text;

        if (!disabled) {
            pageLink.addEventListener("click", (event) => {
                event.preventDefault();
                setupPagination(reviews, page);
                displayReviews(reviews, page, reviewsPerPage);
            });
        }

        pageItem.appendChild(pageLink);
        return pageItem;
    };

    // << 버튼
    paginationContainer.appendChild(createPageItem("<<", 1, currentPage === 1));

    for (let i = 1; i <= totalPages; i++) {
        const pageItem = createPageItem(i, i);
        if (i === currentPage) pageItem.classList.add("active");
        paginationContainer.appendChild(pageItem);
    }

    // >> 버튼
    paginationContainer.appendChild(createPageItem(">>", totalPages, currentPage === totalPages));

    displayReviews(reviews, currentPage, reviewsPerPage);
}

function updateReviewWriteButton() {
    const writeButton = document.getElementById("openModifyModalBtn");
    const currentPath = window.location.pathname;

    if (currentPath.startsWith("/review/book/")) {
        writeButton.style.display = "block"; // 도서별 리뷰 조회에서만 보이게 설정
    } else {
        writeButton.style.display = "none"; // 다른 페이지에서는 숨김
    }
}
