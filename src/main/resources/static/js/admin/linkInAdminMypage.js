const userManagement = document.getElementById("userManagement");
const bookManagement = document.getElementById("bookManagement");

userManagement.addEventListener("click", () => {
    window.location.href = "/admin/user-management";
})

bookManagement.addEventListener("click", () => {
    window.location.href = "/books/book-admin";
})