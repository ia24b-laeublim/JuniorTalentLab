let selectedTaskId = null;
let statusChanged = false;
let pagination = 1;
let maxPages = 1;
let allTasks = [];
let isSearchMode = false;
let currentSearchResults = [];
let searchPagination = 1;
let searchMaxPages = 1;
const RESULTS_PER_PAGE = 10;

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
    
    // Clear suggestions if query is empty
    if (!query || query.length < 2) {
        suggestionsContainer.innerHTML = '';
        suggestionsContainer.style.display = 'none';
        return;
    }
    
    try {
        // Show loading indicator
        suggestionsContainer.innerHTML = '<div class="search-loading">Searching...</div>';
        suggestionsContainer.style.display = 'block';
        
        // Check if tasks are loaded
        if (allTasks.length === 0) {
            suggestionsContainer.innerHTML = '<div class="search-error">Tasks are loading, please wait...</div>';
            return;
        }
        
        // Filter tasks by title and description (case insensitive)
        const filteredTasks = allTasks.filter(task => {
            const titleMatch = task.title && task.title.toLowerCase().includes(query.toLowerCase());
            const descMatch = task.description && task.description.toLowerCase().includes(query.toLowerCase());
            const clientMatch = task.client && (
                (task.client.prename && task.client.prename.toLowerCase().includes(query.toLowerCase())) ||
                (task.client.name && task.client.name.toLowerCase().includes(query.toLowerCase()))
            );
            return titleMatch || descMatch || clientMatch;
        });
        
        // Format the results
        let resultsHTML = '';
        
        // Add tasks results
        if (filteredTasks.length > 0) {
            resultsHTML += `
                <div class="search-section">
                    <h4>Tasks</h4>
                    <ul>
                        ${filteredTasks.slice(0, 5).map(task => `
                            <li>
                                <a href="#" onclick="openTaskFromSearch(${task.id}); return false;">
                                    <i class="fas fa-tasks"></i> ${task.title}
                                    ${task.client ? `<br><small style="margin-left: 25px; color: #666;">${task.client.prename || ''} ${task.client.name || ''}</small>` : ''}
                                </a>
                            </li>
                        `).join('')}
                    </ul>
                </div>
            `;
        }
        
        // Show "no results" if nothing found
        if (filteredTasks.length === 0) {
            resultsHTML = '<div class="search-no-results">No results found</div>';
        }
        
        // Add "view all results" link at the bottom
        if (filteredTasks.length > 0) {
            resultsHTML += `
                <div class="search-view-all">
                    <a href="#" onclick="searchAndDisplayTasks('${query}'); return false;">
                        View all results (${filteredTasks.length})
                    </a>
                </div>
            `;
        }
        
        // Update the suggestions container
        suggestionsContainer.innerHTML = resultsHTML;
        suggestionsContainer.style.display = 'block';
        
    } catch (error) {
        console.error('Error during live search:', error);
        suggestionsContainer.innerHTML = '<div class="search-error">Search error occurred</div>';
    }
}

function handleSearch(e) {
    e.preventDefault();
    const query = document.getElementById('search-input').value.trim();
    if (query) {
        searchAndDisplayTasks(query);
    }
}

function searchAndDisplayTasks(query) {
    const container = document.getElementById("task-container");
    if (!container) return;
    
    // Check if tasks are loaded
    if (allTasks.length === 0) {
        container.innerHTML = "<p style='text-align: center; color: #E60100;'>Tasks are loading, please wait...</p>";
        return;
    }
    
    // Filter tasks only by title when pressing Enter
    const filteredTasks = allTasks.filter(task => {
        return task.title && task.title.toLowerCase().includes(query.toLowerCase());
    });
    
    // Set search mode and store results
    isSearchMode = true;
    currentSearchResults = filteredTasks;
    searchPagination = 1;
    searchMaxPages = Math.ceil(filteredTasks.length / RESULTS_PER_PAGE);
    
    displaySearchResults(query);
    updateSearchPagination();
    
    // Hide search suggestions when showing full results
    const suggestionsContainer = document.getElementById('search-suggestions-container');
    if (suggestionsContainer) {
        suggestionsContainer.innerHTML = '';
        suggestionsContainer.style.display = 'none';
    }
}

