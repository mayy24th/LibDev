// 도서 검색
function searchBooks() {
    const query = document.getElementById("searchQuery").value;
    fetch(`/api/v1/books/search?query=${query}`, {
        method: "GET",
        credentials: "include" // 로그인된 세션 유지
    })
        .then(response => response.json())
        .then(data => {
            const resultsTable = document.getElementById("searchResults");
            resultsTable.innerHTML = "";  // 기존 검색 결과 초기화

            data.forEach(book => {
                const row = `<tr>
                    <td>${book.title}</td>
                    <td>${book.author}</td>
                    <td>${book.publisher}</td>
                    <td><button onclick="selectBook(${JSON.stringify(book)})">선택</button></td>
                </tr>`;
                resultsTable.innerHTML += row;
            });
        })
        .catch(error => console.error("도서 검색 실패:", error));
}

// 선택한 도서 정보 표시
function selectBook(book) {
    document.getElementById("bookTitle").innerText = book.title;
    document.getElementById("bookAuthor").innerText = book.author;
    document.getElementById("bookPublisher").innerText = book.publisher;
    document.getElementById("bookPublishedDate").innerText = book.publishedDate;
    document.getElementById("bookIsbn").innerText = book.isbn;
    document.getElementById("bookCallNumber").innerText = book.callNumber;
    document.getElementById("bookContents").innerText = book.contents.substring(0, 200);
}

// 도서 등록
function registerBook() {
    const bookData = {
        title: document.getElementById("bookTitle").innerText,
        author: document.getElementById("bookAuthor").innerText,
        publisher: document.getElementById("bookPublisher").innerText,
        publishedDate: document.getElementById("bookPublishedDate").innerText,
        isbn: document.getElementById("bookIsbn").innerText,
        callNumber: document.getElementById("bookCallNumber").innerText,
        contents: document.getElementById("bookContents").innerText
    };

    fetch("/api/v1/books/register", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(bookData)
    })
        .then(response => {
            if (response.ok) {
                alert("도서가 성공적으로 등록되었습니다.");
            } else {
                alert("도서 등록 실패!");
            }
        })
        .catch(error => console.error("도서 등록 중 오류 발생:", error));
}

