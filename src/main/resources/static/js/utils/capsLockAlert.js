document.addEventListener("DOMContentLoaded", function() {
    const passwordInput = document.getElementById("passwordInput");
    const capsLockWarning = document.getElementById("capsLockAlert");

    passwordInput.addEventListener("keydown", function(event) {
        const isCapsLockOn = event.getModifierState("CapsLock");

        if (isCapsLockOn) {
            capsLockWarning.classList.remove("d-none");
        } else {
            capsLockWarning.classList.add("d-none");
        }
    });

    passwordInput.addEventListener("keyup", function(event) {
        const isCapsLockOn = event.getModifierState("CapsLock");

        if (!isCapsLockOn) {
            capsLockWarning.classList.add("d-none");
        }
    });

    passwordInput.addEventListener("blur", function() {
        capsLockWarning.classList.add("d-none");
    });
});