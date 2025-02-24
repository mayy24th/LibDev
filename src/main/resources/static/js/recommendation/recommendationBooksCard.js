window.onload = () => {
    // 유사 도서 추천 (bookId 기반)
    const bookId = window.location.pathname.split("/").pop();
    if (bookId && !isNaN(bookId)) {
        fetchRecommendedBooks(`/api/recommendation/similar/${bookId}`);
    } else {
        // 사용자 기반 추천
        fetchRecommendedBooks(`/api/recommendation/user`);
    }
};

async function fetchRecommendedBooks(apiEndpoint) {
    try {
        const response = await fetch(apiEndpoint);
        const data = await response.json();

        const bookList = document.querySelector('.book-list');
        bookList.innerHTML = ""; // 기존 리스트 초기화

        // 데이터가 없을 경우 메시지 출력
        if (!data || data.length === 0) {
            bookList.insertAdjacentHTML("beforeend", "<p>추천 도서가 없습니다.</p>");
            return;
        }

        // 추천 도서 데이터를 카드 형태로 출력
        data.forEach(book => {
            const bookHTML = `
                <div class="book-card">
                    <a href="/books/${book.bookId}">
                        <img src="${book.thumbnail}" alt="책 이미지" class="book-thumbnail">
                        <div class="book-info">
                            <p class="book-title">${book.title}</p>
                            <p class="book-author">${book.author}</p>
                        </div>
                    </a>
                </div>
            `;
            bookList.insertAdjacentHTML("beforeend", bookHTML);
        });
    } catch (error) {
        console.error("추천 도서 불러오기 실패:", error);
    }
}
