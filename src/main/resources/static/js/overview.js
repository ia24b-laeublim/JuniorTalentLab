let selectedTaskId = null;

// This function is called when the dropdown value changes
function updateTaskStatus() {
    const statusDropdown = document.getElementById("popup-status");
    const newStatus = statusDropdown.value;

    if (!selectedTaskId) {
        console.error("No task selected to update status.");
        return;
    }

    console.log(`Updating task ${selectedTaskId} to status: ${newStatus}`);

    // Send the update to the backend
    fetch(`/api/tasks/${selectedTaskId}/status`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ status: newStatus }), // Send the new status in the body
    })
        .then(response => {
            if (!response.ok) {
                // If the server returns an error, show an alert
                alert('Failed to update status. Please try again.');
            } else {
                console.log("Status updated successfully!");
                // Optional: You could show a small "Saved!" message here
            }
        })
        .catch(error => {
            console.error('Error updating status:', error);
            alert('An error occurred while updating the status.');
        });
}


function openPopup(task) {
    selectedTaskId = task.id;

    document.getElementById("popup-title").textContent = task.title ?? "-";
    document.getElementById("popup-name").textContent = task.apprentice?.name ?? "-";
    document.getElementById("popup-gpn").textContent = task.apprentice?.gpn ?? "-";
    document.getElementById("popup-deadline").textContent = task.deadline ?? "-";
    document.getElementById("popup-channel").textContent = task.channel ?? "-";
    document.getElementById("popup-type").textContent = getTaskType(task);
    document.getElementById("popup-format").textContent = task.format ?? "-";
    document.getElementById("popup-target").textContent = task.targetAudience ?? "-";
    document.getElementById("popup-budget").textContent = task.budgetChf ?? "-";
    document.getElementById("popup-handover").textContent = task.handoverMethod ?? "-";
    document.getElementById("popup-description").textContent = task.description ?? "-";
    document.getElementById("popup-other").textContent = task.otherRequirements ?? "—";

    // Set the status dropdown to the correct value from the task
    const statusDropdown = document.getElementById("popup-status");
    if (statusDropdown) {
        statusDropdown.value = task.statusProgress ?? "Started";
    }

    loadComments(task.id);
    document.getElementById("popup").classList.remove("hidden");
}

function closePopup() {
    document.getElementById("popup").classList.add("hidden");
    selectedTaskId = null;
}

function submitComment() {
    const text = document.getElementById("popup-comment").value.trim();
    if (!text || !selectedTaskId) return;

    fetch(`/api/tasks/${selectedTaskId}/comments`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ text: text })
    })
        .then(res => res.ok ? loadComments(selectedTaskId) : alert("Failed to submit comment"))
        .then(() => document.getElementById("popup-comment").value = "");
}

function loadComments(taskId) {
    fetch(`/api/tasks/${taskId}/comments`)
        .then(res => res.json())
        .then(comments => {
            const list = document.getElementById("comment-list");
            list.innerHTML = "";
            comments.forEach(c => {
                const div = document.createElement("div");
                div.innerHTML = `<strong>${c.author?.name ?? 'User'}</strong>: ${c.text}`;
                div.style.borderBottom = "1px solid #eee";
                div.style.padding = "8px 0";
                list.appendChild(div);
            });
        });
}

function getTaskType(task) {
    if (task.paperSize) return "Poster";
    if (task.photoCount) return "Slideshow";
    if (task.lengthSec) return "Video";
    return "Other";
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
                card.innerHTML = `
                  <h2 style="font-size: 1.5rem; font-weight: bold; color: #000000;">${task.title}</h2>
                  <div style="display: flex; gap: 1rem;">
                    <div style="flex: 0 0 250px; display: flex; flex-direction: column; gap: 0.5rem;">
                      <div class="popup-row"><span>Name</span><span>${task.apprentice?.name ?? "-"}</span></div>
                      <div class="popup-row"><span>GPN</span><span>${task.apprentice?.gpn ?? "-"}</span></div>
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