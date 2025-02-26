const userManagement = document.getElementById("userManagement");
const bookManagement = document.getElementById("bookManagement");
const borrowManagement = document.getElementById("borrowManagement");
const reservationManagement = document.getElementById("reservationManagement");

userManagement.addEventListener("click", () => {
    window.location.href = "/admin/user-management";
})

bookManagement.addEventListener("click", () => {
    window.location.href = "/books/book-admin";
})

borrowManagement.addEventListener("click", () => {
    window.location.href = "/borrows/admin";
})

reservationManagement.addEventListener("click",() => {
    window.location.href = "/admin/reservations"
})