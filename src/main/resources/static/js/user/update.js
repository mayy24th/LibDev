import {duplicateCheckEmail} from "./duplicateCheckEmail.js"
import {apiRequestRetry} from "../utils/apiRequsetRetry.js";
import {showAlertToast} from "../utils/showAlertToast.js";
document.addEventListener("DOMContentLoaded",() => {
    const form = document.getElementById("updateForm")

    form.addEventListener("submit", async (event) => {
        event.preventDefault();

        const formData = new FormData(form);
        const name = formData.get("name");
        const phone = `${formData.get("phone1")}-${formData.get("phone2")}-${formData.get("phone3")}`;

        const password = formData.get("password");
        const confirmPassword = formData.get("confirmPassword");

        try {
            const result = await apiRequestRetry("/api/v1/users",{
                method: "PATCH",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ name, phone }),
            })
            showAlertToast(result.data);

            if (password.trim() !== "") {
                if (password !== confirmPassword) {
                    showAlertToast("비밀번호가 일치하지 않습니다.");
                    return;
                }

                const passwordResult = await apiRequestRetry("/api/v1/users/password", {
                    method: "PATCH",
                    headers: {"Content-Type": "application/json"},
                    body: JSON.stringify({password}),
                });

                showAlertToast(passwordResult.data);
            }

        } catch (error) {
            showAlertToast("회원정보 수정 중 오류가 발생했습니다.");
            console.error(error);
        }
    })
})