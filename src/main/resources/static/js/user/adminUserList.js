import {apiRequestRetry} from "../utils/apiRequsetRetry.js";
import {renderPagination} from "../borrow/renderPagination.js";
import {showAlertToast} from "../utils/showAlertToast.js";

document.addEventListener("DOMContentLoaded", async () => {
    await loadUserList(0);
});

async function loadUserList(page) {
    try {
        const response = await apiRequestRetry(`/api/admin/v1/admins/user-list?page=${page}`,{
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
    userListContainer.innerHTML = "";

    userList.forEach((user) => {
        const userItem = document.createElement("tr");

        const userName = document.createElement("td");
        userName.textContent = user.name;

        const userEmail = document.createElement("td");
        userEmail.textContent = user.email;

        const userPhone = document.createElement("td");
        userPhone.textContent = user.phone;

        const createdAt = document.createElement("td");
        createdAt.textContent = new Date(user.createdAt).toLocaleString();

        const penaltyExpiration = document.createElement("td");
        penaltyExpiration.textContent = user.penaltyExpiration ? new Date(user.penaltyExpiration).toLocaleDateString() : "-";

        const totalOverdueDays = document.createElement("td");
        totalOverdueDays.textContent = user.totalOverdueDays;

        const { userRoleTd, actionTd } = createRoleCell(user);

        userItem.appendChild(userName);
        userItem.appendChild(userEmail);
        userItem.appendChild(userPhone);
        userItem.appendChild(createdAt);
        userItem.appendChild(penaltyExpiration);
        userItem.appendChild(totalOverdueDays);
        userItem.appendChild(userRoleTd);
        userItem.appendChild(actionTd);


        userListContainer.appendChild(userItem);
    });
}


function createRoleCell(user) {
    const userRoleTd = document.createElement("td");
    let isEditing = false;

    const roleText = document.createElement("span");
    roleText.textContent = user.role;

    const roleSelect = document.createElement("select");
    ["USER", "ADMIN"].forEach(role => {
        const option = document.createElement("option");
        option.value = role;
        option.textContent = role;
        if (user.role === role) {
            option.selected = true;
        }
        roleSelect.appendChild(option);
    });

    roleSelect.style.display = "none";
    userRoleTd.appendChild(roleText);
    userRoleTd.appendChild(roleSelect);

    const actionButton = document.createElement("button");
    actionButton.textContent = "변경";
    actionButton.classList.add("btn"); // btn 클래스 추가

    actionButton.addEventListener("click", async function () {
        if (!isEditing) {
            roleText.style.display = "none";
            roleSelect.style.display = "inline-block";
            actionButton.textContent = "저장";
            isEditing = true;
        } else {
            const newRole = roleSelect.value;
            try {
                const response = await apiRequestRetry("/api/admin/v1/admins/change-role", {
                    method: "PATCH",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify({ email: user.email, role: newRole }),
                });

                showAlertToast(response.data);

                roleText.textContent = newRole;
                roleText.style.display = "inline";
                roleSelect.style.display = "none";
                actionButton.textContent = "변경";
                isEditing = false;
            } catch (error) {
                showAlertToast(error.message);
            }
        }
    });

    const actionTd = document.createElement("td");
    actionTd.appendChild(actionButton);

    return { userRoleTd, actionTd };
}

