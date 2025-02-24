document.addEventListener("DOMContentLoaded", function () {
    const searchButton = document.getElementById("search-button");
    const searchInput = document.getElementById("search-input");
    const searchTypeSelect = document.getElementById("search-type");

    let booksData = []; // 전체 도서 데이터
    let currentPage = 1;
    const booksPerPage = 10;

    fetchBooks();

    searchButton.addEventListener("click", searchBooks);
    searchInput.addEventListener("keypress", function (event) {
        if (event.key === "Enter") {
            searchBooks();
        }
    });

    function searchBooks() {
        const query = searchInput.value.trim();
        const searchType = searchTypeSelect.value;

        if (query === "") {
            fetchBooks();
            return;
        }

        fetch(`/api/v1/books?query=${encodeURIComponent(query)}&searchType=${encodeURIComponent(searchType)}`)
            .then(response => response.json())
            .then(data => {
                booksData = data;
                currentPage = 1;
                renderBookList();
            })
            .catch(error => console.error("도서 목록을 불러오는 중 오류 발생:", error));
    }

    function fetchBooks() {
        fetch("/api/v1/books")
            .then(response => response.json())
            .then(data => {
                booksData = data;
                renderBookList();
            })
            .catch(error => console.error("전체 도서 목록을 불러오는 중 오류 발생:", error));
    }

    function renderBookList() {
        const bookList = document.getElementById("book-list");
        bookList.innerHTML = "";

        const start = (currentPage - 1) * booksPerPage;
        const end = start + booksPerPage;
        const booksToDisplay = booksData.slice(start, end);

        const resultCountElement = document.getElementById("search-result-count");
        if (resultCountElement) {
            const searchType = searchTypeSelect.value;
            const query = searchInput.value.trim();
            if (query) {
                resultCountElement.textContent = `${searchType}: "${query}"에 대한 검색결과는 총 ${booksData.length}건입니다.`;
            } else {
                resultCountElement.textContent = `전체 도서는 총 ${booksData.length}건입니다.`;
            }
        }

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
