window.onload = function() {
    const topicId = window.location.pathname.split('/').pop();
    const topicName = getTopicName(topicId);

    document.getElementById('topicName').textContent = topicName;

    fetch(`/api/v1/books/search-topic/${topicId}`)
        .then(response => response.json())
        .then(data => {
            const bookList = document.getElementById('bookList');
            data.forEach(book => {
                const card = document.createElement('div');
                card.className = 'col';
                card.innerHTML = `
                    <div class="card h-100 text-center p-2">
                        <img src="${book.thumbnail || '/images/bookImage.jpg'}" class="card-img-top" alt="썸네일" style="max-height: 150px; object-fit: contain;">
                        <div class="card-body">
                            <h5 class="card-title">${book.title}</h5>
                            <p class="card-text">${book.author} | ${book.publisher}</p>
                            <button class="btn custom-btn-outline" onclick="selectBook('${book.title}', '${book.author}', '${book.publisher}', '${book.publishedDate}', '${book.isbn}', '${book.callNumber}', '${book.contents}', '${book.thumbnail}', '${book.topicId}')">선택</button>
                        </div>
                    </div>
                `;
                bookList.appendChild(card);
            });
        });
};

function getTopicName(topicId) {
    const topics = [
        "총류", "철학", "종교", "사회과학", "자연과학", "기술과학", "예술", "언어", "문학", "역사"
    ];
    return topics[topicId];
}

function selectBook(title, author, publisher, publishedDate, isbn, callNumber, contents, thumbnail, topicId) {
    // 도서 선택 로직
}
