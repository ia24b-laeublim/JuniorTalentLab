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
                card.innerHTML = `
                  <h2 style="font-size: 1.5rem; font-weight: bold; color: #000000;">${task.title}</h2>
                  <div style="display: flex; gap: 1rem;">
                    <div style="flex: 0 0 250px; display: flex; flex-direction: column; gap: 0.5rem;">
                      <div class="popup-row" style="background-color: #ddd; padding: 0.5rem;"><span>Name</span><span>${task.apprentice?.name ?? "-"}</span></div>
                      <div class="popup-row" style="background-color: #ddd; padding: 0.5rem;"><span>GPN</span><span>${task.apprentice?.gpn ?? "-"}</span></div>
                      <div class="popup-row" style="background-color: #ddd; padding: 0.5rem;"><span>Deadline</span><span>${task.deadline ?? "-"}</span></div>
                    </div>
                    <div style="flex: 1; background-color: #ddd; padding: 0.5rem;">
                      <div><strong>Description</strong></div>
                      <div>${(task.description?.substring(0, 300) ?? "-") + (task.description?.length > 300 ? '...' : '')}</div>
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
    console.log("Pop-up opened for task ID:", selectedTaskId);

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
    document.getElementById("popup-other").textContent = "—"; // Placeholder

    document.getElementById("popup").classList.remove("hidden");
}

function closePopup() {
    console.log("Closing popup.");
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

// ==================================================================
// THE FIX IS HERE: Corrected logic for closing the popup
// ==================================================================
document.addEventListener("click", (event) => {
    const popup = document.getElementById("popup");
    if (!popup) return;

    // If the popup is already hidden, there's nothing to do
    if (popup.classList.contains("hidden")) {
        return;
    }

    // Check if the click was on a task card. The .closest() method
    // checks the element itself and its parents.
    // If it is, this click was meant to OPEN the popup, so we should
    // not close it. We simply exit the function.
    if (event.target.closest(".task-card")) {
        return;
    }

    // If the click was not on a card, we check if it was inside the
    // popup's content area. If it was NOT, it means the user clicked
    // on the gray background, so we can safely close the popup.
    const content = popup.querySelector(".popup-content");
    if (content && !content.contains(event.target)) {
        closePopup();
    }
});
// ==================================================================


// Helper function for Content Type
function getTaskType(task) {
    if (task.paperSize) return "Poster";
    if (task.photoCount) return "Slideshow";
    if (task.lengthSec) return "Video";
    return "Other"; // Provide a default
}