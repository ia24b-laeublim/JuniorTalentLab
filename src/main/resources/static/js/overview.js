let selectedTaskId = null;
let statusChanged = false;
let pagination = 1;
let maxPages = 1;

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
        
        const response = await fetch(`/api/tasks/search?q=${encodeURIComponent(query)}&status=accepted`);
        const tasks = response.ok ? await response.json() : [];
        
        let resultsHTML = '';
        
        if (tasks.length > 0) {
            resultsHTML += `
                <div class="search-section">
                    <h4>Angenommene Tasks</h4>
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
        const response = await fetch(`/api/tasks/search?q=${encodeURIComponent(query)}&status=accepted`);
        const tasks = response.ok ? await response.json() : [];
        
        const container = document.getElementById("task-container");
        if (!container) return;
        
        container.innerHTML = "";
        
        tasks.forEach(task => {
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

/** Updates the task status via API */
function updateTaskStatus() {
    const statusDropdown = document.getElementById("popup-status");
    if (!statusDropdown) {
        console.warn("No status dropdown found!");
        return;
    }

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
        })
        .catch(error => {
            console.error('Error updating status:', error);
            alert('An error occurred while updating the status.');
        });
}

/** Opens the popup for a task */
function openPopup(task) {
    selectedTaskId = task.id;
    statusChanged = false;

    if (!document.getElementById("popup")) {
        console.warn("Popup element not found!");
        return;
    }

    const clientName = task.client
        ? `${task.client.prename || ''} ${task.client.name || ''}`.trim()
        : "-";

    const updateElement = (id, value) => {
        const el = document.getElementById(id);
        if (el) el.textContent = value ?? "-";
    };

    updateElement("popup-title", task.title);
    updateElement("popup-name", clientName);
    updateElement("popup-gpn", task.client?.gpn);
    updateElement("popup-deadline", task.deadline);
    updateElement("popup-channel", task.channel);
    updateElement("popup-type", getTaskType(task));
    updateElement("popup-format", getMaxFileSize(task));
    updateElement("popup-target", task.targetAudience);
    updateElement("popup-budget", task.budgetChf);
    updateElement("popup-handover", task.handoverMethod);
    updateElement("popup-description", task.description);
    updateElement("popup-other", getSpecificRequirements(task));

    const statusDropdown = document.getElementById("popup-status");
    if (statusDropdown) {
        const savedStatus = task.progress?.trim();
        if (savedStatus && [...statusDropdown.options].some(o => o.value === savedStatus)) {
            statusDropdown.value = savedStatus;
        } else {
            statusDropdown.value = "Started";
        }
    }

    // Attachment handling
    const attachmentSection = document.getElementById("attachment-section");
    const attachBtn = document.getElementById("popup-attachment-btn");
    const attachFilename = document.getElementById("attachment-filename");

    if (attachmentSection && attachBtn && attachFilename) {
        if (task.attachment && task.attachment.id) {
            attachBtn.href = `/api/files/download/${task.attachment.id}`;
            attachFilename.textContent = task.attachment.filename || 'Unknown file';
            attachmentSection.style.display = 'flex';
        } else {
            attachmentSection.style.display = 'none';
        }
    }

    loadComments(task.id);
    document.getElementById("popup").classList.remove("hidden");
}

/** Closes the popup */
function closePopup() {
    const popup = document.getElementById("popup");
    if (popup) {
        popup.classList.add("hidden");
    }
    selectedTaskId = null;

    if (statusChanged) {
        loadTasks(pagination);
    }
}

/** Submits a comment via API */
function submitComment() {
    const commentInput = document.getElementById("popup-comment");
    if (!commentInput) {
        console.warn("Comment input not found!");
        return;
    }

    const text = commentInput.value.trim();
    if (!text || !selectedTaskId) return;

    fetch(`/api/tasks/${selectedTaskId}/comments`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ text })
    })
        .then(res => {
            if (res.ok) {
                loadComments(selectedTaskId);
                commentInput.value = "";
            } else {
                alert("Failed to submit comment.");
            }
        });
}

/** Loads comments for a task */
function loadComments(taskId) {
    fetch(`/api/tasks/${taskId}/comments`)
        .then(res => res.ok ? res.json() : [])
        .then(comments => {
            const list = document.getElementById("comment-list");
            if (!list) {
                console.warn("comment-list not found!");
                return;
            }

            list.innerHTML = "";
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

/** Utility for getting task type */
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
    return task.maxFileSizeMb ? `${task.maxFileSizeMb}MB` : "-";
}

function getSpecificRequirements(task) {
    const req = [];
    if (task.paperSize) req.push(`Size: ${task.paperSize}`);
    if (task.paperType) req.push(`Paper: ${task.paperType}`);
    if (task.lengthSec) req.push(`Length: ${task.lengthSec}s`);
    if (task.voiceover != null) req.push(`Voiceover: ${task.voiceover ? 'Yes' : 'No'}`);
    if (task.disclaimer != null) req.push(`Disclaimer: ${task.disclaimer ? 'Yes' : 'No'}`);
    if (task.brandingRequirements) req.push(`Branding: ${task.brandingRequirements}`);
    if (task.musicStyle) req.push(`Music Style: ${task.musicStyle}`);
    if (task.format) req.push(`Format: ${task.format}`);
    if (task.fileFormat) req.push(`File Format: ${task.fileFormat}`);
    if (task.resolution) req.push(`Resolution: ${task.resolution}`);
    if (task.socialMediaPlatforms) req.push(`Platforms: ${task.socialMediaPlatforms}`);
    if (task.photoCount) req.push(`Photo Count: ${task.photoCount}`);
    if (task.posterSize) req.push(`Poster Size: ${task.posterSize}`);
    if (task.printQualityDpi) req.push(`DPI: ${task.printQualityDpi}`);
    if (task.mountingType) req.push(`Mounting: ${task.mountingType}`);
    if (task.questionCount) req.push(`Questions: ${task.questionCount}`);
    if (task.questionType) req.push(`Type: ${task.questionType}`);
    if (task.startDate) req.push(`Start: ${task.startDate}`);
    if (task.endDate) req.push(`End: ${task.endDate}`);
    if (task.anonymous != null) req.push(`Anonymous: ${task.anonymous ? 'Yes' : 'No'}`);
    if (task.distributionMethod) req.push(`Distribution: ${task.distributionMethod}`);
    return req.length > 0 ? req.join(", ") : "No specific requirements";
}

/** Loads tasks for a given page */
function loadTasks(page) {
    fetch(`/api/tasks/accepted?page=${page}`)
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

document.addEventListener("DOMContentLoaded", () => {
    const prevBtn = document.getElementById("prevPage");
    const nextBtn = document.getElementById("nextPage");
    const prevNumBtn = document.getElementById("prevNumberPage");
    const nextNumBtn = document.getElementById("nextNumberPage");
    const currentBtn = document.getElementById("currentPageBtn");
    const input = document.getElementById("pageInput");

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

    fetch("/api/tasks/accepted/pageAmount")
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

// Outside click closes popup
document.addEventListener("click", (event) => {
    const popup = document.getElementById("popup");
    if (!popup || popup.classList.contains("hidden")) return;
    if (event.target.closest(".task-card")) return;
    const content = popup.querySelector(".popup-content");
    if (content && !content.contains(event.target)) {
        closePopup();
    }
});

// Download PDF click
document.addEventListener("DOMContentLoaded", () => {
    const popup = document.getElementById("popup");
    if (popup) {
        const pdfBtn = popup.querySelector('[title="Download as PDF"]');
        if (pdfBtn) {
            pdfBtn.addEventListener("click", () => {
                if (!selectedTaskId) return;
                const link = document.createElement('a');
                link.href = `/api/tasks/${selectedTaskId}/pdf`;
                link.download = `Task_${selectedTaskId}.pdf`;
                document.body.appendChild(link);
                link.click();
                document.body.removeChild(link);
            });
        }
    }
});
