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
                <p class="book-title">${borrow.bookTitle}</p>
                <div class="borrow-info" id="borrow-info-${borrow.id}">
                    <p>대출일: <span>${formatDate(borrow.borrowDate)}</span></p>
                    <p>반납 예정일: <span id="duedate-${borrow.id}">${formatDate(borrow.dueDate)}</span></p>
                    <p class="extended-text">연장: <span id="extended-status-${borrow.id}">${borrow.extended ? 'Y' : 'N'}</span></p>
                    <p>상태: <span class="${borrow.overdue ? 'overdue-text' : ''}" id="borrow-status-${borrow.id}">${borrow.status}</span>${borrow.overdue ? '<span class="overdue-days"></span>' : ''}</p> 
                </div>
            </div>
            <div class="buttons">
                ${!borrow.extended && borrow.borrowAvailable ? `
                <button class="btn btn-primary extend-btn" id="extend-btn-${borrow.id}" data-borrow-id="${borrow.id}">연장</button>
                ` : ''}
                <button class="btn btn-primary return-btn" id="return-btn-${borrow.id}" 
                data-borrow-id="${borrow.id}" data-borrow-status="${borrow.status}">반납 신청</button>
            </div>
        </div>`
    ).join("");

    document.querySelectorAll(".extend-btn").forEach(button => {
        button.addEventListener("click", function () {
            const borrowId = this.dataset.borrowId;
            extendBorrow(borrowId);
        });
    });

    document.querySelectorAll(".return-btn").forEach(button => {
        const borrowStatus = button.dataset.borrowStatus;

        if (borrowStatus === "반납 신청") {
            button.disabled = true;
        } else {
            button.addEventListener("click", function () {
                const borrowId = this.dataset.borrowId;
                requestReturn(borrowId);
            });
        }
    });
}

function formatDate(dateString) {
    return dateString.split('T')[0];
}