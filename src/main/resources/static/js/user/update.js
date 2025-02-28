import { apiRequestRetry } from "../utils/apiRequsetRetry.js";
import { showAlertToast } from "../utils/showAlertToast.js";

document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("updateForm");
    const passwordField = document.getElementById("password");
    const confirmPasswordGroup = document.getElementById("confirmPasswordGroup");
    const confirmPasswordField = document.getElementById("confirmPassword");
    const passwordToggleBtn = document.getElementById("passwordToggleBtn");


    let isPasswordEditing = false;

    passwordToggleBtn.addEventListener("click", (event) => {
        event.preventDefault(); // 불필요한 이벤트 실행 방지

        if (!isPasswordEditing) {

            passwordField.removeAttribute("readonly");
            passwordField.value = ""
            passwordField.style.backgroundColor = "#ffffff"; // 배경색 변경
            passwordField.style.cursor = "text";
            passwordField.style.border = "1px solid #ced4da";

            confirmPasswordGroup.style.display = "block";
            isPasswordEditing = true;
        } else {
            const password = passwordField.value.trim();
            const confirmPassword = confirmPasswordField.value.trim();

            if (!password || !confirmPassword) {
                showAlertToast("비밀번호를 입력해주세요.");
                return;
            }

            if (password !== confirmPassword) {
                showAlertToast("비밀번호가 일치하지 않습니다.");
                return;
            }

            changePassword(password);
        }
    });

    async function changePassword(password) {
        try {
            console.log("API 요청 보냄:", password);
            const passwordResult = await apiRequestRetry("/api/v1/users/password", {
                method: "PATCH",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ password }),
            });

            showAlertToast(passwordResult.data);

            passwordField.value = "********"; // 보안상의 이유로 표시 변경
            passwordField.setAttribute("readonly", "true");
            passwordField.style.backgroundColor = "#e9ecef";
            passwordField.style.cursor = "not-allowed";
            passwordField.style.border = "none";

            confirmPasswordField.value = "";
            confirmPasswordGroup.style.display = "none";

            passwordToggleBtn.textContent = "비밀번호 변경";
            isPasswordEditing = false;
        } catch (error) {
            showAlertToast("비밀번호 변경 중 오류가 발생했습니다.");
            console.error(error);
        }
    }

    form.addEventListener("submit", async (event) => {
        event.preventDefault();

        const formData = new FormData(form);
        const name = formData.get("name");
        const phone = `${formData.get("phone1")}-${formData.get("phone2")}-${formData.get("phone3")}`;

        try {
            const result = await apiRequestRetry("/api/v1/users", {
                method: "PATCH",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ name, phone }),
            });
            showAlertToast(result.data);
        } catch (error) {
            showAlertToast("회원정보 수정 중 오류가 발생했습니다.");
            console.error(error);
        }
    });
});