document.getElementById("btn-delete").addEventListener("click",  () => {
    let deleteModal = new bootstrap.Modal(document.getElementById("deleteConfirmModal"));
    deleteModal.show();
});

document.getElementById("confirm-delete").addEventListener("click", async () =>{
    try {
        let response = await fetch("/api/v1/users",{
            method: "DELETE"
        })
        const result = await response.json()
        if(!response.ok){
            alert("회원탈퇴에 실패하였습니다.")
            return;
        }

        alert(result.data)
        window.location.href = "/users/login";
    } catch (error){
        alert("회원탈퇴에 실패하였습니다.")
        console.log(error)
    }
})