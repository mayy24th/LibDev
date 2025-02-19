async function requestReturn(borrowId) {
    try {
        const response = await fetch(`/api/v1/return/${borrowId}`, {
            method: "PATCH"
        });

        if (!response.ok) {
            alert("반납 신청 요청 실패");
            throw new Error("반납 신청 요청 실패");
        }

        const data = await response.json();
        updateBorrowStatus(data);
        alert("반납 신청이 완료되었습니다.");
    } catch (error) {
        console.error("Error:", error);
    }
}

function updateBorrowStatus(borrow) {
    const button = document.querySelector(`#return-btn-${borrow.id}`);
    if (button) {
        button.disabled = true;
    }

    const borrowStatus = document.querySelector(`#borrow-status-${borrow.id}`);
    if (borrowStatus) {
        borrowStatus.textContent = borrow.status;
    }
}