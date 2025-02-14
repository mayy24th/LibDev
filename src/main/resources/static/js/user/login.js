document.addEventListener("DOMContentLoaded",() => {
    const form = document.getElementById("loginForm");

    form.addEventListener("submit", async (event) => {
        event.preventDefault();

        const formData = new FormData(form);
        const email = formData.get("email");
        const password = formData.get("password");

        try {
            const response = await  fetch("/api/v3/auths/login", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({email,password}),
            });

            const result = await response.json();

            //추후 세부 에러 알림 표시
            alert(result.data)
        } catch (error){
            alert("로그인 중 오류가 발생했습니다.");
            console.error(error);
        }

    })
})