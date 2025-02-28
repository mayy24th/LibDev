import { formatDate } from "../borrow/utils.js";
import {apiRequestRetry} from "../utils/apiRequsetRetry.js";
import {checkLoginStatus} from "../utils/auth.js";

document.addEventListener("DOMContentLoaded", async () => {
    const userInfoContainer = document.querySelector(".container");
    if (userInfoContainer) userInfoContainer.style.display = "none"; // 초기 숨김 처리

    const isLoggedIn = await checkLoginStatus();
    if (!isLoggedIn) return; // 로그인 안 되어 있으면 실행 종료

    const result = await apiRequestRetry("/api/v1/users", { method: "GET" });
    if (result) {
        populateUserInfo(result.data);
        if (userInfoContainer) userInfoContainer.style.display = "block"; // 로그인 후 표시
    }
});

const editBtn = document.getElementById("edit");

if(editBtn){
    editBtn.addEventListener("click",() => {
        window.location.href = "/users/update"
    })
}


function populateUserInfo(userInfo) {
    const nameElement = document.querySelector(".user-name");
    const joinDateElement = document.querySelector(".join-date");
    const phoneElement = document.querySelector(".phone-number");
    const emailElement = document.querySelector(".email-address");

    if (nameElement) nameElement.textContent = userInfo.name;
    if (joinDateElement) joinDateElement.textContent = formatDate(userInfo.createdAt);
    if (phoneElement) phoneElement.textContent = userInfo.phone;
    if (emailElement) emailElement.textContent = userInfo.email;

    const nameInput = document.getElementById("name");
    const emailInput = document.getElementById("email");
    const emailDomainInput = document.getElementById("emailDomain")
    const phone1Input = document.getElementById("phone1");
    const phone2Input = document.getElementById("phone2");
    const phone3Input = document.getElementById("phone3");

    const currentEmail = document.getElementById("currentEmail");
    if(currentEmail) currentEmail.value = userInfo.email;

    if (nameInput) nameInput.value = userInfo.name;
    if (emailInput) {
        const [emailId, emailDomain] = userInfo.email.split("@");
        emailInput.value = emailId;
        if(emailDomainInput) emailDomainInput.value = emailDomain;
    }
    if (userInfo.phone && phone1Input && phone2Input && phone3Input) {
        const phoneParts = userInfo.phone.split("-");
        if (phoneParts.length === 3) {
            phone1Input.value = phoneParts[0];
            phone2Input.value = phoneParts[1];
            phone3Input.value = phoneParts[2];
        }
    }
}