let selectedTaskId = null;
let statusChanged = false;
let pagination = 1;
let maxPages = 1;

let prevBtn;
let nextBtn;
let prevNumBtn;
let nextNumBtn;
let currentBtn;
let input;

/** Updates the task status via API */
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
                statusChanged = true;
            }
        });
}

function openPopup(task) {
    selectedTaskId = task.id;
    statusChanged = false;

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
    if (statusDropdown) {
        statusDropdown.value = task.progress ?? "Finished";
    }

    const attachmentSection = document.getElementById('attachment-section');
    const attachBtn = document.getElementById('popup-attachment-btn');
    const attachFilename = document.getElementById('attachment-filename');

    if (task.attachment && task.attachment.id) {
        attachBtn.href = `/api/files/download/${task.attachment.id}`;
        attachFilename.textContent = task.attachment.filename || 'Unknown file';
        attachmentSection.style.display = 'flex';
    } else {
        attachmentSection.style.display = 'none';
    }

    loadComments(task.id);
    document.getElementById("popup").classList.remove("hidden");
}

function closePopup() {
    document.getElementById("popup").classList.add("hidden");
    selectedTaskId = null;

    if (statusChanged) {
        loadTasks(pagination);
    }
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
        .then(res => res.ok ? res.json() : [])
        .then(comments => {
            const list = document.getElementById("comment-list");
            list.innerHTML = "";

            list.style.background = "transparent";
            list.style.border = "none";
            list.style.padding = "0";

            comments.forEach(c => {
                const div = document.createElement("div");
                div.innerHTML = `<strong>${c.authorName}:</strong> ${c.content}`;
                div.style.padding = "8px 0";
                div.style.borderBottom = "1px solid #e0e0e0";
                list.appendChild(div);
            });

            if (comments.length <= 2) {
                list.style.height = "auto";
                list.style.overflowY = "visible";
            } else {
                setTimeout(() => {
                    const firstTwo = list.querySelectorAll("div:nth-child(-n+2)");
                    let totalHeight = 0;
                    firstTwo.forEach(el => totalHeight += el.offsetHeight);
                    list.style.height = Math.max(totalHeight, 100) + "px";
                    list.style.overflowY = "auto";
                }, 10);
            }
        })
        .catch(err => {
            console.error("Error loading comments:", err);
        });
}

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

/** Loads tasks for a given page */
function loadTasks(page) {
    fetch(`/api/tasks/finished?page=${page}`)
        .then(res => res.json())
        .then(data => {
            if (!Array.isArray(data)) {
                console.warn("Invalid tasks response:", data);
                return;
            }

            const container = document.getElementById("task-container");
            if (!container) {
                console.warn("task-container not found!");
                return;
            }

            container.innerHTML = "";
            data.forEach(task => {
                const card = document.createElement("div");
                card.className = "task-card";
                card.innerHTML = `
                    <h2 style="font-size: 1.5rem; font-weight: bold; color: #000;">${task.title}</h2>
                    <div style="display: flex; gap: 1rem;">
                        <div style="flex: 0 0 250px; display: flex; flex-direction: column; gap: 0.5rem;">
                            <div class="popup-row"><span>Name</span><span>${task.client?.prename || '-'} ${task.client?.name || '-'}</span></div>
                            <div class="popup-row"><span>GPN</span><span>${task.client?.gpn || '-'}</span></div>
                            <div class="popup-row"><span>Deadline</span><span>${task.deadline || '-'}</span></div>
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
        })
        .catch(err => {
            console.error("Error loading tasks:", err);
        });
}

/** Pagination logic **/
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

    fetch("/api/tasks/finished/pageAmount")
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

function disableButton(btn) {
    if (btn) {
        btn.textContent = " ";
        btn.style.backgroundColor = "#888";
        btn.style.pointerEvents = "none";
    }
}

function enableButton(btn, text) {
    if (btn) {
        btn.textContent = text;
        btn.style.backgroundColor = "#e60100";
        btn.style.pointerEvents = "auto";
    }
}

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

    loadTasks(pagination);
}

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
            if (!selectedTaskId) return;
            const link = document.createElement('a');
            link.href = `/api/tasks/${selectedTaskId}/pdf`;
            link.download = `Task_${selectedTaskId}.pdf`;
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
        });
    }
});
