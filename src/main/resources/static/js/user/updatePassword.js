import {reissue} from "../utils/reissue.js";

document.addEventListener("DOMContentLoaded",() => {
    const form =  document.getElementById("updateForm");

    form.addEventListener("submit" , async (event) => {
        event.preventDefault();

        const formData = new FormData(form);
        const password = formData.get("password");
        const confirmPassword = formData.get("confirmPassword");

        if(password.trim() !== ""){
            if(password !== confirmPassword){
                alert("비밀번호가 일치하지 않습니다.")
                return;
            }

            try {
                let response = await fetch("/api/v1/users/password", {
                    method: "PATCH",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({password})
                })
                if(response.status === 401){
                    const reissued = await  reissue();

                    if(reissued){
                        response = await  fetch("/api/v1/users", {
                            method: "PATCH",
                            headers: { "Content-Type": "application/json" },
                            body: JSON.stringify({password})
                        })
                    }
                }

                const result = await response.json();

                alert(result.message);
            } catch (error){
                alert("비밀번호 변경 중 오류가 발생했습니다.");
                console.error(error);
            }
        }
    })
})