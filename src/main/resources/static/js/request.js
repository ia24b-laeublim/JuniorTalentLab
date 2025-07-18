let selectedTaskId = null;

document.addEventListener("DOMContentLoaded", () => {
    fetch("/api/tasks/open")
        .then(res => {
            if (!res.ok) {
                throw new Error(`HTTP error! status: ${res.status}`);
            }
            return res.json();
        })
        .then(data => {
            const container = document.getElementById("task-container");
            if (!container) return; // Guard clause

            data.forEach(task => {
                const card = document.createElement("div");
                card.className = "task-card";

                // ✅ FIXED: Use client instead of apprentice
                const clientName = task.client ?
                    `${task.client.prename || ''} ${task.client.name || ''}`.trim() :
                    "Unknown";
                const clientGpn = task.client?.gpn || "-";
                const deadlineText = task.deadline || "-";

                card.innerHTML = `
                  <h2 style="font-size: 1.5rem; font-weight: bold; color: #000000;">${task.title}</h2>
                  <div style="display: flex; gap: 1rem;">
                    <div style="flex: 0 0 250px; display: flex; flex-direction: column; gap: 0.5rem;">
                      <div class="popup-row" style="background-color: #f5f5f5; padding: 0.5rem;"><span>Client Name</span><span>${clientName}</span></div>
                      <div class="popup-row" style="background-color: #f5f5f5; padding: 0.5rem;"><span>GPN</span><span>${clientGpn}</span></div>
                      <div class="popup-row" style="background-color: #f5f5f5; padding: 0.5rem;"><span>Deadline</span><span>${deadlineText}</span></div>
                    </div>
                    <div style="flex: 1; background-color: #f5f5f5; padding: 0.5rem;">
                      <div><strong>Description</strong></div>
                      <div>${(task.description?.substring(0, 300) ?? "No description provided") + (task.description?.length > 300 ? '...' : '')}</div>
                    </div>
                  </div>
                `;

                card.addEventListener("click", () => openPopup(task));
                container.appendChild(card);
            });
        })
        .catch(error => {
            console.error("Could not fetch open tasks:", error);
            const container = document.getElementById("task-container");
            if (container) container.innerHTML = "<p style='text-align: center; color: #E60100;'>Could not load tasks. Please try again later.</p>";
        });
});

function openPopup(task) {
    selectedTaskId = task.id;
    console.log("openPopup called with task:", task);
    console.log("selectedTaskId set to:", selectedTaskId);

    const clientName = task.client ?
        `${task.client.prename || ''} ${task.client.name || ''}`.trim() :
        "Unknown";

    document.getElementById("popup-title").textContent = task.title ?? "-";
    document.getElementById("popup-name").textContent = clientName;
    document.getElementById("popup-gpn").textContent = task.client?.gpn ?? "-";
    document.getElementById("popup-deadline").textContent = task.deadline ?? "-";
    document.getElementById("popup-channel").textContent = task.channel ?? "-";
    document.getElementById("popup-type").textContent = getTaskType(task);
    // ✅ CHANGED: Now showing Max File Size instead of Format
    document.getElementById("popup-format").textContent = getMaxFileSize(task);
    document.getElementById("popup-target").textContent = task.targetAudience ?? "-";
    document.getElementById("popup-budget").textContent = task.budgetChf ? `CHF ${task.budgetChf}` : "-";
    document.getElementById("popup-handover").textContent = task.handoverMethod ?? "-";
    document.getElementById("popup-description").textContent = task.description ?? "No description provided";
    document.getElementById("popup-other").textContent = getSpecificRequirements(task);

    document.getElementById("popup").classList.remove("hidden");
}

function closePopup() {
    document.getElementById("popup").classList.add("hidden");
    selectedTaskId = null;
}

function acceptTask() {
    if (!selectedTaskId) return;
    showAcceptNamePopup();
}

