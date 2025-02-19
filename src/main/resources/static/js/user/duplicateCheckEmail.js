export let isEmailChecked;

export async function duplicateCheckEmail(){
    const checkEmailButton = document.getElementById("btn-check");
    isEmailChecked = false;

    checkEmailButton.addEventListener("click", async (event) => {
        event.preventDefault();

        const email = document.getElementById("email").value;
        const domain = document.getElementById("emailDomain").value;

        if(!email || !domain){
            alert("이메일을 입력하세요.");
            return;
        }

        const fullEmail = `${email}@${domain}`;

        try {
            const response = await fetch(`/api/v1/users/check-email/${fullEmail}`,{
                method: "GET",
                headers: {"Content-Type":"application/json"}
            });

            const result = await response.json()
            if(response.ok){
                alert(result.data);
                isEmailChecked = true;
            } else {
                alert(result.message);
            }

        } catch (error) {
            alert("오류가 발생했습니다.");
            console.error(error);
        }
    });
    return isEmailChecked;
}
