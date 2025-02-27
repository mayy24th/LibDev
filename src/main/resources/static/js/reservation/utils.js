export function formatDate(isoString) {
    if (!isoString) return "-";

    const date = new Date(isoString);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const day = String(date.getDate()).padStart(2, "0");
    const hours = String(date.getHours()).padStart(2, "0");
    const minutes = String(date.getMinutes()).padStart(2, "0");

    return `${year}-${month}-${day} / ${hours}:${minutes}`;
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
        case "WAITING":
            return "#213bb1";
        case "READY":
            return "#00712c";
        default:
            return "black";
    }
}
