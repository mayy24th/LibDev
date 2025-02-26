const naverBtn = document.getElementById("social-btn-naver")

naverBtn.addEventListener("click", () =>{
    window.location.href = "/oauth2/authorization/naver"
})

const googleBtn = document.getElementById("social-btn-google")

googleBtn.addEventListener("click",() => {
    window.location.href = "/oauth2/authorization/google"
})
