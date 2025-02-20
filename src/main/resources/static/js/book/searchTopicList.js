document.addEventListener("DOMContentLoaded", function () {
    let booksData = [];
    let currentPage = 1;
    const booksPerPage = 10;
    const searchResultCount = document.getElementById("search-result-count");
    const topicNameSpan = document.getElementById("topic-name");

    fetchBooks();

    const topicNames = {
        0: "총류",
        1: "철학",
        2: "종교",
        3: "사회과학",
        4: "자연과학",
        5: "기술과학",
        6: "예술",
        7: "언어",
        8: "문학",
        9: "역사"
    };

    function fetchBooks() {
        const topicId = window.location.pathname.split('/').pop();
        fetch(`/api/v1/books/search-topic/${topicId}`)
            .then(response => response.json())
            .then(data => {
                booksData = data;
                const topicName = topicNames[topicId];
                searchResultCount.textContent = booksData.length;
                topicNameSpan.textContent = topicName;
                renderBookList();
            })
            .catch(error => console.error("주제별 도서 목록을 불러오는 중 오류 발생:", error));
    }

    function renderBookList() {
        const bookList = document.getElementById("book-list");
        bookList.innerHTML = "";

        const start = (currentPage - 1) * booksPerPage;
        const end = start + booksPerPage;
        const booksToDisplay = booksData.slice(start, end);

        booksToDisplay.forEach(book => {
            console.log("도서 정보:", book);
            const listItem = document.createElement("div");
            listItem.classList.add("list-group-item", "p-3", "shadow-sm", "mb-3");
            listItem.style.cursor = "pointer";

            listItem.addEventListener("click", function () {
                window.location.href = `/books/${book.bookId}`;
            });

            listItem.innerHTML = `
                <div class="d-flex">
                    <img src="${book.thumbnail || '/images/bookImage.jpg'}" alt="표지" class="me-3" style="width: 80px; height: auto;">
                    <div>
                        <h5 class="fw-bold">${book.title}</h5>
                        <p class="mb-1 text-muted">저자: ${book.author} | 출판사: ${book.publisher} | 발행일: ${book.publishedDate}</p>
                        <p class="mb-1 text-muted">ISBN: ${book.isbn} | 청구기호: ${book.callNumber}</p>
                        <div class="p-2 mt-2" style="background-color: #f2f2f2;">
                            <span class="${book.isAvailable ? 'text-success' : 'text-danger'} fw-bold">
                                ${book.isAvailable ? '대출가능[비치중]' : '대출불가[대출중]'}
                            </span>
                            <span class="ms-3 text-muted">
                                ${book.isAvailable ? '도서 예약 불가' : '도서 예약 가능'}
                            </span>
                        </div>
                    </div>
                </div>    
            `;

            bookList.appendChild(listItem);
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
