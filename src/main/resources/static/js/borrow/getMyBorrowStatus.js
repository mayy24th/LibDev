document.addEventListener("DOMContentLoaded", function () {
    fetchCurrentBorrows();
});

async function fetchCurrentBorrows() {
    try {
        const response = await fetch("/api/v1/my/borrow-status", {
            method: "GET",
            credentials:"include"
        });

        if (!response.ok) {
            throw new Error('대출 현황 조회 실패');
        }

        const data = await response.json();
        renderCurrentBorrows(data);
    } catch (error) {
        console.error('Error:', error);
    }
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
                <div class="borrow-info" id="borrow-info-${borrow.id}">
                    <p>대출일: <span>${formatDate(borrow.borrowDate)}</span></p>
                    <p>반납 예정일: <span id="duedate-${borrow.id}">${formatDate(borrow.dueDate)}</span></p>
                    <p>상태: <span>${borrow.status}</span></p>
                    ${borrow.extended ? '<p class="extended-info">연장 완료</p>' : ''}
                </div>
            </div>
            <div class="buttons">
                ${!borrow.extended && borrow.borrowAvailable ? `
                <button class="btn btn-primary" id="extend-btn-${borrow.id}" onclick="extendBorrow(${borrow.id})">연장</button>
                ` : ''}
                <button class="btn btn-primary">반납 신청</button>
            </div>
        </div>`
    ).join("");
}

function formatDate(dateString) {
    return dateString.split('T')[0];
}