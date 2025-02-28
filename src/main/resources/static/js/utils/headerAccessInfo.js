import { reissue } from "./reissue.js";
import {apiRequestRetry} from "./apiRequsetRetry.js";
import {showAlertToast} from "./showAlertToast.js";

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
            showLogoutButton(userInfo.data.name);
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
        separator.classList.add("header-text");
        separator.textContent = " | ";
        const joinLink = createNavLink("/users/join", "회원가입");

        topBar.appendChild(loginLink);
        topBar.appendChild(separator);
        topBar.appendChild(joinLink);
    }

    function showLogoutButton(userName) {
        clearTopBar();
        const loginName = document.createElement("a");
        loginName.classList.add("header-text");
        loginName.textContent = userName + "님";
        const separator = document.createElement("span");
        separator.classList.add("header-text");
        separator.textContent = " | ";
        const logoutLink = createNavLink("/users/logout", "로그아웃");
        logoutLink.addEventListener("click", handleLogout);
        topBar.appendChild(loginName);
        topBar.appendChild(separator);
        topBar.appendChild(logoutLink);
    }

    function updateRoleMenu(role) {// roleMenu 요소 가져오기
        if (role === "ADMIN") {
            if (!roleMenu.querySelector("a[href='/admin/management']")) {
                const adminItem = document.createElement("li");
                const adminLink = document.createElement("a");
                adminLink.href = "/admin/management";
                adminLink.textContent = "관리";
                adminItem.appendChild(adminLink);

                roleMenu.prepend(adminItem);
            }
        }
    }

    async function handleLogout(event) {
        event.preventDefault();
        try {
            const response = await apiRequestRetry("/api/v1/auths/logout", {
                method: "POST",
            });

            showAlertToast("로그아웃 되었습니다.")
            showLoginButton();
            window.location.href="/home";
        } catch (error) {
            console.error("로그아웃 실패:", error);
            showAlertToast("로그아웃 실패");
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