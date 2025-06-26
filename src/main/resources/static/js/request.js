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
                      <div class="popup-row" style="background-color: #ddd; padding: 0.5rem;"><span>Client Name</span><span>${clientName}</span></div>
                      <div class="popup-row" style="background-color: #ddd; padding: 0.5rem;"><span>GPN</span><span>${clientGpn}</span></div>
                      <div class="popup-row" style="background-color: #ddd; padding: 0.5rem;"><span>Deadline</span><span>${deadlineText}</span></div>
                    </div>
                    <div style="flex: 1; background-color: #ddd; padding: 0.5rem;">
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
            if(container) container.innerHTML = "<p style='text-align: center; color: red;'>Could not load tasks. Please try again later.</p>";
        });
});

function openPopup(task) {
    selectedTaskId = task.id;

    // ✅ FIXED: Show client information, not apprentice
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
    selectedTaskId = null; // Deselect task when closing
}

function acceptTask() {
    if (!selectedTaskId) return;
    fetch(`/api/tasks/${selectedTaskId}/accept`, { method: "POST" })
        .then(res => {
            if (res.ok) {
                location.reload();
            } else {
                alert("Failed to accept the task.");
            }
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
        });
}

// Close popup when clicking outside
document.addEventListener("click", (event) => {
    const popup = document.getElementById("popup");
    if (!popup) return;

    if (popup.classList.contains("hidden")) {
        return;
    }

    if (event.target.closest(".task-card")) {
        return;
    }

    const content = popup.querySelector(".popup-content");
    if (content && !content.contains(event.target)) {
        closePopup();
    }
});

// ✅ IMPROVED: Helper function for Content Type
function getTaskType(task) {
    if (task.paperSize || task.paperType) return "Flyer";
    if (task.posterSize) return "Poster";
    if (task.photoCount) return "Slideshow";
    if (task.lengthSec) return "Video";
    if (task.questionCount) return "Poll";
    if (task.format) return "Photo";
    return "General Task";
}

// ✅ NEW: Helper function for Max File Size (replaces Format)
function getMaxFileSize(task) {
    if (task.maxFileSizeMb) {
        return `${task.maxFileSizeMb}MB`;
    }
    return "-";
}

// ✅ UPDATED: Helper function for Specific Requirements (removed Max File Size)
function getSpecificRequirements(task) {
    let requirements = [];

    // Flyer-specific requirements
    if (task.paperSize) requirements.push(`Size: ${task.paperSize}`);
    if (task.paperType) requirements.push(`Paper: ${task.paperType}`);

    // Video-specific requirements
    if (task.lengthSec) requirements.push(`Length: ${task.lengthSec}s`);
    if (task.voiceover !== null && task.voiceover !== undefined)
        requirements.push(`Voiceover: ${task.voiceover ? 'Yes' : 'No'}`);
    if (task.disclaimer !== null && task.disclaimer !== undefined)
        requirements.push(`Disclaimer: ${task.disclaimer ? 'Yes' : 'No'}`);
    if (task.brandingRequirements) requirements.push(`Branding: ${task.brandingRequirements}`);
    if (task.musicStyle) requirements.push(`Music Style: ${task.musicStyle}`);

    // Photo-specific requirements (shared fields reused from Video)
    if (task.format) requirements.push(`Format: ${task.format}`);
    if (task.fileFormat) requirements.push(`File Format: ${task.fileFormat}`);
    if (task.resolution) requirements.push(`Resolution: ${task.resolution}`);
    if (task.socialMediaPlatforms) requirements.push(`Platforms: ${task.socialMediaPlatforms}`);

    return requirements.length > 0 ? requirements.join(", ") : "No specific requirements";
}