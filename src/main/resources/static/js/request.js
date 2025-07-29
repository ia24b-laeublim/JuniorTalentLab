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
            if (container) container.innerHTML = "<p style='text-align: center; color: #E60100;'>Could not load tasks. Please try again later.</p>";
        });
}

/** Opens the popup for a task */
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

// Close popup when clicking outside
document.addEventListener("click", (event) => {
    const overlay2 = document.getElementById("popupOverlay2");
    if (overlay2 && overlay2.style.display === "block") return;

    const popup = document.getElementById("popup");
    if (!popup) return;

    if (popup.classList.contains("hidden")) {
        return;
    }

    if (event.target.closest(".task-card")) {
        return;
    }

    // ⛔ Wenn auf Task-Card geklickt wird → NICHTS TUN
    if (event.target.closest(".task-card")) return;

    // ✅ Wenn außerhalb vom Popup-Content geklickt wird → Schließen
    const content = popup.querySelector(".popup-content");
    if (content && !content.contains(event.target)) {
        closePopup();
    }
});

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

function showRejectConfirm() {
    document.getElementById("popupMessage").textContent = "Are you sure you want to reject the task?";

    document.getElementById("popupOverlay2").style.display = "block";
    document.getElementById("popupContainer2").style.display = "block";

    document.getElementById("acceptBtn").onclick = function() {
        rejectTask();
        closePopup2();

    };

    document.getElementById("rejectBtn").onclick = function() {
        closePopup2();

    };
}

function closePopup2() {
    document.getElementById("popupOverlay2").style.display = "none";
    document.getElementById("popupContainer2").style.display = "none";
}


document.addEventListener("click", (event) => {
    const overlay2 = document.getElementById("popupOverlay2");
    if (overlay2.style.display === "block") return;

    const popup = document.getElementById("popup");
    if (!popup || popup.classList.contains("hidden")) return;
    if (event.target.closest(".task-card")) return;

    const content = popup.querySelector(".popup-content");
    if (content && !content.contains(event.target)) {
        closePopup();
    }
});


function showRejectConfirm() {
    // Statt sofort rejectTask() zu rufen, öffnest du jetzt das Formular:
    if (!selectedTaskId) {
        alert("No task selected");
        return;
    }
    showRejectForm();
}

function showRejectForm() {
    const overlay = document.getElementById("rejectOverlay");
    const f = document.getElementById("rejFirstName");
    const l = document.getElementById("rejLastName");
    const r = document.getElementById("rejReason");
    const ok = document.getElementById("confirmRejectBtn");
    const cancel = document.getElementById("cancelRejectBtn");

    f.value = ""; l.value = ""; r.value = "";
    overlay.classList.remove("hidden");
    overlay.style.display = "flex";
    f.focus();

    ok.onclick = () => confirmRejectTask();
    cancel.onclick = (e) => { e.stopPropagation(); closeRejectForm(); };

    // optional: Enter-Handling
    r.onkeypress = (e) => { if (e.key === "Enter" && e.shiftKey === false) { e.preventDefault(); confirmRejectTask(); } };
}

function closeRejectForm() {
    const overlay = document.getElementById("rejectOverlay");
    overlay.classList.add("hidden");
    overlay.style.display = "none";
    // Hauptpopup sichtbar lassen
    document.getElementById("popup")?.classList.remove("hidden");
}

function confirmRejectTask() {
    const firstName = document.getElementById("rejFirstName").value.trim();
    const lastName  = document.getElementById("rejLastName").value.trim();
    const reason    = document.getElementById("rejReason").value.trim();

    if (!firstName || !lastName || !reason) {
        alert("Please fill in first name, last name and reason.");
        return;
    }
    if (!selectedTaskId) return;

    fetch(`/api/tasks/${selectedTaskId}/reject`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            firstName: firstName,
            lastName:  lastName,
            reason:    reason
        })
    })
        .then(res => {
            if (res.ok) {
                closeRejectForm();
                location.reload();
            } else {
                alert("Failed to reject the task.");
            }
        })
        .catch(err => {
            console.error("Error rejecting task:", err);
            alert("An error occurred while rejecting the task.");
        });
}


// Block ALL Outside-Clicks while the reject popup is visible
document.addEventListener("click", function (e) {
    const overlay   = document.getElementById("rejectOverlay");
    if (!overlay || overlay.classList.contains("hidden")) return; // not open

    const container = document.getElementById("rejectContainer");
    if (container && !container.contains(e.target)) {
        // User clicked outside -> ignore completely
        e.stopPropagation();
        e.preventDefault();
    }
}, true);



// Search functionality
function debounce(func, wait) {
    let timeout;
    return function (...args) {
        clearTimeout(timeout);
        timeout = setTimeout(() => func.apply(this, args), wait);
    };
}

