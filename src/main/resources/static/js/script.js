document.addEventListener("DOMContentLoaded", function () {
    // Initialize dropdown functionality
    initializeDropdown();

    // Initialize carousel functionality
    initializeCarousel();
});

function initializeDropdown() {
    const roleButton = document.querySelector(".role-button");
    const dropdown = document.querySelector(".dropdown");

    if (roleButton && dropdown) {
        console.log("Dropdown elements found, initializing...");

        roleButton.addEventListener("click", function (e) {
            e.stopPropagation();
            dropdown.classList.toggle("show");
            console.log("Dropdown toggled");
        });

        // Close dropdown when clicking outside
        document.addEventListener("click", function (e) {
            if (!dropdown.contains(e.target) && !roleButton.contains(e.target)) {
                dropdown.classList.remove("show");
            }
        });

        // Close dropdown when pressing Escape key
        document.addEventListener("keydown", function(e) {
            if (e.key === "Escape") {
                dropdown.classList.remove("show");
            }
        });
    } else {
        console.log("Dropdown elements not found on this page");
    }
}

function initializeCarousel() {
    const images = document.querySelectorAll('.carousel-image');
    const dots = document.querySelectorAll('.dot');

    if (images.length === 0) {
        console.log('No carousel images found');
        return;
    }

    console.log(`Carousel initialized with ${images.length} images`);

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