(async () => {
    const timerDisplay = document.createElement("p");
    timerDisplay.className = "mt-2 text-danger";
    document.querySelector(".container").appendChild(timerDisplay);

    // 저장된 남은 시간 불러오기
    let timeLeft = localStorage.getItem("timeLeft") ? parseInt(localStorage.getItem("timeLeft")) : 120;

    function updateTimer() {
        const minutes = Math.floor(timeLeft / 60);
        const seconds = timeLeft % 60;
        timerDisplay.textContent = `남은 시간: ${minutes}:${seconds < 10 ? "0" : ""}${seconds}`;

        if (timeLeft === 0) {
            clearInterval(timer);
            timerDisplay.textContent = "인증 시간이 만료되었습니다.";
            document.getElementById("codeSubmit").disabled = true;
        }

        timeLeft--;
        localStorage.setItem("timeLeft", timeLeft); // 남은 시간 저장
    }

    updateTimer(); // 초기 실행
    const timer = setInterval(updateTimer, 1000);
})();
