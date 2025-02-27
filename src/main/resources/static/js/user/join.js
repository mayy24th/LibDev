import { duplicateCheckEmail } from "./duplicateCheckEmail.js"
import {showAlertToast} from "../utils/showAlertToast.js";

document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("joinForm");
    const checkEmailButton = document.getElementById("btn-check");
    let isEmailChecked = false;

    checkEmailButton.addEventListener("click", async (event) => {
        event.preventDefault();

        isEmailChecked = await duplicateCheckEmail();

    });

    form.addEventListener("submit", async (event) => {
        event.preventDefault();

        if (!isEmailChecked) {
            showAlertToast("이메일 중복 체크를 진행해주세요.");
            return;
        }

        clearValidationErrors();

        const formData = new FormData(form);
        const name = formData.get("name");
        const email = formData.get("email");
        const domain = formData.get("emailDomain");
        const password = formData.get("password");
        const confirmPassword = formData.get("confirmPassword");
        const phone = `${formData.get("phone1")}-${formData.get("phone2")}-${formData.get("phone3")}`;

        if (!name || !email || !domain || !password || !confirmPassword) {
            showAlertToast("모든 필드를 입력하세요.");
            return;
        }

        if (password !== confirmPassword) {
            showAlertToast("비밀번호가 일치하지 않습니다.");
            return;
        }

        const fullEmail = `${email}@${domain}`;

        try {
            const response = await fetch("/api/v1/users", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ name, email: fullEmail, password, phone }),
            });

            const result = await response.json();

            if (!response.ok) {
                console.error("회원가입 실패:", result);
                showAlertToast(result.message || "회원가입에 실패했습니다.");
                if (result.data) {
                    showValidationErrors(result.data);
                }
                return;
            }

            showAlertToast("회원가입이 완료되었습니다.");
            window.location.href = "/home"; // 성공 시 로그인 페이지로 이동
        } catch (error) {
            showAlertToast("회원가입 중 오류가 발생했습니다.");
            console.error("네트워크 또는 서버 오류:", error);
        }
    });
});

function showValidationErrors(errors) {
    Object.keys(errors).forEach(field => {
        const inputField = document.getElementById(field);
        if (!inputField) return;

        clearFieldError(inputField);

        const errorMessage = document.createElement("div");
        errorMessage.className = "error-message";
        errorMessage.innerText = errors[field];

        inputField.classList.add("error-border");

        const formGroup = inputField.closest(".form-group");
        formGroup.appendChild(errorMessage);
    });
}

function clearFieldError(inputField) {
    const errorMessages = inputField.closest(".form-group").querySelectorAll(".error-message");
    errorMessages.forEach(el => el.remove());
}

function clearValidationErrors() {
    document.querySelectorAll(".error-message").forEach(el => el.remove());
    document.querySelectorAll(".error-border").forEach(el => el.classList.remove("error-border"));
}