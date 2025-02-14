document.addEventListener("DOMContentLoaded", function () {
    fetchCurrentBorrows();
});

function fetchCurrentBorrows() {
    fetch('/api/v1/my/borrow-status')
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to fetch borrow list');
            }
            return response.json();
        })
        .then(data => renderCurrentBorrows(data))
        .catch(error => console.error('Error:', error));
}

function renderCurrentBorrows(borrows) {
    const borrowContainer = document.querySelector(".borrow-list");

    if (!borrows || borrows.length === 0) { // 데이터가 없을 경우
        borrowContainer.innerHTML = `<p>현재 대출 중인 도서가 없습니다.</p>`;
        return;
    }

    borrowContainer.innerHTML = borrows.map(borrow =>
        `<div class="borrow-box">
            <div class="borrow-content-box">
                <p>${borrow.bookTitle}</p>
                <div class="borrow-info">
                    <p>대출일: <span>${formatDate(borrow.borrowDate)}</span></p>
                    <p>반납 예정일: <span>${formatDate(borrow.dueDate)}</span></p>
                    <p>상태: <span>${borrow.status}</span></p>
                </div>
            </div>
            <div class="buttons">
                <button class="btn btn-primary">연장</button>
                <button class="btn btn-primary">반납 신청</button>
            </div>
        </div>`
    ).join("");
}

function formatDate(dateString) {
    return dateString.split('T')[0];
}