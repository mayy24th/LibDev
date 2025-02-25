export function showAlertToast(message) {
    Toastify({
        text: message,
        duration: -1,
        close: true,
        gravity: "top",
        position: "center",
        style: {
            background: "linear-gradient(to right, #623F3F, #4F2D2D)",
        }
    }).showToast();
}