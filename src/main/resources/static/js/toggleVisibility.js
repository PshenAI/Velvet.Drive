const togglePassword = document.querySelector("#togglePassword");
const password = document.querySelector("#password");
const confirmPassword = document.querySelector("#confirmPassword")

togglePassword.addEventListener("click", function () {
    // toggle the type attribute
    const type = password.getAttribute("type") === "password" ? "text" : "password";
    password.setAttribute("type", type);
    if (confirmPassword !== null){
        confirmPassword.setAttribute("type", type);
    }

    // toggle the icon
    this.classList.toggle("bi-eye");
});