function displaySearchResults(query) {
    const container = document.getElementById("task-container");
    if (!container) return;
    
    container.innerHTML = "";
    
    if (currentSearchResults.length > 0) {
        // Add search results header
        const header = document.createElement("div");
        header.style.cssText = "text-align: center; margin-bottom: 20px; color: #666; font-size: 16px;";
        header.textContent = `Search results for "${query}" (${currentSearchResults.length} found)`;
        container.appendChild(header);
        
        // Calculate pagination
        const startIndex = (searchPagination - 1) * RESULTS_PER_PAGE;
        const endIndex = startIndex + RESULTS_PER_PAGE;
        const tasksToShow = currentSearchResults.slice(startIndex, endIndex);
        
        // Display filtered tasks for current page
        tasksToShow.forEach(task => {
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
    } else {
        const noResults = document.createElement("div");
        noResults.style.cssText = "text-align: center; margin-top: 50px; color: #666; font-size: 16px;";
        noResults.textContent = `No results found for "${query}"`;
        container.appendChild(noResults);
    }
}

function updateSearchPagination() {
    const paginationContainer = document.querySelector('.pagination');
    if (!paginationContainer) return;
    
    // Hide pagination if 10 or fewer results
    if (currentSearchResults.length <= 10) {
        paginationContainer.style.display = 'none';
        return;
    }
    
    // Show pagination for more than 10 results
    paginationContainer.style.display = 'flex';
    
    // Update pagination buttons
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
    
    const prevNum = searchPagination - 1;
    const nextNum = searchPagination + 1;
    
    if (prevNum >= 1) {
        enableButton(prevNumBtn, prevNum.toString());
        enableButton(prevBtn, "«");
    } else {
        disableButton(prevNumBtn);
        disableButton(prevBtn);
    }
    
    if (nextNum <= searchMaxPages) {
        enableButton(nextNumBtn, nextNum.toString());
        enableButton(nextBtn, "»");
    } else {
        disableButton(nextNumBtn);
        disableButton(nextBtn);
    }
    
    if (currentBtn) currentBtn.textContent = searchPagination;
    if (input) input.value = searchPagination;
}

function updateSearchPaginationPage(newPage) {
    searchPagination = Math.max(1, Math.min(newPage, searchMaxPages));
    const query = document.getElementById('search-input').value.trim();
    displaySearchResults(query);
    updateSearchPagination();
}

function openTaskFromSearch(taskId) {
    console.log('Opening task from search with ID:', taskId);
    // Find task in locally stored tasks
    const task = allTasks.find(t => t.id === taskId);
    if (task) {
        console.log('Found task, opening popup:', task.title);
        openPopup(task);
        // Clear and hide search suggestions
        const suggestionsContainer = document.getElementById('search-suggestions-container');
        if (suggestionsContainer) {
            suggestionsContainer.innerHTML = '';
            suggestionsContainer.style.display = 'none';
        }
        // Clear search input
        const searchInput = document.getElementById('search-input');
        if (searchInput) {
            searchInput.value = '';
        }
    } else {
        console.error('Task not found in local tasks:', taskId);
        console.log('Available task IDs:', allTasks.map(t => t.id));
        // Try to reload tasks if not found
        if (allTasks.length === 0) {
            console.log('No tasks loaded, trying to reload...');
            loadAllTasks();
        }
    }
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

/** Loads all tasks for search functionality using Promise.all */
async function loadAllTasks() {
    try {
        // Create array of promises for all pages
        const pagePromises = [];
        for (let page = 1; page <= maxPages; page++) {
            pagePromises.push(
                fetch(`/api/tasks/accepted?page=${page}`)
                    .then(response => response.ok ? response.json() : [])
                    .catch(() => [])
            );
        }
        
        // Wait for all pages to load
        const allPages = await Promise.all(pagePromises);
        
        // Flatten and filter valid data
        allTasks = allPages
            .filter(data => Array.isArray(data))
            .flat();
            
        console.log(`Loaded ${allTasks.length} tasks for search from ${maxPages} pages`);
    } catch (err) {
        console.error("Error loading all tasks for search:", err);
        // Fallback: try to load current page only
        try {
            const response = await fetch(`/api/tasks/accepted?page=${pagination}`);
            const data = await response.json();
            allTasks = Array.isArray(data) ? data : [];
        } catch (fallbackErr) {
            console.error("Fallback also failed:", fallbackErr);
            allTasks = [];
        }
    }
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
        if (isSearchMode) {
            updateSearchPaginationPage(newPage);
            return;
        }
        
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
            loadAllTasks(); // Load all tasks for search functionality
        })
        .catch(err => {
            console.error("Error fetching page count:", err);
            maxPages = 1;
            updatePagination(1);
            loadAllTasks(); // Load all tasks for search functionality
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
    
    // Clear search when input is empty
    if (searchInput) {
        searchInput.addEventListener('input', (e) => {
            if (e.target.value.trim() === '' && isSearchMode) {
                // Exit search mode and return to normal pagination
                isSearchMode = false;
                currentSearchResults = [];
                const paginationContainer = document.querySelector('.pagination');
                if (paginationContainer) {
                    paginationContainer.style.display = 'flex';
                }
                updatePagination(pagination);
            }
        });
    }
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
