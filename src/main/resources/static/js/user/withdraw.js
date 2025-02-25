import {apiRequestRetry} from "../utils/apiRequsetRetry.js";
import {showAlertToast} from "../utils/showAlertToast.js";

document.getElementById("btn-delete").addEventListener("click",  () => {
    let deleteModal = new bootstrap.Modal(document.getElementById("deleteConfirmModal"));
    deleteModal.show();
});

document.getElementById("confirm-delete").addEventListener("click", async () =>{
    try {
        const result = await apiRequestRetry("/api/v1/users",{
            method: "DELETE"
        })

        showAlertToast(result.data)
        window.location.href = "/users/login";
    } catch (error){
        showAlertToast("회원탈퇴에 실패하였습니다.")
        console.log(error)
    }
})