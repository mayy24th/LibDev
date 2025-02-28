import {showAlertToast} from "../utils/showAlertToast.js";

const codeBtn = document.getElementById("codeSubmit");

codeBtn.addEventListener("click", async () => {
    const verificationCode = document.getElementById("code").value.trim();

    if (!verificationCode) {
        showAlertToast("인증번호를 입력해주세요.");
        return;
    }

    const email = localStorage.getItem("requestEmail");

    try{
        const response = await fetch("/api/v1/auths/password-find/code",{
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({email, verificationCode}),
        })

        const result = await response.json();

        if(!response.ok){
            showAlertToast("유효하지 않은 인증번호입니다.")
            return;
        }

        localStorage.removeItem("timeLeft");

        showAlertToast(result.data);

        window.location.href = "/auths/password-find/reset"

    } catch (error){
        showAlertToast("인증 과정 중 오류가 발생했습니다");
        console.log(error);
    }
})