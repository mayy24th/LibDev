import { reissue } from "../utils/reissue.js";

document.addEventListener("DOMContentLoaded", async () => {
    try{
        let response = await fetch("/api/v1/users", {
            method: "GET",
            credentials:"include"
        })

        if (response.status === 401 || response.status === 500) {
            const reissued = await reissue();

            if (reissued) {
                response = await fetch("/api/v1/users", {
                    method: "GET",
                    credentials: "include"
                });
            } else {
                return;
            }
        }

        if (!response.ok) {
            throw new Error("회원정보 조회 실패");
        }
        const result =  await response.json()
        const userInfo = result.data;

        // 필드에 사용자 정보 주입
        document.querySelector(".user-name").textContent = userInfo.name;
        document.querySelector(".join-date").textContent = userInfo.createdAt;
        document.querySelector(".phone-number").textContent = userInfo.phone;
        document.querySelector(".email-address").textContent = userInfo.email;


    } catch(error){
        alert("회원정보 조회 실패");
        console.error(error);
    }
})

const editBtn = document.getElementById("edit");

editBtn.addEventListener("click",() => {
    window.location.href = "/users/update"
})

