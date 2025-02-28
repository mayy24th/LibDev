import { fetchUserId } from "./fetchUser.js";

export async function fetchUnreadNotifications() {
    try {
        const userId = await fetchUserId();
        if (!userId) {
            return;
        }

        const response = await fetch(`/api/v1/notifications/unread/${userId}`, {
            method: "GET",
            credentials: "include"
        });

        if (!response.ok) {
            console.warn(`읽지 않은 알림을 불러오는 데 실패했습니다. 상태 코드: ${response.status}`);
            return [];
        }
        return await response.json();

    } catch (error) {
        console.error("알림 가져오기 실패:", error);
        return [];
    }
}

// 페이지 로드 시 저장된 알림 불러오기
document.addEventListener("DOMContentLoaded", fetchUnreadNotifications);
