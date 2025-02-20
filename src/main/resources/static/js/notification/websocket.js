async function fetchUserId() {
    try {
        const response = await fetch("/api/v1/auths/me", {
            method: "GET",
            credentials: "include",
            headers: {
                "Content-Type": "application/json"
            }
        });

        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }

        const data = await response.json();
        return data.userId;
    } catch (error) {
        console.error("사용자 ID를 가져오는 중 오류 발생:", error);
        return null;
    }
}

async function initializeWebSocket() {
    const userId = await fetchUserId();
    if (!userId) {
        console.error("WebSocket 초기화 실패: 사용자 ID 없음");
        return;
    }

    const socket = new SockJS('/ws');
    const stompClient = Stomp.over(socket);

    stompClient.connect({}, () => {
        console.log("WebSocket 연결 성공! User ID:", userId);

        // WebSocket 구독 (userId를 기반으로 구독)
        stompClient.subscribe(`/topic/reservations/${userId}`, (message) => {
            const notification = JSON.parse(message.body);

            // Toastify 알림 표시
            Toastify({
                text: notification.message,
                duration: 20000,
                close: true,
                gravity: "top",
                position: "center",
                style: {
                    background: "linear-gradient(to right, #623F3F, #4F2D2D)",
                }
            }).showToast();

        });
    }, (error) => {
        console.error("WebSocket 연결 실패:", error);
    });
}

// 페이지 로딩 시 WebSocket 초기화
document.addEventListener("DOMContentLoaded", initializeWebSocket);