function showAcceptNamePopup() {
    const overlay = document.getElementById("acceptOverlay");
    const firstNameInput = document.getElementById("firstNameInput");
    const lastNameInput = document.getElementById("lastNameInput");
    const gpnInput = document.getElementById("gpnInput");
    const confirmBtn = document.getElementById("confirmAcceptBtn");
    const cancelBtn = document.getElementById("cancelAcceptBtn");
    
    // Clear previous inputs
    firstNameInput.value = "";
    lastNameInput.value = "";
    gpnInput.value = "";
    
    // Show overlay
    overlay.classList.remove("hidden");
    overlay.style.display = "flex";
    
    // Focus on first input
    firstNameInput.focus();
    
    // Set up event handlers
    confirmBtn.onclick = () => confirmAcceptTask();
    cancelBtn.onclick = (event) => {
        event.stopPropagation();
        closeAcceptNamePopup();
    };
    
    // Allow Enter key navigation
    firstNameInput.onkeypress = (e) => {
        if (e.key === 'Enter') {
            lastNameInput.focus();
        }
    };
    
    lastNameInput.onkeypress = (e) => {
        if (e.key === 'Enter') {
            gpnInput.focus();
        }
    };
    
    gpnInput.onkeypress = (e) => {
        if (e.key === 'Enter') {
            confirmAcceptTask();
        }
    };
    
    // GPN input validation - only numbers (handled by HTML5 type="number")
}

function closeAcceptNamePopup() {
    const overlay = document.getElementById("acceptOverlay");
    overlay.classList.add("hidden");
    overlay.style.display = "none";
    // Ensure main popup is visible when overlay closes
    const mainPopup = document.getElementById("popup");
    if (mainPopup) {
        mainPopup.classList.remove("hidden");
    }
}

function confirmAcceptTask() {
    const firstName = document.getElementById("firstNameInput").value.trim();
    const lastName = document.getElementById("lastNameInput").value.trim();
    const gpnValue = document.getElementById("gpnInput").value.trim();
    
    if (!firstName || !lastName || !gpnValue) {
        alert("Please enter first name, last name, and GPN.");
        return;
    }
    
    const gpn = parseInt(gpnValue);
    if (isNaN(gpn) || gpn <= 0) {
        alert("GPN must be a valid positive number.");
        return;
    }
    
    if (!selectedTaskId) return;
    
    fetch(`/api/tasks/${selectedTaskId}/accept`, { 
        method: "POST",
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            firstName: firstName,
            lastName: lastName,
            gpn: gpn
        })
    })
    .then(res => {
        if (res.ok) {
            closeAcceptNamePopup();
            location.reload();
        } else {
            alert("Failed to accept the task.");
        }
    })
    .catch(error => {
        console.error('Error accepting task:', error);
        alert('An error occurred while accepting the task.');
    });
}

function rejectTask() {
    if (!selectedTaskId) return;
    fetch(`/api/tasks/${selectedTaskId}/reject`, { method: "POST" })
        .then(res => {
            if (res.ok) {
                location.reload();
            } else {
                alert("Failed to reject the task.");
            }
        })
        .catch(error => {
            console.error('Error rejecting task:', error);
            alert('An error occurred while rejecting the task.');
        });
}

// ——————————————————————————————————
// CLOSE MAIN POPUP WHEN CLICKING OUTSIDE
// (but NOT when the “Are you sure?” confirm overlay is visible)
// ——————————————————————————————————
document.addEventListener("click", (event) => {
    const overlay2 = document.getElementById("popupOverlay2");
    const acceptOverlay = document.getElementById("acceptOverlay");
    // ← CHANGED: bail out immediately if confirm‐overlay or accept overlay is open
    if (overlay2 && !overlay2.classList.contains("hidden")) {
        return;
    }
    if (acceptOverlay && !acceptOverlay.classList.contains("hidden")) {
        return;
    }

    const popup = document.getElementById("popup");
    if (!popup || popup.classList.contains("hidden")) return;
    if (event.target.closest(".task-card")) return;

    const content = popup.querySelector(".popup-content");
    if (content && !content.contains(event.target)) {
        closePopup();
    }
});