async function handleLiveSearch() {
    const query = document.getElementById('search-input').value.trim();
    const suggestionsContainer = document.getElementById('search-suggestions-container');
    
    if (!query || query.length < 2) {
        suggestionsContainer.innerHTML = '';
        suggestionsContainer.style.display = 'none';
        return;
    }
    
    try {
        suggestionsContainer.innerHTML = '<div class="search-loading">Suche...</div>';
        suggestionsContainer.style.display = 'block';
        
        const response = await fetch(`/api/tasks/search?q=${encodeURIComponent(query)}&status=open`);
        const tasks = response.ok ? await response.json() : [];
        
        let resultsHTML = '';
        
        if (tasks.length > 0) {
            resultsHTML += `
                <div class="search-section">
                    <h4>Offene Tasks</h4>
                    <ul>
                        ${tasks.slice(0, 5).map(task => `
                            <li><a href="#" onclick="openTaskFromSearch(${task.id}); return false;">
                                <i class="fas fa-search"></i> ${task.title}
                            </a></li>
                        `).join('')}
                    </ul>
                </div>`;
        }
        
        if (tasks.length === 0) {
            resultsHTML = '<div class="search-no-results">Keine Ergebnisse gefunden</div>';
        }
        
        suggestionsContainer.innerHTML = resultsHTML;
        suggestionsContainer.style.display = 'block';
    } catch (err) {
        console.error('Live-Suche Fehler:', err);
        suggestionsContainer.innerHTML = '<div class="search-error">Fehler bei der Suche</div>';
    }
}

function handleSearch(e) {
    e.preventDefault();
    const query = document.getElementById('search-input').value.trim();
    if (query) {
        searchAndDisplayTasks(query);
    }
}

async function searchAndDisplayTasks(query) {
    try {
        const response = await fetch(`/api/tasks/search?q=${encodeURIComponent(query)}&status=open`);
        const tasks = response.ok ? await response.json() : [];
        
        const container = document.getElementById("task-container");
        if (!container) return;
        
        container.innerHTML = "";
        
        tasks.forEach(task => {
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
    } catch (error) {
        console.error("Could not search tasks:", error);
        const container = document.getElementById("task-container");
        if (container) container.innerHTML = "<p style='text-align: center; color: #E60100;'>Could not search tasks. Please try again later.</p>";
    }
}

function openTaskFromSearch(taskId) {
    fetch(`/api/tasks/${taskId}`)
        .then(res => res.json())
        .then(task => {
            openPopup(task);
            const suggestionsContainer = document.getElementById('search-suggestions-container');
            suggestionsContainer.innerHTML = '';
            suggestionsContainer.style.display = 'none';
        })
        .catch(err => console.error('Error loading task:', err));
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
    
    // Search event listeners
    const searchForm = document.getElementById('search-form');
    const searchInput = document.getElementById('search-input');
    if (searchForm) searchForm.addEventListener('submit', handleSearch);
    if (searchInput) {
        searchInput.addEventListener('input', debounce(handleLiveSearch, 300));
    }
    
    // Close search suggestions when clicking outside
    document.addEventListener('click', (e) => {
        const container = document.getElementById('search-suggestions-container');
        if (container && !container.contains(e.target) && e.target !== searchInput) {
            container.innerHTML = '';
            container.style.display = 'none';
        }
    });
});


function preventOutsideClicksClosingPopups() {
    document.addEventListener('click', function(e) {
        const mainPopup     = document.getElementById('popup');
        const acceptOverlay = document.getElementById('acceptOverlay');
        const rejectOverlay = document.getElementById('rejectOverlay');

        // Prüfen, ob irgendein Popup / Overlay gerade offen ist
        const isMainOpen   = mainPopup   && !mainPopup.classList.contains('hidden');
        const isAcceptOpen = acceptOverlay && acceptOverlay.style.display === 'flex';
        const isRejectOpen = rejectOverlay && rejectOverlay.style.display === 'flex';

        if (isMainOpen || isAcceptOpen || isRejectOpen) {
            // Wenn der Klick NICHT in einen der Popup‐Container geht, dann abfangen
            const insideMain   = !!e.target.closest('.popup-content');
            const insideAccept = !!e.target.closest('#acceptContainer');
            const insideReject = !!e.target.closest('#rejectContainer');

            if (!insideMain && !insideAccept && !insideReject) {
                e.stopPropagation();
                e.preventDefault();
            }
        }
    }, true);
}


// Accept‐Overlay
const acceptOverlay   = document.getElementById('acceptOverlay');
const acceptContainer = document.getElementById('acceptContainer');
if (acceptOverlay)   acceptOverlay.addEventListener('click', e => e.stopPropagation());
if (acceptContainer) acceptContainer.addEventListener('click', e => e.stopPropagation());

