import { fetchUnreadNotifications } from "/js/notification/fetchUnreadNotifications.js";
import {fetchUserId} from "/js/notification/fetchUser.js";

async function initializeWebSocket() {
    const userId = await fetchUserId();
    if (!userId) {
        console.error("WebSocket 초기화 실패: 사용자 ID 없음");
        return;
    }

    const socket = new SockJS('/ws');
    const stompClient = Stomp.over(socket);

    stompClient.connect({}, async () => {
        console.log("WebSocket 연결 성공!! User ID:", userId);

        // 서버에서 읽지 않은 알림 가져오기 & Toastify 실행
        const notifications = await fetchUnreadNotifications();
        notifications.forEach(notification => showToast(notification));

        // WebSocket 구독 (새로운 알림 수신 시 Toastify 실행)
        stompClient.subscribe(`/topic/reservations/${userId}`, (message) => {
            const notification = JSON.parse(message.body);
            console.log("새 알림 수신:", notification);
            showToast(notification);
        });
    }, (error) => {
        console.error("WebSocket 연결 실패:", error);
    });
}


function showToast(notification) {
    Toastify({
        text: `알림 (${notification.id}): ${notification.message}`,
        duration: -1,
        close: true,
        gravity: "top",
        position: "center",
        style: {
            background: "linear-gradient(to right, #623F3F, #4F2D2D)",
        },
        callback: async function() {
            console.log("알림 삭제 요청:", notification.id);
            await deleteNotification(notification.id);
        }
    }).showToast();
}

// 알림 삭제 API 호출
export async function deleteNotification(notificationId) {
    try {
        const response = await fetch(`/api/v1/notifications/${notificationId}`, {
            method: "DELETE",
            credentials: "include",
        });

        if (!response.ok) {
            throw new Error("알림 삭제 실패");
        }

        console.log(`알림 삭제 완료 - ID: ${notificationId}`);
    } catch (error) {
        console.error("알림 삭제 중 오류 발생:", error);
    }
}


document.addEventListener("DOMContentLoaded", initializeWebSocket);
