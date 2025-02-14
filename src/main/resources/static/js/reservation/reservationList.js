document.addEventListener("DOMContentLoaded", function() {
    fetchReservations();
});

function fetchReservations() {
        const token = localStorage.getItem('jwt'); // LocalStorage에서 JWT 토큰 가져오기
        if (!token) {
            console.error("JWT 토큰이 존재하지 않습니다.");
            alert("로그인이 필요합니다.");
            return; // 요청 중단
        }

        fetch('/api/v1/reservations', {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`, // JWT 토큰 추가
                'Content-Type': 'application/json'
            }
        })
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            console.log("예약 내역 응답:", data); // 응답 확인
            const reservationList = document.getElementById('reservationList');
            reservationList.innerHTML = '';

            if (!Array.isArray(data)) {
                console.error("API 응답이 배열이 아닙니다:", data);
                return;
            }

            data.forEach(reservation => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${reservation.reservationId}</td>
                    <td>${reservation.bookTitle}</td>
                    <td>${reservation.author}</td>
                    <td>${reservation.status}</td>
                    <td>${reservation.reservedDate}</td>
                    <td>${reservation.expirationDate}</td>
                    <td><button onclick="cancelReservation(${reservation.reservationId})">취소</button></td>
                `;
                reservationList.appendChild(row);
            });
        })
        .catch(error => console.error('예약 내역을 불러오는 중 오류 발생:', error));
}

function cancelReservation(reservationId) {
    fetch(`/api/v1/reservations/${reservationId}`, {
        method: 'DELETE',
    })
        .then(response => response.text())
        .then(message => {
            alert(message);
            fetchReservations();
        })
        .catch(error => alert('예약 취소 실패: ' + error));
}
