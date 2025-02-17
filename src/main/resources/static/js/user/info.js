document.addEventListener("DOMContentLoaded",async () => {
    try{
        const response = await fetch("/api/v1/users", {
            method: "GET",
            credentials:"include"
        })

        //추후 세부 에러 알림 표시
        if(!response.ok){
            window.location.href = "/users/login"
            return;
        }
        const result =  await response.json()
        const userInfo = result.data;

        // 사용자 이름 삽입
        document.querySelector(".profile-info strong").textContent = `${userInfo.name}님, 안녕하세요`;

        // 사용자 정보 삽입
        document.querySelector(".personal-info").innerHTML = `
            회원가입일: ${userInfo.createdAt} <br>
            휴대폰 번호: ${userInfo.phone} <br>
            이메일 주소: ${userInfo.email}
        `;


    } catch(error){
        alert("회원정보 조회 실패");
        console.error(error);
    }
})