window.onload = () => {
    const currentPath = window.location.pathname;

    let apiEndpoint = "";
    let pageTitle = "도서 목록";

    if (currentPath.includes("/recommendation/popular")) {
        apiEndpoint = "/api/recommendation/popular";
        pageTitle = "인기 도서";
    } else if (currentPath.includes("/recommendation/list")) {
        apiEndpoint = "/api/recommendation/user";
        pageTitle = "추천 도서";
    }

    document.title = pageTitle;
    document.getElementById("page-title").textContent = pageTitle;

    if (apiEndpoint) {
        fetchBooks(apiEndpoint);
    }
};

async function fetchBooks(apiEndpoint) {
    try {
        const response = await fetch(apiEndpoint);
        const data = await response.json();

        const bookList = document.querySelector(".book-list");
        bookList.innerHTML = ""; // 기존 목록 초기화

        if (!data || data.length === 0) {
            displayNoBooksMessage(bookList);
            return;
        }

        renderBooks(data, bookList);
    } catch (error) {
        console.error("도서 목록 불러오기 실패:", error);
    }
}

function displayNoBooksMessage(container) {
    const message = document.createElement("p");
    message.textContent = "도서가 없습니다.";
    container.appendChild(message);
}

function renderBooks(books, container) {
    books.forEach(book => {
        const bookLink = document.createElement("a");
        bookLink.href = `/books/${book.bookId}`;
        bookLink.classList.add("book-link");

        const bookCard = document.createElement("div");
        bookCard.classList.add("book-card");

        const bookThumbnail = document.createElement("div");
        bookThumbnail.classList.add("book-thumbnail");
        const thumbnailImage = document.createElement("img");
        thumbnailImage.src = book.thumbnail;
        thumbnailImage.alt = `${book.title} 이미지`;
        bookThumbnail.appendChild(thumbnailImage);

        const bookInfo = document.createElement("div");
        bookInfo.classList.add("book-info");

        const bookTitle = document.createElement("h3");
        bookTitle.classList.add("book-title");
        bookTitle.textContent = book.title;

        const bookMeta = document.createElement("div");
        bookMeta.classList.add("book-meta-inline");
        bookMeta.insertAdjacentHTML("beforeend", `<span><strong>저자:</strong> ${book.author}</span>`);
        bookMeta.insertAdjacentHTML("beforeend", `<span><strong>출판사:</strong> ${book.publisher}</span>`);
        bookMeta.insertAdjacentHTML("beforeend", `<span><strong>발행일:</strong> ${book.publishedDate}</span>`);

        const divider = document.createElement("hr");
        divider.classList.add("book-divider");

        const bookDescription = document.createElement("p");
        bookDescription.classList.add("book-description");
        bookDescription.textContent = book.contents;

        bookInfo.appendChild(bookTitle);
        bookInfo.appendChild(bookMeta);
        bookInfo.appendChild(divider);
        bookInfo.appendChild(bookDescription);

        bookCard.appendChild(bookThumbnail);
        bookCard.appendChild(bookInfo);

        bookLink.appendChild(bookCard);
        container.appendChild(bookLink);
    });
}
