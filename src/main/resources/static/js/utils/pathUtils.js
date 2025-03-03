import {checkLoginStatus} from "./auth.js";

export function getReviewApiEndpoint() {
    const currentPath = window.location.pathname;

    if (currentPath === "/review/user") {
        checkLoginStatus();
        return "/api/review/user"; // 로그인한 유저의 리뷰
    } else if (currentPath.startsWith("/review/book/")) {
        const bookId = currentPath.split("/").pop();
        return `/api/review/book/${bookId}`; // 특정 도서 리뷰
    }

    return "/api/review"; // 기본값 (전체 리뷰)
}
