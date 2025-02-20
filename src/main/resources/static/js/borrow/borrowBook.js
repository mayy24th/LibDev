const borrowButton = document.querySelector(".borrow-btn");

if (borrowButton) {
    borrowButton.addEventListener("click", function () {
        const bookId = this.dataset.bookId;
        borrowBook(bookId);
    });
}

async function borrowBook(bookId) {
    try {
        const response = await fetch(`/api/v1/borrow?bookId=${bookId}`, {
            method: "POST"
        });

        if (!response.ok) {
            alert("도서 대출 실패");
            throw new Error("도서 대출 실패");
        }

        updateBorrowBtn();
        alert("해당 도서가 대출되었습니다.");
    } catch (error) {
        console.error("Error:", error);
    }
}

function updateBorrowBtn() {
    const borrowButton = document.querySelector(".borrow-btn");

    if (borrowButton) {
        const parentDiv = borrowButton.parentNode;

        borrowButton.remove();

        const reserveButton = document.createElement("button");
        reserveButton.classList.add("btn", "btn-custom-1");
        reserveButton.textContent = "예약하기";

        parentDiv.insertBefore(reserveButton, parentDiv.children[0]);
    }
}
