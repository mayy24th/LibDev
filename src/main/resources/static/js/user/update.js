import {reissue} from "../utils/reissue.js";
import {duplicateCheckEmail} from "./duplicateCheckEmail.js"

document.addEventListener("DOMContentLoaded",() => {
    const form = document.getElementById("updateForm")
    const checkEmailButton = document.getElementById("btn-check");
    let btn = false;
    let isEmailChecked = false;

    const currentEmail = document.getElementById("currentEmail").value;
    const emailField = document.getElementById("email");
    const emailDomainField = document.getElementById("emailDomain");

    function toggleCheckButton() {
        const fullEmail = `${emailField.value}@${emailDomainField.value}`;

        if (fullEmail === currentEmail) {
            btn = false;
            isEmailChecked = true;
        } else {
            btn = true;
            isEmailChecked = false;
        }
    }

    emailField.addEventListener("input", toggleCheckButton);
    emailDomainField.addEventListener("input", toggleCheckButton);

    checkEmailButton.addEventListener("click", async (event) => {
        event.preventDefault();
        if (btn) {
            isEmailChecked = await duplicateCheckEmail();
        }
    });


    form.addEventListener("submit", async (event) => {
        event.preventDefault();

        if (btn && !isEmailChecked) {
            alert("이메일 중복 체크를 진행해주세요.");
            return;
        }

        const formData = new FormData(form);
        const name = formData.get("name");
        const email = formData.get("email");
        const domain = formData.get("emailDomain");
        const phone = `${formData.get("phone1")}-${formData.get("phone2")}-${formData.get("phone3")}`;

        const password = formData.get("password");
        const confirmPassword = formData.get("confirmPassword");

        const fullEmail = `${email}@${domain}`;

        try {
            let response = await  fetch("/api/v1/users",{
                method: "PATCH",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ name, email: fullEmail, phone }),
            });
            if(response.status === 401){
                const reissued = await reissue();

                if(reissued){
                    response = await fetch("/api/v1/users",{
                        method: "PATCH",
                        headers: { "Content-Type": "application/json" },
                        body: JSON.stringify({ name, email: fullEmail, phone }),
                    })
                }
            }

            const result = await response.json();
            alert(result.message);

            if (password.trim() !== "") {
                if (password !== confirmPassword) {
                    alert("비밀번호가 일치하지 않습니다.");
                    return;
                }

                let passwordResponse = await fetch("/api/v1/users/password", {
                    method: "PATCH",
                    headers: {"Content-Type": "application/json"},
                    body: JSON.stringify({password}),
                });

                if (passwordResponse.status === 401) {
                    const reissued = await reissue();
                    if (reissued) {
                        passwordResponse = await fetch("/api/v1/users/password", {
                            method: "PATCH",
                            headers: {"Content-Type": "application/json"},
                            body: JSON.stringify({password}),
                        });
                    }
                }

                const passwordResult = await passwordResponse.json();
                alert(passwordResult.message);
            }



        } catch (error) {
            alert("회원정보 수정 중 오류가 발생했습니다.");
            console.error(error);
        }
    })
})