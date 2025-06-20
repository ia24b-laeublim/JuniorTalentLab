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

document.addEventListener("DOMContentLoaded", function () {
    // Existing dropdown functionality
    const roleButton = document.querySelector(".role-button");
    const dropdown = document.querySelector(".dropdown");

    if (roleButton && dropdown) {
        roleButton.addEventListener("click", function (e) {
            e.stopPropagation();
            dropdown.classList.toggle("show");
        });

        window.addEventListener("click", function (e) {
            if (!dropdown.contains(e.target)) {
                dropdown.classList.remove("show");
            }
        });
    }

    // Carousel functionality
    initializeCarousel();
});

function initializeCarousel() {
    const images = document.querySelectorAll('.carousel-image');
    const dots = document.querySelectorAll('.dot');

    if (images.length === 0) {
        console.log('No carousel images found');
        return;
    }

    let currentSlideIndex = 0;
    const totalSlides = images.length;

    function showSlide(index) {
        // Hide all images
        images.forEach(img => img.classList.remove('active'));
        dots.forEach(dot => dot.classList.remove('active'));

        // Show current image and highlight corresponding dot
        if (images[index]) {
            images[index].classList.add('active');
        }
        if (dots[index]) {
            dots[index].classList.add('active');
        }
    }

    function nextSlide() {
        currentSlideIndex = (currentSlideIndex + 1) % totalSlides;
        showSlide(currentSlideIndex);
    }

    function previousSlide() {
        currentSlideIndex = (currentSlideIndex - 1 + totalSlides) % totalSlides;
        showSlide(currentSlideIndex);
    }

    function goToSlide(index) {
        currentSlideIndex = index - 1;
        showSlide(currentSlideIndex);
    }

    // Make functions globally accessible for onclick handlers
    window.nextSlide = nextSlide;
    window.previousSlide = previousSlide;
    window.currentSlide = goToSlide;

    // Initialize first slide
    showSlide(currentSlideIndex);

    // Auto-advance slides every 5 seconds
    setInterval(nextSlide, 5000);
}