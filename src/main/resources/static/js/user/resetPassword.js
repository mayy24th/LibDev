const resetBtn = document.getElementById("resetSubmit");

resetBtn.addEventListener("click", async() => {
    const password = document.getElementById("password").value;
    const confirmPassword = document.getElementById("confirmPassword").value;

    if(!password){
        alert("비밀번호를 입력해주세요.")
        return;
    }

    if(password !== confirmPassword){
        alert("비밀번호가 일치하지 않습니다.");
        return;
    }

    try{
        const response = await fetch("/api/v1/auths/password-find/reset",{
            method:"PATCH",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({password}),
        })

        const result = await  response.json();

        if(!response.ok){
            alert("비밀번호가 변경되지 않았습니다")
            return;
        }
        alert(result.data);

        window.location.href = "/users/login"
    } catch (error){
        alert("비밀번호 재설정 중 오류가 발생했습니다.")
    }
})