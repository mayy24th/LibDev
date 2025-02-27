import { fetchUnreadNotifications } from "./fetchUnreadNotifications.js";
import { fetchUserId } from "./fetchUser.js";
import {showAlertToast} from "../utils/showAlertToast";

async function initializeWebSocket() {
    const userId = await fetchUserId();
    if (!userId) {
        console.error("WebSocket 초기화 실패: 사용자 ID 없음");
        return;
    }

    const socket = new SockJS('/ws');
    const stompClient = Stomp.over(socket);

    stompClient.connect({}, async () => {
        // 서버에서 읽지 않은 알림 가져오기 & Toastify 실행
        const notifications = await fetchUnreadNotifications();
        notifications.forEach(notification => showToast(notification));

        // WebSocket 구독 (새로운 알림 수신 시 Toastify 실행)
        stompClient.subscribe(`/topic/reservations/${userId}`, (message) => {
            const notification = JSON.parse(message.body);
            showToast(notification);
        });
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
            showAlertToast("알림 삭제 실패");
            return;
        }

        console.log(`알림 삭제 완료 - ID: ${notificationId}`);
    } catch (error) {
        console.error("알림 삭제 중 오류 발생:", error);
    }
}


document.addEventListener("DOMContentLoaded", initializeWebSocket);
