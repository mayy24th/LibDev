import { showAlertToast } from "../utils/showAlertToast.js";

const form = document.getElementById("loginForm");

form.addEventListener("submit", async (event) => {
    event.preventDefault();

    const formData = new FormData(form);
    const email = formData.get("email");
    const password = formData.get("password");

    try {
        const response = await fetch("/api/v3/auths/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ email, password }),
        });

        const result = await response.json();
        if (result.code === 401) {
            showAlertToast(result.data);
            return;
        }
        showAlertToast(result.data);

        const prevUrl = document.referrer || "";

        if (prevUrl.includes("/users/join")) {
            window.location.href = "/home";
        } else {
            window.history.back();
        }

    } catch (error) {
        showAlertToast("로그인 중 오류가 발생했습니다.");
        console.error(error);
    }
});

