export async function fetchUserId() {
    try {
        const response = await fetch("/api/v1/auths/me", {
            method: "GET",
            credentials: "include",
            headers: {
                "Content-Type": "application/json"
            }
        });

        if (!response.ok) {
            throw new Error("사용자 정보를 가져오지 못했습니다.");
        }

        const data = await response.json();
        return data.userId;
    } catch (error) {
        console.error("사용자 ID 조회 실패:", error);
        return null;
    }
}
