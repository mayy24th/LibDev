const myBorrow = document.getElementById("myBorrow");
const myReturn = document.getElementById("myReturn");
const myReservation = document.getElementById("myReservation");
const myReview = document.getElementById("myReview");

myBorrow.addEventListener("click",() => {
    window.location.href = "/borrows/my-status";
})

myReturn.addEventListener("click",() => {
    window.location.href = "/borrows/my-history";
})

myReservation.addEventListener("click",() => {
    window.location.href = "/reservations/list";
})

myReview.addEventListener("click",() => {
    window.location.href = "/review/user"
})