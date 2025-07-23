let selectedTaskId = null;
let pagination = 1;
let maxPages = 1;

let prevBtn;
let nextBtn;
let prevNumBtn;
let nextNumBtn;
let currentBtn;
let input;

/** Disable a pagination button */
function disableButton(btn) {
    if (btn) {
        btn.textContent = " ";
        btn.style.backgroundColor = "#888";
        btn.style.pointerEvents = "none";
    }
}

/** Enable a pagination button with text */
function enableButton(btn, text) {
    if (btn) {
        btn.textContent = text;
        btn.style.backgroundColor = "#e60100";
        btn.style.pointerEvents = "auto";
    }
}

/** Update pagination UI and load data */
function updatePagination(newPage) {
    pagination = Math.max(1, Math.min(newPage, maxPages));

    const prevNum = pagination - 1;
    const nextNum = pagination + 1;

    if (prevNum >= 1) {
        enableButton(prevNumBtn, prevNum.toString());
        enableButton(prevBtn, "«");
    } else {
        disableButton(prevNumBtn);
        disableButton(prevBtn);
    }

    if (nextNum <= maxPages) {
        enableButton(nextNumBtn, nextNum.toString());
        enableButton(nextBtn, "»");
    } else {
        disableButton(nextNumBtn);
        disableButton(nextBtn);
    }

    if (currentBtn) currentBtn.textContent = pagination;
    if (input) input.value = pagination;

    loadOpenTasks(pagination);
}

/** Loads Open Tasks for given page */
function loadOpenTasks(page) {
    fetch(`/api/tasks/open?page=${page}`)
        .then(res => {
            if (!res.ok) {
                throw new Error(`HTTP error! status: ${res.status}`);
            }
            return res.json();
        })
        .then(data => {
            const container = document.getElementById("task-container");
            if (!container) return;

            container.innerHTML = "";

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
            if(container) container.innerHTML = "<p style='text-align: center; color: #E60100;'>Could not load tasks. Please try again later.</p>";
        });
}

/** Opens the popup for a task */
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

document.addEventListener("click", (event) => {
    const overlay2 = document.getElementById("popupOverlay2");
    if (overlay2 && overlay2.style.display === "block") return;

    const popup = document.getElementById("popup");
    if (!popup || popup.classList.contains("hidden")) return;
    if (event.target.closest(".task-card")) return;

    const content = popup.querySelector(".popup-content");
    if (content && !content.contains(event.target)) {
        closePopup();
    }
});

function getTaskType(task) {
    if (task.paperSize && task.paperType) return "Flyer";
    if (task.posterSize) return "Poster";
    if (task.photoCount) return "Slideshow";
    if (task.lengthSec) return "Video";
    if (task.questionCount) return "Poll";
    if (task.format && task.resolution) return "Photo";
    return "General Task";
}

function getMaxFileSize(task) {
    if (task.maxFileSizeMb) {
        return `${task.maxFileSizeMb}MB`;
    }
    return "-";
}

function getSpecificRequirements(task) {
    let requirements = [];

    if (task.paperSize) requirements.push(`Size: ${task.paperSize}`);
    if (task.paperType) requirements.push(`Paper: ${task.paperType}`);
    if (task.lengthSec) requirements.push(`Length: ${task.lengthSec}s`);
    if (task.voiceover !== null && task.voiceover !== undefined)
        requirements.push(`Voiceover: ${task.voiceover ? 'Yes' : 'No'}`);
    if (task.disclaimer !== null && task.disclaimer !== undefined)
        requirements.push(`Disclaimer: ${task.disclaimer ? 'Yes' : 'No'}`);
    if (task.brandingRequirements) requirements.push(`Branding: ${task.brandingRequirements}`);
    if (task.musicStyle) requirements.push(`Music Style: ${task.musicStyle}`);
    if (task.format) requirements.push(`Format: ${task.format}`);
    if (task.fileFormat) requirements.push(`File Format: ${task.fileFormat}`);
    if (task.resolution) requirements.push(`Resolution: ${task.resolution}`);
    if (task.socialMediaPlatforms) requirements.push(`Platforms: ${task.socialMediaPlatforms}`);
    if (task.photoCount) requirements.push(`Photo Count: ${task.photoCount}`);
    if (task.posterSize) requirements.push(`Poster Size: ${task.posterSize}`);
    if (task.printQualityDpi) requirements.push(`DPI: ${task.printQualityDpi}`);
    if (task.mountingType) requirements.push(`Mounting: ${task.mountingType}`);
    if (task.questionCount) requirements.push(`Questions: ${task.questionCount}`);
    if (task.questionType) requirements.push(`Type: ${task.questionType}`);
    if (task.startDate) requirements.push(`Start: ${task.startDate}`);
    if (task.endDate) requirements.push(`End: ${task.endDate}`);
    if (task.anonymous !== null && task.anonymous !== undefined)
        requirements.push(`Anonymous: ${task.anonymous ? 'Yes' : 'No'}`);
    if (task.distributionMethod) requirements.push(`Distribution: ${task.distributionMethod}`);

    return requirements.length > 0 ? requirements.join(", ") : "No specific requirements";
}

function showRejectConfirm() {
    const overlay2 = document.getElementById("popupOverlay2");
    const box2     = document.getElementById("popupContainer2");
    const msg      = document.getElementById("popupMessage");
    const btnOK    = document.getElementById("acceptBtn");
    const btnCancel= document.getElementById("rejectBtn");

    msg.textContent        = "Are you sure you want to reject the task?";
    overlay2.style.display = "block";
    box2.style.display     = "block";

    overlay2.addEventListener("click", e => e.stopPropagation());
    box2.addEventListener   ("click", e => e.stopPropagation());

    btnOK.onclick     = () => { rejectTask(); closePopup2(); };
    btnCancel.onclick = () => closePopup2();
}

function closePopup2() {
    document.getElementById("popupOverlay2").style.display   = "none";
    document.getElementById("popupContainer2").style.display = "none";
}

document.addEventListener("DOMContentLoaded", () => {
    prevBtn = document.getElementById("prevPage");
    nextBtn = document.getElementById("nextPage");
    prevNumBtn = document.getElementById("prevNumberPage");
    nextNumBtn = document.getElementById("nextNumberPage");
    currentBtn = document.getElementById("currentPageBtn");
    input = document.getElementById("pageInput");

    if (prevBtn) prevBtn.addEventListener("click", e => {
        e.preventDefault();
        updatePagination(pagination - 1);
    });

    if (prevNumBtn) prevNumBtn.addEventListener("click", e => {
        e.preventDefault();
        updatePagination(pagination - 1);
    });

    if (nextBtn) nextBtn.addEventListener("click", e => {
        e.preventDefault();
        updatePagination(pagination + 1);
    });

    if (nextNumBtn) nextNumBtn.addEventListener("click", e => {
        e.preventDefault();
        updatePagination(pagination + 1);
    });

    if (currentBtn) currentBtn.addEventListener("click", e => {
        e.preventDefault();
    });

    if (input) {
        input.addEventListener("change", () => {
            const val = parseInt(input.value);
            if (!isNaN(val) && val >= 1) {
                updatePagination(val);
            } else {
                input.value = pagination;
            }
        });
    }

    fetch("/api/tasks/open/pageAmount")
        .then(res => res.json())
        .then(pages => {
            maxPages = Math.max(1, pages);
            updatePagination(1);
        })
        .catch(err => {
            console.error("Error fetching page count:", err);
            maxPages = 1;
            updatePagination(1);
        });
});
