export function formatDate(isoString) {
    if (!isoString) return "-";

    const date = new Date(isoString);
    const year = date.getFullYear();
    const month = date.getMonth() + 1;
    const day = date.getDate();
    const hours = date.getHours();
    const minutes = date.getMinutes();

    const period = hours >= 12 ? "오후" : "오전";
    const formattedHours = hours % 12 || 12;

    return `${year}년 ${month}월 ${day}일 ${period} ${formattedHours}시 ${minutes}분`;
}

export function statusText(status) {
    const statusMap = {
        "WAITING": "대기중",
        "READY": "대출가능"
    };
    return statusMap[status] || status;
}

export function statusColor(status) {
    switch (status) {
        case "대기중":
            return "#213bb1";  // 파란색
        case "대출가능":
            return "#00712c";  // 초록색
        default:
            return "black";
    }
}
