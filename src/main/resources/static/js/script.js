document.addEventListener("DOMContentLoaded", function () {
    const roleButton = document.querySelector(".role-button");
    const dropdown = document.querySelector(".dropdown");

    roleButton.addEventListener("click", function (e) {
        e.stopPropagation();
        dropdown.classList.toggle("show");
    });

    window.addEventListener("click", function (e) {
        if (!dropdown.contains(e.target)) {
            dropdown.classList.remove("show");
        }
    });
});