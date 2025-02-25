import {apiRequestRetry} from "../utils/apiRequsetRetry.js";
import {renderPagination} from "../borrow/renderPagination.js";

document.addEventListener("DOMContentLoaded", async () => {
    loadUserList(0);
});

async function loadUserList(page) {
    try {
        const response = await apiRequestRetry(`/api/admin/v1/admins/allUsers?page=${page}`,{
            method: "GET"
        })

        const userList = response.data.content;
        displayUserList(userList);
        renderPagination(response.data.totalPages, response.data.number, loadUserList);

    } catch (error){
        console.error(error.message);
    }
}

function displayUserList(userList) {
    const userListContainer = document.querySelector(".user-list");
    userListContainer.innerHTML = ""; // 기존 내용 초기화

    userList.forEach((user) => {
        const userItem = document.createElement("tr");

        const userName = document.createElement("td");
        userName.textContent = user.name;

        const userEmail = document.createElement("td");
        userEmail.textContent = user.email;

        const createdAt = document.createElement("td");
        createdAt.textContent = new Date(user.createdAt).toLocaleDateString();

        const penaltyExpiration = document.createElement("td");
        penaltyExpiration.textContent = user.penaltyExpiration ? new Date(user.penaltyExpiration).toLocaleDateString() : "-";

        const totalOverdueDays = document.createElement("td");
        totalOverdueDays.textContent = user.totalOverdueDays;

        const userRole = document.createElement("td");
        userRole.textContent = user.role;

        userItem.appendChild(userName);
        userItem.appendChild(userEmail);
        userItem.appendChild(createdAt);
        userItem.appendChild(penaltyExpiration);
        userItem.appendChild(totalOverdueDays);
        userItem.appendChild(userRole);

        userListContainer.appendChild(userItem);
    });
}

