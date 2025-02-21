export function formatDate(date) {
    return date ? date.split('T')[0] : "-";
}

export function statusColor(status) {
    switch (status) {
        case "대출 중":
            return "#213bb1";
        case "반납 신청":
            return "#00712c";
        case "연체 중":
            return "#df0303";
        default:
            return "black";
    }
}
