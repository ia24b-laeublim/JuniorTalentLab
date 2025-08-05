// --- START OF CORRECTED script.js ---

document.addEventListener("DOMContentLoaded", function () {
    // Einmalige Initialisierung aller Komponenten
    initializeDropdown();
    initializeCarousel();
    initializeContactForm();
    initializeTaskTypeSelection();
    initializeFormValidation();
    initializeImprintHover();
    initializeDragAndDrop();

    // Der "Read More" Button benötigt keine spezielle Initialisierung,
    // da er über onclick im HTML aufgerufen wird.
});

/**
 * Initialisiert das Dropdown-Menü in der Navigation.
 */
function initializeDropdown() {
    const roleButton = document.querySelector(".role-button");
    const dropdown = document.querySelector(".dropdown");

    if (roleButton && dropdown) {
        console.log("Dropdown elements found, initializing...");
        roleButton.addEventListener("click", function (e) {
            e.stopPropagation();
            dropdown.classList.toggle("show");
        });
        document.addEventListener("click", function (e) {
            if (!dropdown.contains(e.target) && !roleButton.contains(e.target)) {
                dropdown.classList.remove("show");
            }
        });
        document.addEventListener("keydown", function(e) {
            if (e.key === "Escape") {
                dropdown.classList.remove("show");
            }
        });
    } else {
        // console.log("Dropdown elements not found on this page");
    }
}

/**
 * Initialisiert das Bilder-Karussell.
 */
function initializeCarousel() {
    const images = document.querySelectorAll('.carousel-image');
    if (images.length === 0) return;

    const dots = document.querySelectorAll('.dot');
    let currentSlideIndex = 0;
    const totalSlides = images.length;

    function showSlide(index) {
        images.forEach(img => img.classList.remove('active'));
        dots.forEach(dot => dot.classList.remove('active'));
        if (images[index]) images[index].classList.add('active');
        if (dots[index]) dots[index].classList.add('active');
    }

    function nextSlide() {
        currentSlideIndex = (currentSlideIndex + 1) % totalSlides;
        showSlide(currentSlideIndex);
    }

    window.nextSlide = nextSlide;
    window.previousSlide = () => {
        currentSlideIndex = (currentSlideIndex - 1 + totalSlides) % totalSlides;
        showSlide(currentSlideIndex);
    };
    window.currentSlide = (index) => {
        currentSlideIndex = index - 1;
        showSlide(currentSlideIndex);
    };

    showSlide(currentSlideIndex);
    setInterval(nextSlide, 5000);
}

/**
 * Initialisiert die Validierung für das Kontaktformular.
 */
function initializeContactForm() {
    const contactForm = document.getElementById("contact-form");
    if (!contactForm) return;

    const feedback = contactForm.getAttribute("data-feedback");
    if (feedback) alert(feedback);

    contactForm.addEventListener('submit', function(e) {
        e.preventDefault();
        const popup = document.getElementById('popupContainerContact');
        const popupMessage = document.getElementById('popupMessageContact');
        const continueBtn = document.getElementById('continueContactBtn');
        const emailField = document.getElementById('input-email');

        if (emailField && !emailField.value.trim().endsWith('@ubs.com')) {
            popupMessage.textContent = "Your email doesn't end with @ubs.com";
            popup.style.display = 'block';
            continueBtn.onclick = () => popup.style.display = 'none';
            return;
        }

        const requiredFields = [
            { id: 'input-firstName', name: 'First name' },
            { id: 'input-lastName', name: 'Last name' },
            { id: 'input-gpn', name: 'GPN' },
            { id: 'input-email', name: 'Email' },
            { id: 'task-description', name: 'Message' }
        ];
        const emptyFields = requiredFields.filter(field => {
            const element = document.getElementById(field.id);
            return !element || !element.value.trim();
        }).map(field => field.name);

        if (emptyFields.length > 0) {
            popupMessage.textContent = `Please fill in all required fields: ${emptyFields.join(', ')}`;
            popup.style.display = 'block';
            continueBtn.onclick = () => popup.style.display = 'none';
            return;
        }

        popupMessage.textContent = 'Message sent successfully!';
        popup.style.display = 'block';
        continueBtn.onclick = () => {
            popup.style.display = 'none';
            contactForm.submit();
        };
    });
}

/**
 * Macht die "Task Type"-Karten klickbar.
 */
function initializeTaskTypeSelection() {
    const taskTypeCards = document.querySelectorAll('.task-type-card');
    if (taskTypeCards.length === 0) return;

    taskTypeCards.forEach(card => {
        card.addEventListener('click', function() {
            taskTypeCards.forEach(c => c.classList.remove('selected'));
            this.classList.add('selected');
            const radioButton = this.querySelector('input[type="radio"]');
            if (radioButton) radioButton.checked = true;
        });
    });
}

/**
 * Initialisiert die Client-seitige Validierung für das "Create Task"-Formular.
 * KORRIGIERT: Fügt eine Prüfung hinzu, ob das Formular existiert.
 */
function initializeFormValidation() {
    const form = document.getElementById('create-task-form');
    // --- KORREKTUR START ---
    if (!form) return; // Beendet die Funktion, wenn das Formular nicht auf der Seite ist.
    // --- KORREKTUR ENDE ---

    const requiredFields = form.querySelectorAll('[required]');
    form.addEventListener('submit', function(e) {
        e.preventDefault();
        if (validateForm()) {
            this.submit();
        }
    });

    requiredFields.forEach(field => {
        field.addEventListener('blur', () => validateField(field));
        field.addEventListener('input', function() {
            this.classList.remove('error');
            const errorMsg = this.parentNode.querySelector('.error-message');
            if (errorMsg) errorMsg.remove();
        });
    });
}

