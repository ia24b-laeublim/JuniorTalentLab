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

    const clientName = task.client ?
        `${task.client.prename || ''} ${task.client.name || ''}`.trim() :
        "Unknown";

    document.getElementById("popup-title").textContent = task.title ?? "-";
    document.getElementById("popup-name").textContent = clientName;
    document.getElementById("popup-gpn").textContent = task.client?.gpn ?? "-";
    document.getElementById("popup-deadline").textContent = task.deadline ?? "-";
    document.getElementById("popup-channel").textContent = task.channel ?? "-";
    document.getElementById("popup-type").textContent = getTaskType(task);
    document.getElementById("popup-format").textContent = getTaskFormat(task);
    document.getElementById("popup-target").textContent = task.targetAudience ?? "-";
    document.getElementById("popup-budget").textContent = task.budgetChf ? `CHF ${task.budgetChf}` : "-";
    document.getElementById("popup-handover").textContent = task.handoverMethod ?? "-";
    document.getElementById("popup-description").textContent = task.description ?? "No description provided";
    document.getElementById("popup-other").textContent = getOtherRequirements(task);

    document.getElementById("popup").classList.remove("hidden");
}

function closePopup() {
    document.getElementById("popup").classList.add("hidden");
    selectedTaskId = null; // Deselect task when closing
}

function acceptTask() {
    if (!selectedTaskId) return;
    fetch(`/api/tasks/${selectedTaskId}/accept`, {method: "POST"})
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
    fetch(`/api/tasks/${selectedTaskId}/reject`, {method: "POST"})
        .then(res => {
            if (res.ok) {
                location.reload();
            } else {
                alert("Failed to reject the task.");
            }
        });
}

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

function getTaskType(task) {
    console.log("getTaskType called with task:", task);
    
    // Check Poll first (most specific)
    if (task.questionCount != null || task.questionType != null) {
        console.log("Detected as Poll");
        return "Poll";
    }
    
    // Check Video (has lengthSec)
    if (task.lengthSec != null) {
        console.log("Detected as Video");
        return "Video";
    }
    
    // Check Flyer (has paperSize AND paperType)
    if (task.paperSize != null && task.paperType != null) {
        console.log("Detected as Flyer");
        return "Flyer";
    }
    
    // Check Poster (has posterSize)
    if (task.posterSize != null) {
        console.log("Detected as Poster");
        return "Poster";
    }
    
    // Check Slideshow (has photoCount)
    if (task.photoCount != null) {
        console.log("Detected as Slideshow");
        return "Slideshow";
    }
    
    // Check Photo (more restrictive - should be ONLY Photo tasks)
    if (task.format != null && task.resolution != null && 
        task.lengthSec == null && task.photoCount == null && task.posterSize == null &&
        task.paperSize == null && task.paperType == null && task.questionCount == null && task.questionType == null) {
        console.log("Detected as Photo");
        return "Photo";
    }
    
    console.log("Detected as General Task");
    return "General Task";
}

function getTaskFormat(task) {
    if (task.maxFileSizeMb) {
        return `${task.maxFileSizeMb}MB`;
    }
    return "-";
}

