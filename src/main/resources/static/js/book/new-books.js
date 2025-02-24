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

        // << 버튼 (첫 페이지로 이동)
        const prevPageItem = document.createElement("li");
        prevPageItem.classList.add("page-item");
        const prevPageLink = document.createElement("a");
        prevPageLink.classList.add("page-link");
        prevPageLink.href = "#";
        prevPageLink.innerHTML = "<<";
        prevPageLink.addEventListener("click", function (event) {
            event.preventDefault();
            currentPage = 1;
            renderBookList();
        });
        prevPageItem.appendChild(prevPageLink);
        pagination.appendChild(prevPageItem);

        // 페이지 번호들
        const pageGroupSize = 5;
        const startPage = Math.floor((currentPage - 1) / pageGroupSize) * pageGroupSize + 1;
        const endPage = Math.min(startPage + pageGroupSize - 1, totalPages);

        for (let i = startPage; i <= endPage; i++) {
            const pageItem = document.createElement("li");
            pageItem.classList.add("page-item");
            if (i === currentPage) pageItem.classList.add("active");

            const pageLink = document.createElement("a");
            pageLink.classList.add("page-link");
            pageLink.href = "#";
            pageLink.textContent = i;
            pageLink.addEventListener("click", function (event) {
                event.preventDefault();
                currentPage = i;
                renderBookList();
            });

            pageItem.appendChild(pageLink);
            pagination.appendChild(pageItem);
        }

        // >> 버튼 (다음 페이지로 이동)
        const nextPageItem = document.createElement("li");
        nextPageItem.classList.add("page-item");
        const nextPageLink = document.createElement("a");
        nextPageLink.classList.add("page-link");
        nextPageLink.href = "#";
        nextPageLink.innerHTML = ">>";
        nextPageLink.addEventListener("click", function (event) {
            event.preventDefault();
            currentPage = Math.min(currentPage + 1, totalPages);
            renderBookList();
        });
        nextPageItem.appendChild(nextPageLink);
        pagination.appendChild(nextPageItem);
    }
});