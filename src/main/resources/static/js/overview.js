let selectedTaskId = null;

// Diese Funktion wird aufgerufen, wenn der Status geändert wird
function updateTaskStatus() {
    const statusDropdown = document.getElementById("popup-status");
    const newStatus = statusDropdown.value;

    if (!selectedTaskId) {
        console.error("No task selected to update status.");
        return;
    }

    console.log(`Updating task ${selectedTaskId} to status: ${newStatus}`);

    fetch(`/api/tasks/${selectedTaskId}/status`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ status: newStatus }),
    })
        .then(response => {
            if (!response.ok) {
                alert('Failed to update status. Please try again.');
            } else {
                console.log("Status updated successfully!");
            }
        })
        .catch(error => {
            console.error('Error updating status:', error);
            alert('An error occurred while updating the status.');
        });
}


function openPopup(task) {
    selectedTaskId = task.id;

    const clientName = task.client ? `${task.client.prename || ''} ${task.client.name || ''}`.trim() : "-";

    document.getElementById("popup-title").textContent = task.title ?? "-";
    document.getElementById("popup-name").textContent = clientName;
    document.getElementById("popup-gpn").textContent = task.client?.gpn ?? "-";
    document.getElementById("popup-deadline").textContent = task.deadline ?? "-";
    document.getElementById("popup-channel").textContent = task.channel ?? "-";
    document.getElementById("popup-type").textContent = getTaskType(task);
    document.getElementById("popup-format").textContent = getMaxFileSize(task);
    document.getElementById("popup-target").textContent = task.targetAudience ?? "-";
    document.getElementById("popup-budget").textContent = task.budgetChf ?? "-";
    document.getElementById("popup-handover").textContent = task.handoverMethod ?? "-";
    document.getElementById("popup-description").textContent = task.description ?? "-";
    document.getElementById("popup-other").textContent = getSpecificRequirements(task);

    const statusDropdown = document.getElementById("popup-status");
    const validOptions = Array.from(statusDropdown.options).map(opt => opt.value);

    const savedStatus = task.progress?.trim();

    if (savedStatus && validOptions.includes(savedStatus)) {
        statusDropdown.value = savedStatus;
    } else {
        statusDropdown.value = "Started";
    }

    loadComments(task.id);
    document.getElementById("popup").classList.remove("hidden");
}

function closePopup() {
    document.getElementById("popup").classList.add("hidden");
    selectedTaskId = null;
    location.reload();
}

function submitComment() {
    const text = document.getElementById("popup-comment").value.trim();
    if (!text || !selectedTaskId) return;

    fetch(`/api/tasks/${selectedTaskId}/comments`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ text: text })
    })
        .then(res => {
            if (res.ok) {
                loadComments(selectedTaskId);
                document.getElementById("popup-comment").value = "";
            } else {
                alert("Failed to submit comment");
            }
        });
}

function loadComments(taskId) {
    fetch(`/api/tasks/${taskId}/comments`)
        .then(res => {
            if (!res.ok) {
                console.error("Failed to load comments, server responded with status: " + res.status);
                return [];
            }
            return res.json();
        })
        .then(comments => {
            const list = document.getElementById("comment-list");
            list.innerHTML = "";
            comments.forEach(c => {
                const div = document.createElement("div");
                div.innerHTML = `<strong>${c.authorName}:</strong> ${c.content}`;
                div.style.borderBottom = "1px solid #eee";
                div.style.padding = "8px 0";
                list.appendChild(div);
            });
        })
        .catch(error => {
            console.error("Error fetching or parsing comments:", error);
        });
}

function getTaskType(task) {
    if (task.paperSize || task.paperType) return "Flyer";
    if (task.posterSize) return "Poster";
    if (task.photoCount) return "Slideshow";
    if (task.lengthSec) return "Video";
    if (task.questionCount) return "Poll";
    if (task.format) return "Photo";
    return "General Task";
}


document.addEventListener("DOMContentLoaded", () => {
    fetch("/api/tasks/accepted")
        .then(res => res.json())
        .then(data => {
            const container = document.getElementById("task-container");
            if (!container) return;
            data.forEach(task => {
                const card = document.createElement("div");
                card.className = "task-card";

                const clientName = task.client ? `${task.client.prename || ''} ${task.client.name || ''}`.trim() : "-";
                const clientGpn = task.client?.gpn ?? "-";

                card.innerHTML = `
                  <h2 style="font-size: 1.5rem; font-weight: bold; color: #000000;">${task.title}</h2>
                  <div style="display: flex; gap: 1rem;">
                    <div style="flex: 0 0 250px; display: flex; flex-direction: column; gap: 0.5rem;">
                      <div class="popup-row"><span>Name</span><span>${clientName}</span></div>
                      <div class="popup-row"><span>GPN</span><span>${clientGpn}</span></div>
                      <div class="popup-row"><span>Deadline</span><span>${task.deadline ?? "-"}</span></div>
                    </div>
                    <div style="flex: 1; background-color: #f5f5f5; padding: 1rem; border-radius: 4px;">
                      <div><strong>Description</strong></div>
                      <p style="margin-top: 0.5rem;">${(task.description?.substring(0, 150) ?? "-") + (task.description?.length > 150 ? '...' : '')}</p>
                    </div>
                  </div>
                `;
                card.addEventListener("click", () => openPopup(task));
                container.appendChild(card);
            });
        });
});

document.addEventListener("click", (event) => {
    const popup = document.getElementById("popup");
    if (!popup || popup.classList.contains("hidden")) return;
    if (event.target.closest(".task-card")) return;
    const content = popup.querySelector(".popup-content");
    if (content && !content.contains(event.target)) {
        closePopup();
    }
});

document.addEventListener("DOMContentLoaded", () => {
    const popup = document.getElementById("popup");
    popup.querySelector('[title="Download as PDF"]').addEventListener("click", () => {
        if (!selectedTaskId) return;

        const link = document.createElement('a');
        link.href = `/api/tasks/${selectedTaskId}/pdf`;
        link.download = `Task_${selectedTaskId}.pdf`;
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
    });
});

function getMaxFileSize(task) {
    // This assumes maxFileSizeMb comes from the child table (e.g., FlyerTask)
    if (task.maxFileSizeMb) {
        return `${task.maxFileSizeMb}MB`;
    }
    return "-";
}

function getSpecificRequirements(task) {
    const requirements = [];

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
    if (task.format) requirements.push(`Format: ${task.format}`);
    if (task.fileFormat) requirements.push(`File Format: ${task.fileFormat}`);
    if (task.resolution) requirements.push(`Resolution: ${task.resolution}`);
    if (task.musicStyle) requirements.push(`Music Style: ${task.musicStyle}`);
    if (task.socialMediaPlatforms) requirements.push(`Platforms: ${task.socialMediaPlatforms}`);

    return requirements.length > 0 ? requirements.join(", ") : "No specific requirements";
}
