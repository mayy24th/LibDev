export function showAlertToast(message) {
    /*console.log("showAlertToast 함수 실행됨:", message);*/
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