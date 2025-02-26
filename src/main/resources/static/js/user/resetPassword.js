import {showAlertToast} from "../utils/showAlertToast.js";

const form = document.getElementById("resetPasswordForm");

const resetBtn = document.getElementById("resetSubmit");

resetBtn.addEventListener("click", async(event) => {
    event.preventDefault();

    const formData = new FormData(form);
    const password = formData.get("password");
    const confirmPassword = formData.get("confirmPassword");

    if(!password){
        showAlertToast("비밀번호를 입력해주세요.")
        return;
    }

    if(password !== confirmPassword){
        showAlertToast("비밀번호가 일치하지 않습니다.");
        return;
    }

    try{
        const response = await fetch("/api/v1/auths/password-find/reset",{
            method:"PATCH",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({password}),
        })

        const result = await  response.json();

        localStorage.removeItem("requestEmail");

        showAlertToast(result.data);

        window.location.href = "/users/login"
    } catch (error){
        showAlertToast("비밀번호 재설정 중 오류가 발생했습니다.")
    }
})