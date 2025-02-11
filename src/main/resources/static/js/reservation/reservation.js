function createReservation() {
    const userId = document.getElementById('userId').value;
    const bookId = document.getElementById('bookId').value;

    fetch('/api/v1/reservations', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ userId, bookId })
    })
        .then(response => response.json())
        .then(data => alert('예약 완료: ' + JSON.stringify(data)))
        .catch(error => alert('예약 실패: ' + error));
}
