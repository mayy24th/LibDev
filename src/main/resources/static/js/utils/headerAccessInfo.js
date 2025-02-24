import { reissue } from "./reissue.js";
import {apiRequestRetry} from "./apiRequsetRetry.js";

document.addEventListener("DOMContentLoaded", async () => {
    const topBar = document.querySelector(".top-bar");
    const roleMenu = document.querySelector(".role-menu");

    try {
        let response = await fetch("/api/v1/users", {
            method: "GET",
        });

        if (response.status === 401 || response.status === 500) {
            const reissued = await reissue();
            if (reissued) {
                response = await fetch("/api/v1/users", {
                    method: "GET",
                });
            } else {
                showLoginButton();
                return;
            }
        }

        if (response.ok) {
            const userInfo = await response.json();
            updateRoleMenu(userInfo.data.role);
            showLogoutButton();
        } else {
            showLoginButton();
        }
    } catch (error) {
        console.error("로그인 상태 확인 실패:", error);
        showLoginButton();
    }

    function showLoginButton() {
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

    function updateRoleMenu(role) {
        const firstMenuItem = roleMenu.querySelector("li:first-child a");
        if (role === "ADMIN") {
            firstMenuItem.href = "/admin/management";
            firstMenuItem.textContent = "관리";
        } else {
            firstMenuItem.href = "/users/mypage";
            firstMenuItem.textContent = "기본정보";
        }
    }

    async function handleLogout(event) {
        event.preventDefault();
        try {
            const response = await apiRequestRetry("/api/v1/auths/logout", {
                method: "POST",
            });

            if (response.ok) {
                alert("로그아웃 되었습니다.")
                showLoginButton();
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