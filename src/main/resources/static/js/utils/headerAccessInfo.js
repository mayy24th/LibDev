import { reissue } from "./reissue.js";

document.addEventListener("DOMContentLoaded", async () => {
    const topBar = document.querySelector(".top-bar");

    try {
        let response = await fetch("/api/v1/users", {
            method: "GET",
        });

        if (response.status === 401 || response.status === 500) {
            const reissued = await reissue();
            if (reissued) {
                response = await fetch("/api/v1/users", {
                    method: "GET",
                    credentials: "include",
                });
            } else {
                showLoginButtons();
                return;
            }
        }

        if (response.ok) {
            showLogoutButton();
        } else {
            showLoginButtons();
        }
    } catch (error) {
        console.error("로그인 상태 확인 실패:", error);
        showLoginButtons();
    }

    function showLoginButtons() {
        clearTopBar();
        const loginLink = createNavLink("/users/login", "로그인");
        const separator = document.createElement("span");
        separator.textContent = " | ";
        const joinLink = createNavLink("/users/join", "회원가입");

        topBar.appendChild(loginLink);
        topBar.appendChild(separator);
        topBar.appendChild(joinLink);
    }

    function showLogoutButton() {
        clearTopBar();
        const logoutLink = createNavLink("/users/logout", "로그아웃");
        logoutLink.addEventListener("click", handleLogout);
        topBar.appendChild(logoutLink);
    }

    async function handleLogout(event) {
        event.preventDefault();
        try {
            const response = await fetch("/api/v1/auths/logout", {
                method: "POST",
            });

            if (response.ok) {
                showLoginButtons();
            }
        } catch (error) {
            console.error("로그아웃 실패:", error);
            alert("로그아웃 실패")
        }
    }

    function clearTopBar() {
        while (topBar.firstChild) {
            topBar.removeChild(topBar.firstChild);
        }
    }

    function createNavLink(href, text) {
        const link = document.createElement("a");
        link.classList.add("header-text");
        link.href = href;
        link.textContent = text;
        return link;
    }
});