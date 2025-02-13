document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("joinForm");

    form.addEventListener("submit", async (event) => {
        event.preventDefault(); // 기본 폼 제출 방지

        const formData = new FormData(form);
        const name = formData.get("name");
        const email = formData.get("email");
        const domain = formData.get("emailDomain");
        const password = formData.get("password");
        const confirmPassword = formData.get("confirmPassword");
        const phone = `${formData.get("phone1")}-${formData.get("phone2")}-${formData.get("phone3")}`;

        // 필수 입력값 체크
        if (!name || !email || !domain || !password || !confirmPassword) {
            alert("모든 필드를 입력하세요.");
            return;
        }

        // 비밀번호 확인 체크
        if (password !== confirmPassword) {
            alert("비밀번호가 일치하지 않습니다.");
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
            alert(result.message);
        } catch (error) {
            alert("회원가입 중 오류가 발생했습니다.");
            console.error(error);
        }
    });
});