document.addEventListener("DOMContentLoaded", function () {
    const searchButton = document.getElementById("search-button");
    const searchInput = document.getElementById("search-input");
    const searchTypeSelect = document.getElementById("search-type");  // 검색 유형 추가

    // 페이지 로드 시 전체 목록 불러오기
    fetchBooks();

    searchButton.addEventListener("click", searchBooks);
    searchInput.addEventListener("keypress", function (event) {
        if (event.key === "Enter") {
            searchBooks();
        }
    });

    function searchBooks() {
        const query = searchInput.value.trim();
        const searchType = searchTypeSelect.value; // 선택된 검색 유형을 가져옴

        if (query === "") {
            alert("검색어를 입력하세요.");
            return;
        }

        // 검색 조건에 맞춰 서버에 요청
        fetch(`/api/v1/books?query=${encodeURIComponent(query)}&searchType=${encodeURIComponent(searchType)}`)
            .then(response => response.json())
            .then(data => {
                renderBookList(data);
            })
            .catch(error => {
                console.error("도서 목록을 불러오는 중 오류 발생:", error);
            });
    }

    function fetchBooks() {
        fetch("/api/v1/books") // 전체 목록 API 호출
            .then(response => response.json())
            .then(data => {
                renderBookList(data);
            })
            .catch(error => {
                console.error("전체 도서 목록을 불러오는 중 오류 발생:", error);
            });
    }

    function renderBookList(books) {
        const bookList = document.getElementById("book-list");
        bookList.innerHTML = "";

        document.getElementById("search-result-count").textContent = books.length;

        books.forEach(book => {
            const row = document.createElement("tr");

            row.innerHTML = `
                <td><img src="${book.thumbnail}" alt="표지" class="thumbnail-img"></td>
                <td>${book.title}</td>
                <td>${book.author}</td>
                <td>${book.publisher}</td>
                <td>${book.publishedDate}</td>
                <td>${book.isbn}</td>
                <td>${book.isAvailable ? "대출 불가" : "대출 가능"}</td>
            `;

            bookList.appendChild(row);
        });
    }
});
