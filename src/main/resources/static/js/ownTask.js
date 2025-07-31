
    document.addEventListener("DOMContentLoaded", function () {
    const deleteButton = document.getElementById("delete-button");
    const taskId = document.getElementById("taskId")?.value;

    if (deleteButton && taskId) {
    deleteButton.addEventListener("click", function (e) {
    e.preventDefault();

    const confirmed = confirm("Are you sure you want to delete this task?");
    if (!confirmed) return;

    fetch(`/api/tasks/${taskId}`, {
    method: "DELETE",
    headers: {
    "Content-Type": "application/json"
}
})
    .then(res => {
    if (res.ok) {
    alert("Task deleted successfully.");
    window.location.href = "/overview"; // oder wo du hin willst
} else {
    return res.text().then(text => { throw new Error(text); });
}
})
    .catch(error => {
    console.error("Delete failed:", error);
    alert("An error occurred while deleting the task.");
});
});
}
});
