export function showAlertToast(message, notificationId = null) {
    const toast = Toastify({
        text: message,
        duration: -1,
        close: true,
        gravity: "top",
        position: "center",
        style: {
            background: "linear-gradient(to right, #623F3F, #4F2D2D)",
        },
        onClick: function() {
            console.log("알림 클릭:", message);
        }
    });

    toast.showToast();

    // X 버튼을 눌렀을 때 알림 삭제 (DB에서 삭제)
    if (notificationId) {
        document.querySelector(".toastify-close").addEventListener("click", async () => {
            await deleteNotification(notificationId);
        });
    }
}

// API 요청으로 DB에서 알림 삭제
async function deleteNotification(notificationId) {
    try {
        const response = await fetch(`/api/v1/notifications/${notificationId}`, {
            method: "DELETE",
        });

        if (!response.ok) {
            console.error(`알림 삭제 실패: ${response.status}`);
            return;
        }


    } catch (error) {
        console.error("알림 삭제 중 오류 발생:", error);
    }
}
