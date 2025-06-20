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

        // Close dropdown when pressing an Escape key
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

// Email
document.addEventListener("DOMContentLoaded", function () {
    const contactForm = document.getElementById("contact-form");

    if (contactForm) {
        const feedback = contactForm.getAttribute("data-feedback");
        if (feedback) {
            alert(feedback);
        }
    }
});

document.addEventListener('DOMContentLoaded', function() {
    initializeTaskTypeSelection();
    initializeFormValidation();
});

function initializeTaskTypeSelection() {
    const taskTypeCards = document.querySelectorAll('.task-type-card');
    const radioButtons = document.querySelectorAll('input[name="taskType"]');

    taskTypeCards.forEach(card => {
        card.addEventListener('click', function() {
            // Remove selected class from all cards
            taskTypeCards.forEach(c => c.classList.remove('selected'));

            // Add selected class to clicked card
            this.classList.add('selected');

            // Check the corresponding radio button
            const radioButton = this.querySelector('input[type="radio"]');
            if (radioButton) {
                radioButton.checked = true;
            }
        });
    });

    // Handle radio button changes (for keyboard navigation)
    radioButtons.forEach(radio => {
        radio.addEventListener('change', function() {
            if (this.checked) {
                taskTypeCards.forEach(card => {
                    card.classList.remove('selected');
                    if (card.dataset.type === this.value) {
                        card.classList.add('selected');
                    }
                });
            }
        });
    });
}

function initializeFormValidation() {
    const form = document.getElementById('create-task-form');
    const requiredFields = form.querySelectorAll('[required]');

    form.addEventListener('submit', function(e) {
        e.preventDefault();

        if (validateForm()) {
            // If validation passes, submit the form
            this.submit();
        }
    });

    // Add real-time validation feedback
    requiredFields.forEach(field => {
        field.addEventListener('blur', function() {
            validateField(this);
        });

        field.addEventListener('input', function() {
            // Remove error styling when user starts typing
            this.classList.remove('error');
            const errorMsg = this.parentNode.querySelector('.error-message');
            if (errorMsg) {
                errorMsg.remove();
            }
        });
    });
}

function validateForm() {
    const form = document.getElementById('create-task-form');
    const requiredFields = form.querySelectorAll('[required]');
    const taskTypeSelected = form.querySelector('input[name="taskType"]:checked');

    let isValid = true;

    // Clear previous error messages
    document.querySelectorAll('.error-message').forEach(msg => msg.remove());
    document.querySelectorAll('.error').forEach(field => field.classList.remove('error'));

    // Check if task type is selected
    if (!taskTypeSelected) {
        showError('Please select a task type');
        isValid = false;
    }

    // Validate required fields
    requiredFields.forEach(field => {
        if (!validateField(field)) {
            isValid = false;
        }
    });

    // Validate deadline is in the future
    const deadlineField = document.getElementById('task-deadline');
    if (deadlineField.value) {
        const selectedDate = new Date(deadlineField.value);
        const today = new Date();
        today.setHours(0, 0, 0, 0);

        if (selectedDate <= today) {
            showFieldError(deadlineField, 'Deadline must be in the future');
            isValid = false;
        }
    }

    if (isValid) {
        showSuccess('Task created successfully!');
    }

    return isValid;
}

function validateField(field) {
    const value = field.value.trim();

    if (field.hasAttribute('required') && !value) {
        showFieldError(field, 'This field is required');
        return false;
    }

    if (field.type === 'email' && value && !isValidEmail(value)) {
        showFieldError(field, 'Please enter a valid email address');
        return false;
    }

    return true;
}

function showFieldError(field, message) {
    field.classList.add('error');

    const errorElement = document.createElement('div');
    errorElement.className = 'error-message';
    errorElement.textContent = message;
    errorElement.style.color = '#e00000';
    errorElement.style.fontSize = '0.9rem';
    errorElement.style.marginTop = '5px';

    field.parentNode.appendChild(errorElement);
}

function showError(message) {
    const errorElement = document.createElement('div');
    errorElement.className = 'form-message error';
    errorElement.textContent = message;

    const container = document.querySelector('.create-task-container');
    container.insertBefore(errorElement, container.firstChild);

    // Scroll to top to show error
    window.scrollTo({ top: 0, behavior: 'smooth' });
}

function showSuccess(message) {
    const successElement = document.createElement('div');
    successElement.className = 'form-message success';
    successElement.textContent = message;

    const container = document.querySelector('.create-task-container');
    container.insertBefore(successElement, container.firstChild);

    // Scroll to top to show success message
    window.scrollTo({ top: 0, behavior: 'smooth' });
}

function isValidEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
}