export async function reissue() {
    try{
        const response = await fetch("/api/v1/auths/reissue", {
            method: "POST",
            credentials: "include"
        });

        if (!response.ok) throw new Error("로그인 정보 없음");

        return true;


    } catch (error){
        console.log("로그인되지 않음");
        alert("로그인이 필요합니다.");
        window.location.href = "/users/login"; // 로그인 페이지로 이동
        return null; // 로그인하지 않은 경우 null 반환
    }
}