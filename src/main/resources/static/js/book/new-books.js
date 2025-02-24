document.addEventListener("DOMContentLoaded", function () {
    let booksData = [];
    let currentPage = 1;
    const booksPerPage = 10;
    const searchResultCount = document.getElementById("search-result-count");

    fetchBooks();

    function fetchBooks() {
        fetch("/api/v1/books/new")
            .then(response => response.json())
            .then(data => {
                booksData = data;
                searchResultCount.textContent = booksData.length;
                renderBookList();
            })
            .catch(error => console.error("신착 도서 목록을 불러오는 중 오류 발생:", error));
    }

    function renderBookList() {
        const bookList = document.getElementById("book-list");
        bookList.innerHTML = "";

        const start = (currentPage - 1) * booksPerPage;
        const end = start + booksPerPage;
        const booksToDisplay = booksData.slice(start, end);

        booksToDisplay.forEach(book => {
            const bookLink = document.createElement("a");
            bookLink.href = `/books/${book.bookId}`;
            bookLink.classList.add("book-link");

            const bookCard = document.createElement("div");
            bookCard.classList.add("book-card");

            const bookThumbnail = document.createElement("div");
            bookThumbnail.classList.add("book-thumbnail");
            const thumbnailImage = document.createElement("img");
            thumbnailImage.src = book.thumbnail || '/images/bookImage.jpg';
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
            bookList.appendChild(bookLink);
        });

        renderPagination();
    }

    function renderPagination() {
        const pagination = document.getElementById("pagination");
        pagination.innerHTML = "";

        const totalPages = Math.ceil(booksData.length / booksPerPage);
        const pageGroupSize = 5;
        const startPage = Math.floor((currentPage - 1) / pageGroupSize) * pageGroupSize + 1;
        const endPage = Math.min(startPage + pageGroupSize - 1, totalPages);

        // << 버튼 (앞 그룹의 마지막 페이지로 이동)
        const prevPageItem = createPageItem("<<", startPage - 1, startPage === 1);
        pagination.appendChild(prevPageItem);

        // 페이지 번호들
        for (let i = startPage; i <= endPage; i++) {
            const pageItem = createPageItem(i, i, false);
            if (i === currentPage) pageItem.classList.add("active");
            pagination.appendChild(pageItem);
        }

        // >> 버튼 (다음 그룹의 첫 페이지로 이동)
        const nextPageItem = createPageItem(">>", endPage + 1, endPage >= totalPages);
        pagination.appendChild(nextPageItem);
    }

    // 페이지 아이템 생성 함수
    function createPageItem(text, page, disabled = false) {
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
                currentPage = page;
                renderBookList();
            });
        }

        pageItem.appendChild(pageLink);
        return pageItem;
    }
});