function showRejectConfirm() {
    console.log("showRejectConfirm called, selectedTaskId:", selectedTaskId);

    const overlay2   = document.getElementById("popupOverlay2");
    const box2       = document.getElementById("popupContainer2");
    const msg        = document.getElementById("popupMessage");
    const btnOK      = document.getElementById("acceptBtn");
    const btnCancel  = document.getElementById("rejectBtn");

    if (!overlay2 || !box2 || !msg || !btnOK || !btnCancel) {
        console.error("Required popup elements not found");
        alert("Popup elements not found - check console");
        return;
    }
    if (!selectedTaskId) {
        console.error("No task selected");
        alert("No task selected");
        return;
    }

    msg.textContent = "Are you sure you want to reject the task?";
    overlay2.classList.remove("hidden");
    overlay2.style.display = "flex";

    btnOK.onclick     = () => { rejectTask(); closePopup2(); };
    btnCancel.onclick = (event) => {
        event.stopPropagation();
        closePopup2();
    };
}

function closePopup2() {
    const overlay2 = document.getElementById("popupOverlay2");
    if (overlay2) {
        overlay2.classList.add("hidden");
        overlay2.style.display = "none";
    }
}

// ✅ IMPROVED: Helper function for Content Type
function getTaskType(task) {
    if (task.paperSize && task.paperType) return "Flyer";
    if (task.posterSize) return "Poster";
    if (task.photoCount) return "Slideshow";
    if (task.lengthSec) return "Video";
    if (task.questionCount) return "Poll";
    if (task.format && task.resolution) return "Photo";
    return "General Task";
}

// ✅ NEW: Helper function for Max File Size (replaces Format)
function getMaxFileSize(task) {
    if (task.maxFileSizeMb) {
        return `${task.maxFileSizeMb}MB`;
    }
    return "-";
}

// ✅ UPDATED: Helper function for Specific Requirements
function getSpecificRequirements(task) {
    let requirements = [];

    // Flyer-specific
    if (task.paperSize) requirements.push(`Size: ${task.paperSize}`);
    if (task.paperType) requirements.push(`Paper: ${task.paperType}`);

    // Video-specific
    if (task.lengthSec) requirements.push(`Length: ${task.lengthSec}s`);
    if (task.voiceover != null) requirements.push(`Voiceover: ${task.voiceover ? 'Yes' : 'No'}`);
    if (task.disclaimer != null) requirements.push(`Disclaimer: ${task.disclaimer ? 'Yes' : 'No'}`);
    if (task.brandingRequirements) requirements.push(`Branding: ${task.brandingRequirements}`);
    if (task.musicStyle) requirements.push(`Music Style: ${task.musicStyle}`);

    // Photo-specific
    if (task.format) requirements.push(`Format: ${task.format}`);
    if (task.fileFormat) requirements.push(`File Format: ${task.fileFormat}`);
    if (task.resolution) requirements.push(`Resolution: ${task.resolution}`);
    if (task.socialMediaPlatforms) requirements.push(`Platforms: ${task.socialMediaPlatforms}`);

    // Slideshow-specific
    if (task.photoCount) requirements.push(`Photo Count: ${task.photoCount}`);

    // Poster-specific
    if (task.posterSize) requirements.push(`Poster Size: ${task.posterSize}`);
    if (task.printQualityDpi) requirements.push(`DPI: ${task.printQualityDpi}`);
    if (task.mountingType) requirements.push(`Mounting: ${task.mountingType}`);

    // Poll-specific
    if (task.questionCount) requirements.push(`Questions: ${task.questionCount}`);
    if (task.questionType) requirements.push(`Type: ${task.questionType}`);
    if (task.startDate) requirements.push(`Start: ${task.startDate}`);
    if (task.endDate) requirements.push(`End: ${task.endDate}`);
    if (task.anonymous != null) requirements.push(`Anonymous: ${task.anonymous ? 'Yes' : 'No'}`);
    if (task.distributionMethod) requirements.push(`Distribution: ${task.distributionMethod}`);

    return requirements.length > 0
        ? requirements.join(", ")
        : "No specific requirements";
}
