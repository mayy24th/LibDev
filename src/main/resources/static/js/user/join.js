async function submitForm() {
    const name = document.getElementById('name').value;
    const email = document.getElementById('email').value;
    const domain = document.getElementById('emailDomain').value;
    const password = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirmPassword').value;
    const phone = `${document.getElementById('phone1').value}-${document.getElementById('phone2').value}-${document.getElementById('phone3').value}`;

    /*임시 validation*/
    if (!name || !email || !domain || !password || !confirmPassword) {
        alert('모든 필드를 입력하세요.');
        return;
    }
    if (password !== confirmPassword) {
        alert('비밀번호가 일치하지 않습니다.');
        return;
    }
    const fullEmail = `${email}@${domain}`;
    const response = await fetch('/api/v1/users', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ name, email: fullEmail, password, phone })
    });

    const result = await response.json();
    alert(result.message);
}