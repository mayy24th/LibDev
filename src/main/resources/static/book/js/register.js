// 검색 함수 (도서 검색 후 결과를 표시)
function searchBooks() {
    const query = document.getElementById('searchQuery').value;

    fetch(`/api/v1/books/search?query=${query}`)
        .then(response => response.json())
        .then(data => {
            const resultsContainer = document.getElementById('searchResults');
            resultsContainer.innerHTML = ''; // 기존 검색 결과 초기화

            data.forEach(book => {
                const row = document.createElement('tr');

                // 도서명, 저자, 출판사, 썸네일
                row.innerHTML = `
                    <td>${book.title}</td>
                    <td>${book.author}</td>
                    <td>${book.publisher}</td>
                    <td><img src="${book.thumbnail}" alt="썸네일" width="50" height="80"></td>
                    <td><button onclick="selectBook('${book.title}', '${book.author}', '${book.publisher}', '${book.publishedDate}', '${book.isbn}', '${book.callNumber}', '${book.contents}', '${book.thumbnail}')">선택</button></td>
                `;

                resultsContainer.appendChild(row);
            });
        })
        .catch(error => console.error('Error:', error));
}

// 도서 선택 함수 (선택한 도서 정보를 상세 정보에 표시)
function selectBook(title, author, publisher, publishedDate, isbn, callNumber, contents, thumbnail) {
    document.getElementById('bookTitle').textContent = title;
    document.getElementById('bookAuthor').textContent = author;
    document.getElementById('bookPublisher').textContent = publisher;
    document.getElementById('bookPublishedDate').textContent = publishedDate;
    document.getElementById('bookIsbn').textContent = isbn;
    document.getElementById('bookCallNumber').textContent = callNumber;
    document.getElementById('bookContents').textContent = contents;
    document.getElementById('bookThumbnail').src = thumbnail;

    // 국립중앙도서관 API에서 청구기호와 주제 ID 가져오기
    fetch(`/api/v1/books/library-info?isbn=${isbn}`)
        .then(response => response.json())
        .then(data => {
            document.getElementById('bookCallNumber').textContent = data.callNumber || '정보 없음';
            document.getElementById('bookTopicId').textContent = data.topicId || '정보 없음';
        })
        .catch(error => console.error('Error fetching library info:', error));
}

// 도서 등록 함수
function registerBook() {
    const title = document.getElementById('bookTitle').textContent;
    const author = document.getElementById('bookAuthor').textContent;
    const publisher = document.getElementById('bookPublisher').textContent;
    const publishedDate = document.getElementById('bookPublishedDate').textContent;
    const isbn = document.getElementById('bookIsbn').textContent;
    const callNumber = document.getElementById('bookCallNumber').textContent;
    const topicId = document.getElementById('bookTopicId').textContent;
    const contents = document.getElementById('bookContents').textContent;
    const thumbnail = document.getElementById('bookThumbnail').src;

    fetch('/api/v1/books/register', {
        method: 'POST',
        body: JSON.stringify({ title, author, publisher, publishedDate, isbn, callNumber, topicId, contents, thumbnail }),
        headers: { 'Content-Type': 'application/json' },
        credentials: 'same-origin',
    })
        .then(response => response.json())
        .then(data => alert('도서 등록 성공!'))
        .catch(error => alert('도서 등록 실패'));
}
