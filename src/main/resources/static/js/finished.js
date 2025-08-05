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

            container.innerHTML = "";

            if (data.length === 0) {
                container.innerHTML = `
                    <div style="text-align: center; padding: 60px 20px; color: #666;">
                        <div style="font-size: 48px; margin-bottom: 20px;">✅</div>
                        <h3 style="color: #333; margin-bottom: 10px;">No tasks available</h3>
                        <p style="font-size: 16px;">Check back later for completed tasks!</p>
                    </div>
                `;
                return;
            }

            data.forEach(task => {
                const card = document.createElement("div");
                card.className = "task-card";

                const clientName = task.client ?
                    `${task.client.prename || ''} ${task.client.name || ''}`.trim() :
                    "Unknown";
                const clientGpn = task.client?.gpn || "-";
                const deadlineText = task.deadline || "-";
                const progress = task.progress || "Finished";

                card.innerHTML = `
                  <h2 style="font-size: 1.5rem; font-weight: bold; color: #000000;">${task.title}</h2>
                  <div style="display: flex; gap: 1rem;">
                    <div style="flex: 0 0 250px; display: flex; flex-direction: column; gap: 0.5rem;">
                      <div class="popup-row" style="background-color: #f5f5f5; padding: 0.5rem;"><span>Client Name</span><span>${clientName}</span></div>
                      <div class="popup-row" style="background-color: #f5f5f5; padding: 0.5rem;"><span>GPN</span><span>${clientGpn}</span></div>
                      <div class="popup-row" style="background-color: #f5f5f5; padding: 0.5rem;"><span>Deadline</span><span>${deadlineText}</span></div>
                      <div class="popup-row" style="background-color: #f5f5f5; padding: 0.5rem;"><span>Status</span><span style="color: #28a745; font-weight: bold;">${progress}</span></div>
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
            console.error("Could not fetch finished tasks:", error);
            const container = document.getElementById("task-container");
            if (container) container.innerHTML = "<p style='text-align: center; color: #E60100;'>Could not load tasks. Please try again later.</p>";
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

    // Set the current status in the dropdown
    const statusSelect = document.getElementById("popup-status");
    if (statusSelect && task.progress) {
        statusSelect.value = task.progress;
    }

    // Load comments
    loadComments(task.id);

    document.getElementById("popup").classList.remove("hidden");
    loadPopupContent(task);
}

function closePopup() {
    document.getElementById("popup").classList.add("hidden");
    selectedTaskId = null;
}

function updateTaskStatus() {
    if (!selectedTaskId) return;

    const newStatus = document.getElementById("popup-status").value;

    fetch(`/api/tasks/${selectedTaskId}/status`, {
        method: "POST",
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            status: newStatus
        })
    })
    .then(res => {
        if (res.ok) {
            console.log("Status updated successfully");
            // Reload the current page of tasks
            loadFinishedTasks(pagination);
        } else {
            alert("Failed to update status. Please try again.");
        }
    })
    .catch(error => {
        console.error('Error updating status:', error);
        alert('An error occurred while updating the status.');
    });
}

function submitComment() {
    if (!selectedTaskId) return;

    const commentText = document.getElementById("popup-comment").value.trim();
    if (!commentText) {
        alert("Please enter a comment before submitting.");
        return;
    }

    fetch(`/api/tasks/${selectedTaskId}/comments`, {
        method: "POST",
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            text: commentText
        })
    })
    .then(res => {
        if (res.ok) {
            document.getElementById("popup-comment").value = "";
            loadComments(selectedTaskId);
        } else {
            alert("Failed to submit comment. Please try again.");
        }
    })
    .catch(error => {
        console.error('Error submitting comment:', error);
        alert('An error occurred while submitting the comment.');
    });
}

function loadComments(taskId) {
    fetch(`/api/tasks/${taskId}/comments`)
        .then(res => res.json())
        .then(comments => {
            const commentList = document.getElementById("comment-list");
            if (!commentList) return;

            commentList.innerHTML = "";
            comments.forEach(comment => {
                const commentDiv = document.createElement("div");
                commentDiv.className = "comment";
                commentDiv.innerHTML = `
                    <div class="comment-author">${comment.author}</div>
                    <div class="comment-text">${comment.text}</div>
                `;
                commentList.appendChild(commentDiv);
            });
        })
        .catch(error => {
            console.error('Error loading comments:', error);
        });
}

// Helper functions
function getTaskType(task) {
    // Check Poll first (most specific)
    if (task.questionCount != null || task.questionType != null) return "Poll";
    
    // Check Video (has lengthSec)
    if (task.lengthSec != null) return "Video";
    
    // Check Flyer (has paperSize AND paperType)
    if (task.paperSize != null && task.paperType != null) return "Flyer";
    
    // Check Poster (has posterSize)
    if (task.posterSize != null) return "Poster";
    
    // Check Slideshow (has photoCount)
    if (task.photoCount != null) return "Slideshow";
    
    // Check Photo (has format and resolution, but no other specific fields)
    if (task.format != null && task.resolution != null && 
        task.lengthSec == null && task.photoCount == null && task.posterSize == null) {
        return "Photo";
    }
    
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

    // Flyer-specific requirements
    if (task.paperSize) requirements.push(`Size: ${task.paperSize}`);
    if (task.paperType) requirements.push(`Paper: ${task.paperType}`);

    // Video-specific requirements
    if (task.lengthSec) requirements.push(`Length: ${task.lengthSec}s`);
    if (task.voiceover != null) requirements.push(`Voiceover: ${task.voiceover ? 'Yes' : 'No'}`);
    if (task.disclaimer != null) requirements.push(`Disclaimer: ${task.disclaimer ? 'Yes' : 'No'}`);
    if (task.brandingRequirements) requirements.push(`Branding: ${task.brandingRequirements}`);
    if (task.musicStyle) requirements.push(`Music Style: ${task.musicStyle}`);

    // Photo-specific requirements
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
        
        const response = await fetch(`/api/tasks/search?q=${encodeURIComponent(query)}&status=finished`);
        const tasks = response.ok ? await response.json() : [];
        
        let resultsHTML = '';
        
        if (tasks.length > 0) {
            resultsHTML += `
                <div class="search-section">
                    <h4>Finished Tasks</h4>
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
        const response = await fetch(`/api/tasks/search?q=${encodeURIComponent(query)}&status=finished`);
        const tasks = response.ok ? await response.json() : [];
        
        const container = document.getElementById("task-container");
        if (!container) return;
        
        container.innerHTML = "";
        
        if (tasks.length === 0) {
            container.innerHTML = `
                <div style="text-align: center; padding: 60px 20px; color: #666;">
                    <div style="font-size: 48px; margin-bottom: 20px;">🔍</div>
                    <h3 style="color: #333; margin-bottom: 10px;">No results found</h3>
                    <p style="font-size: 16px;">Try a different search term</p>
                </div>
            `;
            return;
        }
        
        tasks.forEach(task => {
            const card = document.createElement("div");
            card.className = "task-card";
            
            const clientName = task.client ?
                `${task.client.prename || ''} ${task.client.name || ''}`.trim() :
                "Unknown";
            const clientGpn = task.client?.gpn || "-";
            const deadlineText = task.deadline || "-";
            const progress = task.progress || "Finished";
            
            card.innerHTML = `
              <h2 style="font-size: 1.5rem; font-weight: bold; color: #000000;">${task.title}</h2>
              <div style="display: flex; gap: 1rem;">
                <div style="flex: 0 0 250px; display: flex; flex-direction: column; gap: 0.5rem;">
                  <div class="popup-row" style="background-color: #f5f5f5; padding: 0.5rem;"><span>Client Name</span><span>${clientName}</span></div>
                  <div class="popup-row" style="background-color: #f5f5f5; padding: 0.5rem;"><span>GPN</span><span>${clientGpn}</span></div>
                  <div class="popup-row" style="background-color: #f5f5f5; padding: 0.5rem;"><span>Deadline</span><span>${deadlineText}</span></div>
                  <div class="popup-row" style="background-color: #f5f5f5; padding: 0.5rem;"><span>Status</span><span style="color: #28a745; font-weight: bold;">${progress}</span></div>
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