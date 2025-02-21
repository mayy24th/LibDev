export async function reissue() {
    try{
        const response = await fetch("/api/v1/auths/reissue", {
            method: "POST",
            credentials: "include"
        });

        if (!response.ok) throw new Error("로그인 정보 없음");

        return true;


    } catch (error){
        console.log("로그인되어 있지 않음");
        return null;
    }
}