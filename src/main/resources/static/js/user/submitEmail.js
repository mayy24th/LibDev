const emailBtn = document.getElementById("emailSubmit");

emailBtn.addEventListener("click", async () => {
    const email = document.getElementById("email").value.trim();
    console.log(email);

    if (!email) {
        alert("이메일을 입력해주세요.");
        return;
    }

    localStorage.setItem("requestEmail", email);

    try{
        const response = await fetch("/api/v1/auths/password-find/verify",{
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({email}),
        })

        const result = await response.json();

        if(result.status === 404){
            alert("해당 이메일을 찾을 수 없습니다.")
            return;
        }

        alert(result.data);

        window.location.href = "/auths/password-find/codeVerify";
    } catch (error){
        alert("인증 과정 중 오류가 발생했습니다");
        console.log(error);
    }
})