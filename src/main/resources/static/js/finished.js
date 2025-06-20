let selectedTaskId = null;

// Diese Funktion ist called, wenn der Dropdown-Wert sich ändert
function updateTaskStatus() {
    const statusDropdown = document.getElementById("popup-status");
    const newStatus = statusDropdown.value;

    if (!selectedTaskId) {
        console.error("No task selected to update status.");
        return;
    }

    fetch(`/api/tasks/${selectedTaskId}/status`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ status: newStatus }),
    })
        .then(response => {
            if (!response.ok) {
                alert('Failed to update status. Please try again.');
            } else {
                console.log("Status updated successfully!");
            }
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

    const statusDropdown = document.getElementById("popup-status");
    statusDropdown.value = task.progress ?? "Finished";

    loadComments(task.id);
    document.getElementById("popup").classList.remove("hidden");
}

function closePopup() {
    document.getElementById("popup").classList.add("hidden");
    selectedTaskId = null;
    location.reload(); // Seite neu laden, um Änderungen sichtbar zu machen
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
                loadComments(selectedTaskId); // Kommentare neu laden
                document.getElementById("popup-comment").value = ""; // Textfeld leeren
            } else {
                alert("Failed to submit comment");
            }
        });
}

// ==========================================================
// HIER IST DIE KORREKTUR
// ==========================================================
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
                // Greift jetzt auf die korrekten DTO-Eigenschaften zu:
                // c.authorName statt c.author.name
                // c.content statt c.text
                div.innerHTML = `<strong>${c.authorName}:</strong> ${c.content}`;
                list.appendChild(div);
            });
        })
        .catch(error => {
            console.error("Error fetching or parsing comments:", error);
        });
}


function getTaskType(task) {
    if (task.paperSize) return "Poster";
    if (task.photoCount) return "Slideshow";
    if (task.lengthSec) return "Video";
    return "Other";
}

document.addEventListener("DOMContentLoaded", () => {
    fetch("/api/tasks/finished")
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

document.addEventListener("DOMContentLoaded", () => {
    const popup = document.getElementById("popup");
    const downloadBtn = popup.querySelector('[title="Download as PDF"]');

    if (downloadBtn) {
        downloadBtn.addEventListener("click", () => {
            if (!selectedTaskId) {
                console.error("❌ Keine Task-ID für PDF ausgewählt");
                return;
            }

            const link = document.createElement('a');
            link.href = `/api/tasks/${selectedTaskId}/pdf`;
            link.download = `Task_${selectedTaskId}.pdf`;
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
        });
    }
});