function getOtherRequirements(task) {
    let requirements = [];

    // Flyer-specific requirements
    if (task.paperSize) requirements.push(`Size: ${task.paperSize}`);
    if (task.paperType) requirements.push(`Paper: ${task.paperType}`);

    // Video-specific requirements
    if (task.lengthSec) requirements.push(`Length: ${task.lengthSec}s`);
    if (task.voiceover != null) requirements.push(`Voiceover: ${task.voiceover ? 'Yes' : 'No'}`);
    if (task.disclaimer != null) requirements.push(`Disclaimer: ${task.disclaimer ? 'Yes' : 'No'}`);
    if (task.brandingRequirements) requirements.push(`Branding: ${task.brandingRequirements}`);
    if (task.musicStyle) requirements.push(`Music Style: ${task.musicStyle}`);

    // Photo-specific requirements (shared fields reused from Video)
    if (task.format) requirements.push(`Format: ${task.format}`);
    if (task.fileFormat) requirements.push(`File Format: ${task.fileFormat}`);
    if (task.resolution) requirements.push(`Resolution: ${task.resolution}`);
    if (task.socialMediaPlatforms) requirements.push(`Platforms: ${task.socialMediaPlatforms}`);

    // Slideshow-specific requirements
    if (task.photoCount) requirements.push(`Photo Count: ${task.photoCount}`);

    // Poster-specific requirements
    if (task.posterSize) requirements.push(`Poster Size: ${task.posterSize}`);
    if (task.printQualityDpi) requirements.push(`DPI: ${task.printQualityDpi}`);
    if (task.mountingType) requirements.push(`Mounting: ${task.mountingType}`);

    // Poll-specific requirements
    if (task.questionCount) requirements.push(`Questions: ${task.questionCount}`);
    if (task.questionType) requirements.push(`Type: ${task.questionType}`);
    if (task.startDate) requirements.push(`Start: ${task.startDate}`);
    if (task.endDate) requirements.push(`End: ${task.endDate}`);
    if (task.anonymous != null) requirements.push(`Anonymous: ${task.anonymous ? 'Yes' : 'No'}`);
    if (task.distributionMethod) requirements.push(`Distribution: ${task.distributionMethod}`);

    return requirements.length > 0 ? requirements.join(", ") : "No specific requirements";
}

// Helper function to validate email domain
function isValidUBSEmail(email) {
    return email && email.trim().endsWith('@ubs.com');
}