/**
 * Initialisiert die Hover-Effekte für die Visitenkarten auf der Impressum-Seite.
 * KORRIGIERT: Prüft, ob die benötigten Elemente existieren.
 */
function initializeImprintHover() {
    const teamMembers = document.querySelectorAll('.team-member');
    const businessCard = document.getElementById('businessCard');

    // --- KORREKTUR START ---
    if (teamMembers.length === 0 || !businessCard) return;
    // --- KORREKTUR ENDE ---

    teamMembers.forEach(member => {
        member.addEventListener('mouseenter', function() {
            businessCard.querySelector('.card-name').textContent = this.textContent;
            businessCard.querySelector('.card-role').textContent = this.getAttribute('data-role');
            businessCard.querySelector('.card-email').textContent = this.getAttribute('data-email');
            businessCard.style.display = 'block';
        });
        member.addEventListener('mousemove', e => {
            businessCard.style.left = e.pageX + 10 + 'px';
            businessCard.style.top = e.pageY + 10 + 'px';
        });
        member.addEventListener('mouseleave', () => {
            businessCard.style.display = 'none';
        });
        member.addEventListener('click', function() {
            window.location.href = 'mailto:' + this.getAttribute('data-email');
        });
    });
}

/**
 * Initialisiert die Drag & Drop Funktionalität für den Dateiupload.
 * KORRIGIERT: Prüft, ob die Drop-Zone existiert.
 */
function initializeDragAndDrop() {
    const dropZone = document.getElementById('drop-zone');
    // --- KORREKTUR START ---
    if (!dropZone) return;
    // --- KORREKTUR ENDE ---

    const fileInput = document.getElementById('file-input');
    const fileNameSpan = document.getElementById('file-name');

    dropZone.addEventListener('click', () => fileInput.click());
    ['dragenter', 'dragover'].forEach(evName => {
        dropZone.addEventListener(evName, e => {
            e.preventDefault();
            e.stopPropagation();
            dropZone.classList.add('dragover');
        });
    });
    ['dragleave', 'drop'].forEach(evName => {
        dropZone.addEventListener(evName, e => {
            e.preventDefault();
            e.stopPropagation();
            dropZone.classList.remove('dragover');
        });
    });
    dropZone.addEventListener('drop', e => {
        const files = e.dataTransfer.files;
        if (!files.length) return;
        fileInput.files = files;
        fileNameSpan.textContent = files[0].name;
    });
    fileInput.addEventListener('change', () => {
        if (fileInput.files.length) {
            fileNameSpan.textContent = fileInput.files[0].name;
        }
    });
}


// Hilfsfunktionen (unverändert, können hier bleiben)
function toggleText(button) {
    const moreText = button.previousElementSibling;
    if (moreText.style.display === "inline") {
        moreText.style.display = "none";
        button.textContent = "Read more";
    } else {
        moreText.style.display = "inline";
        button.textContent = "Read less";
    }
}

function validateForm() {
    const form = document.getElementById('create-task-form');
    if (!form) return false;
    // ... (Rest der Funktion bleibt gleich, da sie nur aufgerufen wird, wenn das Formular existiert)
    const requiredFields = form.querySelectorAll('[required]');
    const taskTypeSelected = form.querySelector('input[name="taskType"]:checked');
    let isValid = true;
    document.querySelectorAll('.error-message').forEach(msg => msg.remove());
    document.querySelectorAll('.error').forEach(field => field.classList.remove('error'));
    if (!taskTypeSelected) {
        showError('Please select a task type');
        isValid = false;
    }
    requiredFields.forEach(field => {
        if (!validateField(field)) isValid = false;
    });
    const deadlineField = document.getElementById('task-deadline');
    if (deadlineField && deadlineField.value) {
        const selectedDate = new Date(deadlineField.value);
        const today = new Date();
        today.setHours(0, 0, 0, 0);
        if (selectedDate <= today) {
            showFieldError(deadlineField, 'Deadline must be in the future');
            isValid = false;
        }
    }
    if (isValid) showSuccess('Task created successfully!');
    return isValid;
}

function validateField(field) {
    const value = field.value.trim();
    if (field.hasAttribute('required') && !value) {
        showFieldError(field, 'This field is required');
        return false;
    }
    if (field.type === 'email' && value && !isValidEmail(value)) {
        showFieldError(field, 'Please enter a valid @ubs.com email address');
        return false;
    }
    return true;
}

function showFieldError(field, message) {
    field.classList.add('error');
    const errorElement = document.createElement('div');
    errorElement.className = 'error-message';
    errorElement.textContent = message;
    errorElement.style.cssText = 'color: #E60100; font-size: 0.9rem; margin-top: 5px;';
    field.parentNode.appendChild(errorElement);
}

function showError(message) {
    const container = document.querySelector('.create-task-container');
    if (!container) return;
    const errorElement = document.createElement('div');
    errorElement.className = 'form-message error';
    errorElement.textContent = message;
    container.insertBefore(errorElement, container.firstChild);
    window.scrollTo({ top: 0, behavior: 'smooth' });
}

function showSuccess(message) {
    const container = document.querySelector('.create-task-container');
    if (!container) return;
    const successElement = document.createElement('div');
    successElement.className = 'form-message success';
    successElement.textContent = message;
    container.insertBefore(successElement, container.firstChild);
    window.scrollTo({ top: 0, behavior: 'smooth' });
}

function isValidEmail(email) {
    return /^[a-zA-Z0-9._%+-]+@ubs\.com$/.test(email);
}