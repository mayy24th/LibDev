import {showAlertToast} from "../utils/showAlertToast.js";

export async function duplicateCheckEmail() {
    const email = document.getElementById("email").value;
    const domain = document.getElementById("emailDomain").value;

    if (!email || !domain) {
        showAlertToast("이메일을 입력하세요.");
        return false;
    }

    const fullEmail = `${email}@${domain}`;

    try {
        const response = await fetch(`/api/v1/users/check-email/${fullEmail}`, {
            method: "GET",
            headers: { "Content-Type": "application/json" }
        });

        const result = await response.json();
        if (response.ok) {
            alert(result.data);
            return true;
        } else {
            alert(result.message);
            return false;
        }
    } catch (error) {
        alert("오류가 발생했습니다.");
        console.error(error);
        return false;
    }
}
