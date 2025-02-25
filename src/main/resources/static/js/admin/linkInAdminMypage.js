const userManagement = document.getElementById("userManagement");
const bookManagement = document.getElementById("bookManagement");
const borrowManagement = document.getElementById("borrowManagement");

userManagement.addEventListener("click", () => {
    window.location.href = "/admin/user-management";
})

bookManagement.addEventListener("click", () => {
    window.location.href = "/books/book-admin";
})

borrowManagement.addEventListener("click", () => {
    window.location.href = "/borrows/admin";
})