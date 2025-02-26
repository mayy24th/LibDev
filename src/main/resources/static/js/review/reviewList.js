import { openModifyModal } from "./reviewModify.js";
import { openDeleteModal } from "./reviewDelete.js";
import { showAlertToast } from "../utils/showAlertToast.js"

export async function loadReviews(apiEndpoint) {
    try {
        const response = await fetch(apiEndpoint, { method: "GET" });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message);
        }

        const reviews = await response.json();
        reviews.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));

        updateReviewWriteButton();

        if (reviews.length === 0) {
            displayReviews([]);
            return;
        }

        setupPagination(reviews, 1); // 첫 페이지 초기화
    } catch (error) {
        console.error("한줄평 목록 불러오기 실패:", error);
        showAlertToast(error.message);
    }
}

export function displayReviews(reviews, page = 1, reviewsPerPage = 5) {
    const container = document.getElementById("reviewContainer");
    container.innerHTML = "";

    if (reviews.length === 0) {
        // 등록된 한줄평이 없는 경우 메시지 표시
        const noReviewsMessage = document.createElement("p");
        noReviewsMessage.textContent = "등록된 한줄평이 없습니다.";
        noReviewsMessage.style.textAlign = "center";
        noReviewsMessage.style.color = "#666";
        noReviewsMessage.style.fontSize = "16px";
        container.appendChild(noReviewsMessage);

        // 페이지네이션 숨기기
        document.getElementById("paginationContainer").style.display = "none";
        return;
    }

    // 페이지네이션이 필요한 경우 다시 보이게 설정
    document.getElementById("paginationContainer").style.display = "flex";

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
        header.appendChild(document.createTextNode(review.bookName));

        const divider = document.createElement("hr");
        divider.classList.add("review-divider");

        const reviewRow = document.createElement("div");
        reviewRow.classList.add("review-row");

        const label = document.createElement("span");
        label.classList.add("review-label");
        label.appendChild(document.createTextNode("한줄평"));

        const text = document.createElement("span");
        text.classList.add("review-text");
        text.appendChild(document.createTextNode(review.content));

        reviewRow.appendChild(label);
        reviewRow.appendChild(text);

        const authorRow = document.createElement("div");
        authorRow.classList.add("review-row");

        const authorLabel = document.createElement("span");
        authorLabel.classList.add("review-label");
        authorLabel.appendChild(document.createTextNode("작성자"));

        const authorText = document.createElement("span");
        authorText.classList.add("review-author");
        authorText.appendChild(document.createTextNode(review.userName));

        authorRow.appendChild(authorLabel);
        authorRow.appendChild(authorText);

        const footer = document.createElement("div");
        footer.classList.add("review-footer");

        if (review.owner) {
            const editBtn = document.createElement("button");
            editBtn.classList.add("btn-edit", "edit-btn");
            editBtn.appendChild(document.createTextNode("수정"));
            editBtn.dataset.id = review.id;
            editBtn.dataset.content = review.content;
            editBtn.addEventListener("click", () => openModifyModal(review.id, review.content));

            const deleteBtn = document.createElement("button");
            deleteBtn.classList.add("btn-delete", "delete-btn");
            deleteBtn.appendChild(document.createTextNode("삭제"));
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

    window.scrollTo({ top: 0, behavior: "smooth" });
}

export function updatePageInfo(reviews, currentPage, reviewsPerPage) {
    const totalReviews = reviews.length;
    const totalPages = Math.ceil(totalReviews / reviewsPerPage);
    const pageInfoElement = document.getElementById("pageInfo");

    // 페이지 정보 초기화
    pageInfoElement.innerHTML = "";

    // 전체 리뷰 개수
    const totalReviewsText = document.createElement("span");
    totalReviewsText.appendChild(document.createTextNode(`전체 ${totalReviews}개 (페이지 `));

    // 현재 페이지 강조
    const currentPageText = document.createElement("span");
    currentPageText.classList.add("current-page");
    currentPageText.appendChild(document.createTextNode(`${currentPage}`));

    // 전체 페이지 수
    const totalPageText = document.createElement("span");
    totalPageText.appendChild(document.createTextNode(`/${totalPages})`));

    // 요소 삽입
    pageInfoElement.appendChild(totalReviewsText);
    pageInfoElement.appendChild(currentPageText);
    pageInfoElement.appendChild(totalPageText);
}

function setupPagination(reviews, currentPage) {
    const paginationContainer = document.getElementById("paginationContainer");
    paginationContainer.innerHTML = "";

    const reviewsPerPage = 5; // 한 페이지에 표시할 리뷰 수
    const maxPagesToShow = 5; // 한번에 보여질 최대 페이지 수
    const totalPages = Math.ceil(reviews.length / reviewsPerPage);

    // 리뷰가 없는 경우 페이지네이션 숨기기
    if (reviews.length === 0) {
        paginationContainer.style.display = "none";
        return;
    } else {
        paginationContainer.style.display = "flex"; // 다시 보이도록 설정
    }

    const startPage = Math.floor((currentPage - 1) / maxPagesToShow) * maxPagesToShow + 1;
    const endPage = Math.min(startPage + maxPagesToShow - 1, totalPages);

    const hasPrevGroup = startPage > 1;
    const hasNextGroup = endPage < totalPages;

    const createPageItem = (text, page, disabled = false) => {
        const pageItem = document.createElement("li");
        pageItem.classList.add("page-item");
        if (disabled) pageItem.classList.add("disabled");

        const pageLink = document.createElement("a");
        pageLink.classList.add("page-link");
        pageLink.href = "#";
        pageLink.appendChild(document.createTextNode(text));

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

    if (totalPages > 0) {
        paginationContainer.appendChild(
            createPageItem("<<", startPage - 1, !hasPrevGroup)
        );
    }

    for (let i = startPage; i <= endPage; i++) {
        const pageItem = createPageItem(i, i);
        if (i === currentPage) pageItem.classList.add("active");
        paginationContainer.appendChild(pageItem);
    }

    if (totalPages > 0) {
        paginationContainer.appendChild(
            createPageItem(">>", endPage + 1, !hasNextGroup)
        );
    }

    updatePageInfo(reviews, currentPage, reviewsPerPage);
    displayReviews(reviews, currentPage, reviewsPerPage);
}


function updateReviewWriteButton() {
    const writeButton = document.getElementById("openModifyModalBtn");
    const currentPath = window.location.pathname;

    if (currentPath.startsWith("/review/book/")) {
        writeButton.style.display = "block";
    } else {
        writeButton.style.display = "none";
    }
}
