const codeBtn = document.getElementById("codeSubmit");

codeBtn.addEventListener("click", async () => {
    const verificationCode = document.getElementById("code").value.trim();

    if (!verificationCode) {
        alert("인증번호를 입력해주세요.");
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
            alert("유효하지 않은 인증번호입니다.")
            return;
        }

        alert(result.data);

        window.location.href = "/auths/password-find/reset"

    } catch (error){
        alert("인증 과정 중 오류가 발생했습니다");
        console.log(error);
    }
})