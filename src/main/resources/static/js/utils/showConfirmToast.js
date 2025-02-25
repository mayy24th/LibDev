export function showConfirmToast(message, onConfirm, onCancel) {
    const toast = Toastify({
        text: `
            <div style="text-align: center; font-size: 16px;">
                <p>${message}</p>
                <button id="confirmBtn" class="toast-btn confirm">확인</button>
                <button id="cancelBtn" class="toast-btn cancel">취소</button>
            </div>
        `,
        duration: -1,
        gravity: "top",
        position: "center",
        escapeMarkup: false,
        style: {
            background: "linear-gradient(to right, #623F3F, #4F2D2D)",
            padding: "18px",
            borderRadius: "2px"
        },
    }).showToast();

    // 버튼 이벤트 바인딩
    setTimeout(() => {
        document.getElementById("confirmBtn").addEventListener("click", () => {
            toast.hideToast();
            if (onConfirm) onConfirm();
        });
        document.getElementById("cancelBtn").addEventListener("click", () => {
            toast.hideToast();
            if (onCancel) onCancel();
        });
    }, 100); // DOM이 렌더링될 때까지 약간의 딜레이 필요
}

