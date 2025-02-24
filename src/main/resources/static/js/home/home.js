window.onload = () => {
    fetchRecommendedBooks();
};

// 추천 도서 불러오기 함수
async function fetchRecommendedBooks() {
    try {
        const bookId = window.location.pathname.split("/").pop();
        const response = await fetch(`/api/recommendation/user`);
        const data = await response.json();

        const bookList = document.querySelector('.book-list');
        bookList.innerHTML = "";

        if (!data || data.length === 0) {
            bookList.insertAdjacentHTML("beforeend", "<p>추천 도서가 없습니다.</p>");
            return;
        }

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
