import {showAlertToast} from "../utils/showAlertToast.js";
import {fetchUnreadNotifications} from "/js/notification/fetchUnreadNotifications.js";

const form = document.getElementById("loginForm");

form.addEventListener("submit", async (event) => {
    event.preventDefault();

    const formData = new FormData(form);
    const email = formData.get("email");
    const password = formData.get("password");

    try {
        const response = await  fetch("/api/v3/auths/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({email,password}),
        });

        const result = await response.json();
        if(result.code === 401){
            showAlertToast(result.data)
            return;
        }
        showAlertToast(result.data)

        // 로그인 성공 후 현재 유저의 userId 가져오기
        /*const userId = await getCurrentUserId();
        if (userId) {
            // userId가 존재하면 읽지 않은 알림 가져오기
            await fetchUnreadNotifications(userId);
        }*/

        window.location.href = "/home";
    } catch (error){
        showAlertToast("로그인 중 오류가 발생했습니다.");
        console.error(error);
    }

})