document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('create-flyer-form');
    const popup = document.getElementById('popupContainerCreate');
    const continueBtn = document.getElementById('continueBtn');
    const popupMessage = document.getElementById('popupMessageCreate');
    
    form.addEventListener('submit', e => {
        e.preventDefault();
        
        // Validate email domain first
        const emailField = document.getElementById('task-email');
        if (emailField && !isValidUBSEmail(emailField.value)) {
            popupMessage.textContent = 'Your email doesn\'t end with @ubs.com';
            popup.style.display = 'block';
            continueBtn.onclick = () => popup.style.display = 'none';
            return;
        }
        
        // Validate required fields
        const requiredFields = [
            { id: 'task-gpn', name: 'GPN' },
            { id: 'task-name', name: 'Name' },
            { id: 'task-email', name: 'Email' },
            { id: 'task-title', name: 'Task Title' },
            { id: 'task-deadline', name: 'Deadline' },
            { id: 'task-budget', name: 'Budget' },
            { id: 'task-target-audience', name: 'Target Audience' },
            { id: 'task-max-file-size', name: 'Max File Size' },
            { id: 'task-channel', name: 'Channel' },
            { id: 'task-handover-method', name: 'Handover Method' },
            { id: 'task-description', name: 'Description' }
        ];
        
        const emptyFields = [];
        requiredFields.forEach(field => {
            const element = document.getElementById(field.id);
            if (!element || !element.value.trim()) {
                emptyFields.push(field.name);
            }
        });
        
        if (emptyFields.length > 0) {
            popupMessage.textContent = `Please fill in all required fields: ${emptyFields.join(', ')}`;
            popup.style.display = 'block';
            continueBtn.onclick = () => popup.style.display = 'none';
            return;
        }
        
        // All fields are valid, show success message
        popupMessage.textContent = 'Task created successfully!';
        popup.style.display = 'block';
        continueBtn.onclick = () => {
            popup.style.display = 'none';
            form.submit();
        };
    });
});
document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('create-photo-form');
    const popup = document.getElementById('popupContainerCreate');
    const continueBtn = document.getElementById('continueBtn');
    const popupMessage = document.getElementById('popupMessageCreate');
    
    form.addEventListener('submit', e => {
        e.preventDefault();
        
        // Validate email domain first
        const emailField = document.getElementById('task-email');
        if (emailField && !isValidUBSEmail(emailField.value)) {
            popupMessage.textContent = 'Your email doesn\'t end with @ubs.com';
            popup.style.display = 'block';
            continueBtn.onclick = () => popup.style.display = 'none';
            return;
        }
        
        // Validate required fields
        const requiredFields = [
            { id: 'task-gpn', name: 'GPN' },
            { id: 'task-name', name: 'Name' },
            { id: 'task-email', name: 'Email' },
            { id: 'task-title', name: 'Task Title' },
            { id: 'task-deadline', name: 'Deadline' },
            { id: 'task-budget', name: 'Budget' },
            { id: 'task-target-audience', name: 'Target Audience' },
            { id: 'task-max-file-size', name: 'Max File Size' },
            { id: 'task-channel', name: 'Channel' },
            { id: 'task-handover-method', name: 'Handover Method' },
            { id: 'task-description', name: 'Description' }
        ];
        
        const emptyFields = [];
        requiredFields.forEach(field => {
            const element = document.getElementById(field.id);
            if (!element || !element.value.trim()) {
                emptyFields.push(field.name);
            }
        });
        
        if (emptyFields.length > 0) {
            popupMessage.textContent = `Please fill in all required fields: ${emptyFields.join(', ')}`;
            popup.style.display = 'block';
            continueBtn.onclick = () => popup.style.display = 'none';
            return;
        }
        
        // All fields are valid, show success message
        popupMessage.textContent = 'Task created successfully!';
        popup.style.display = 'block';
        continueBtn.onclick = () => {
            popup.style.display = 'none';
            form.submit();
        };
    });
});
document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('create-other-form');
    const popup = document.getElementById('popupContainerCreate');
    const continueBtn = document.getElementById('continueBtn');
    const popupMessage = document.getElementById('popupMessageCreate');
    
    form.addEventListener('submit', e => {
        e.preventDefault();
        
        // Validate email domain first
        const emailField = document.getElementById('task-email');
        if (emailField && !isValidUBSEmail(emailField.value)) {
            popupMessage.textContent = 'Your email doesn\'t end with @ubs.com';
            popup.style.display = 'block';
            continueBtn.onclick = () => popup.style.display = 'none';
            return;
        }
        
        // Validate required fields
        const requiredFields = [
            { id: 'task-gpn', name: 'GPN' },
            { id: 'task-name', name: 'Name' },
            { id: 'task-email', name: 'Email' },
            { id: 'task-title', name: 'Task Title' },
            { id: 'task-deadline', name: 'Deadline' },
            { id: 'task-budget', name: 'Budget' },
            { id: 'task-target-audience', name: 'Target Audience' },
            { id: 'task-max-file-size', name: 'Max File Size' },
            { id: 'task-channel', name: 'Channel' },
            { id: 'task-handover-method', name: 'Handover Method' },
            { id: 'task-description', name: 'Description' }
        ];
        
        const emptyFields = [];
        requiredFields.forEach(field => {
            const element = document.getElementById(field.id);
            if (!element || !element.value.trim()) {
                emptyFields.push(field.name);
            }
        });
        
        if (emptyFields.length > 0) {
            popupMessage.textContent = `Please fill in all required fields: ${emptyFields.join(', ')}`;
            popup.style.display = 'block';
            continueBtn.onclick = () => popup.style.display = 'none';
            return;
        }
        
        // All fields are valid, show success message
        popupMessage.textContent = 'Task created successfully!';
        popup.style.display = 'block';
        continueBtn.onclick = () => {
            popup.style.display = 'none';
            form.submit();
        };
    });
});
document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('create-poll-form');
    const popup = document.getElementById('popupContainerCreate');
    const continueBtn = document.getElementById('continueBtn');
    const popupMessage = document.getElementById('popupMessageCreate');
    
    form.addEventListener('submit', e => {
        e.preventDefault();
        
        // Validate email domain first
        const emailField = document.getElementById('task-email');
        if (emailField && !isValidUBSEmail(emailField.value)) {
            popupMessage.textContent = 'Your email doesn\'t end with @ubs.com';
            popup.style.display = 'block';
            continueBtn.onclick = () => popup.style.display = 'none';
            return;
        }
        
        // Validate required fields
        const requiredFields = [
            { id: 'task-gpn', name: 'GPN' },
            { id: 'task-name', name: 'Name' },
            { id: 'task-email', name: 'Email' },
            { id: 'task-title', name: 'Task Title' },
            { id: 'task-deadline', name: 'Deadline' },
            { id: 'task-budget', name: 'Budget' },
            { id: 'task-target-audience', name: 'Target Audience' },
            { id: 'task-max-file-size', name: 'Max File Size' },
            { id: 'task-channel', name: 'Channel' },
            { id: 'task-handover-method', name: 'Handover Method' },
            { id: 'task-description', name: 'Description' }
        ];
        
        const emptyFields = [];
        requiredFields.forEach(field => {
            const element = document.getElementById(field.id);
            if (!element || !element.value.trim()) {
                emptyFields.push(field.name);
            }
        });
        
        if (emptyFields.length > 0) {
            popupMessage.textContent = `Please fill in all required fields: ${emptyFields.join(', ')}`;
            popup.style.display = 'block';
            continueBtn.onclick = () => popup.style.display = 'none';
            return;
        }
        
        // All fields are valid, show success message
        popupMessage.textContent = 'Task created successfully!';
        popup.style.display = 'block';
        continueBtn.onclick = () => {
            popup.style.display = 'none';
            form.submit();
        };
    });
});
document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('create-poster-form');
    const popup = document.getElementById('popupContainerCreate');
    const continueBtn = document.getElementById('continueBtn');
    const popupMessage = document.getElementById('popupMessageCreate');
    
    form.addEventListener('submit', e => {
        e.preventDefault();
        
        // Validate email domain first
        const emailField = document.getElementById('task-email');
        if (emailField && !isValidUBSEmail(emailField.value)) {
            popupMessage.textContent = 'Your email doesn\'t end with @ubs.com';
            popup.style.display = 'block';
            continueBtn.onclick = () => popup.style.display = 'none';
            return;
        }
        
        // Validate required fields
        const requiredFields = [
            { id: 'task-gpn', name: 'GPN' },
            { id: 'task-name', name: 'Name' },
            { id: 'task-email', name: 'Email' },
            { id: 'task-title', name: 'Task Title' },
            { id: 'task-deadline', name: 'Deadline' },
            { id: 'task-budget', name: 'Budget' },
            { id: 'task-target-audience', name: 'Target Audience' },
            { id: 'task-max-file-size', name: 'Max File Size' },
            { id: 'task-channel', name: 'Channel' },
            { id: 'task-handover-method', name: 'Handover Method' },
            { id: 'task-description', name: 'Description' }
        ];
        
        const emptyFields = [];
        requiredFields.forEach(field => {
            const element = document.getElementById(field.id);
            if (!element || !element.value.trim()) {
                emptyFields.push(field.name);
            }
        });
        
        if (emptyFields.length > 0) {
            popupMessage.textContent = `Please fill in all required fields: ${emptyFields.join(', ')}`;
            popup.style.display = 'block';
            continueBtn.onclick = () => popup.style.display = 'none';
            return;
        }
        
        // All fields are valid, show success message
        popupMessage.textContent = 'Task created successfully!';
        popup.style.display = 'block';
        continueBtn.onclick = () => {
            popup.style.display = 'none';
            form.submit();
        };
    });
});
document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('create-video-form');
    const popup = document.getElementById('popupContainerCreate');
    const continueBtn = document.getElementById('continueBtn');
    const popupMessage = document.getElementById('popupMessageCreate');
    
    form.addEventListener('submit', e => {
        e.preventDefault();
        
        // Validate email domain first
        const emailField = document.getElementById('task-email');
        if (emailField && !isValidUBSEmail(emailField.value)) {
            popupMessage.textContent = 'Your email doesn\'t end with @ubs.com';
            popup.style.display = 'block';
            continueBtn.onclick = () => popup.style.display = 'none';
            return;
        }
        
        // Validate required fields
        const requiredFields = [
            { id: 'task-gpn', name: 'GPN' },
            { id: 'task-name', name: 'Name' },
            { id: 'task-email', name: 'Email' },
            { id: 'task-title', name: 'Task Title' },
            { id: 'task-deadline', name: 'Deadline' },
            { id: 'task-budget', name: 'Budget' },
            { id: 'task-target-audience', name: 'Target Audience' },
            { id: 'task-max-file-size', name: 'Max File Size' },
            { id: 'task-channel', name: 'Channel' },
            { id: 'task-handover-method', name: 'Handover Method' },
            { id: 'task-description', name: 'Description' }
        ];
        
        const emptyFields = [];
        requiredFields.forEach(field => {
            const element = document.getElementById(field.id);
            if (!element || !element.value.trim()) {
                emptyFields.push(field.name);
            }
        });
        
        if (emptyFields.length > 0) {
            popupMessage.textContent = `Please fill in all required fields: ${emptyFields.join(', ')}`;
            popup.style.display = 'block';
            continueBtn.onclick = () => popup.style.display = 'none';
            return;
        }
        
        // All fields are valid, show success message
        popupMessage.textContent = 'Task created successfully!';
        popup.style.display = 'block';
        continueBtn.onclick = () => {
            popup.style.display = 'none';
            form.submit();
        };
    });
});
document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('create-slideshow-form');
    const popup = document.getElementById('popupContainerCreate');
    const continueBtn = document.getElementById('continueBtn');
    const popupMessage = document.getElementById('popupMessageCreate');
    
    form.addEventListener('submit', e => {
        e.preventDefault();
        
        // Validate email domain first
        const emailField = document.getElementById('task-email');
        if (emailField && !isValidUBSEmail(emailField.value)) {
            popupMessage.textContent = 'Your email doesn\'t end with @ubs.com';
            popup.style.display = 'block';
            continueBtn.onclick = () => popup.style.display = 'none';
            return;
        }
        
        // Validate required fields
        const requiredFields = [
            { id: 'task-gpn', name: 'GPN' },
            { id: 'task-name', name: 'Name' },
            { id: 'task-email', name: 'Email' },
            { id: 'task-title', name: 'Task Title' },
            { id: 'task-deadline', name: 'Deadline' },
            { id: 'task-budget', name: 'Budget' },
            { id: 'task-target-audience', name: 'Target Audience' },
            { id: 'task-max-file-size', name: 'Max File Size' },
            { id: 'task-channel', name: 'Channel' },
            { id: 'task-handover-method', name: 'Handover Method' },
            { id: 'task-description', name: 'Description' }
        ];
        
        const emptyFields = [];
        requiredFields.forEach(field => {
            const element = document.getElementById(field.id);
            if (!element || !element.value.trim()) {
                emptyFields.push(field.name);
            }
        });
        
        if (emptyFields.length > 0) {
            popupMessage.textContent = `Please fill in all required fields: ${emptyFields.join(', ')}`;
            popup.style.display = 'block';
            continueBtn.onclick = () => popup.style.display = 'none';
            return;
        }
        
        // All fields are valid, show success message
        popupMessage.textContent = 'Task created successfully!';
        popup.style.display = 'block';
        continueBtn.onclick = () => {
            popup.style.display = 'none';
            form.submit();
        };
    });
});