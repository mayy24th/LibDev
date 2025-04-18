import {showAlertToast} from "./showAlertToast.js";

export async function checkLoginStatus() {
    try {
        const response = await fetch("/api/v1/users", {
            method: "GET",
            credentials: "include" // 쿠키 포함하여 요청
        });

        if (!response.ok) throw new Error("로그인 정보 없음");

        return true;
    } catch (error) {
        console.log("로그인되지 않음");
        showAlertToast("로그인이 필요합니다.");

        setTimeout(() => {
            window.location.href = "/users/login";
        }, 500);

        return null; // 로그인하지 않은 경우 null 반환
    }
}