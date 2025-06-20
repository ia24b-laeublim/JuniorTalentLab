package ch.ubs.juniorlab.controller;

import ch.ubs.juniorlab.entity.Comment;
import ch.ubs.juniorlab.entity.Task;
import ch.ubs.juniorlab.repository.CommentRepository;
import ch.ubs.juniorlab.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private CommentRepository commentRepository;

    // Offene Tasks (unverändert)
    @GetMapping("/open")
    public List<Task> getOpenTasks() {
        return taskRepository.findAll().stream()
                .filter(task -> task.getStatus() == null || "REJECTED".equalsIgnoreCase(task.getStatus()))
                .toList();
    }

    // Akzeptierte, aber NICHT erledigte Tasks anzeigen (unverändert)
    @GetMapping("/accepted")
    public List<Task> getAcceptedTasks() {
        return taskRepository.findAll().stream()
                .filter(task -> "ACCEPTED".equalsIgnoreCase(task.getStatus()) && !"Finished".equalsIgnoreCase(task.getProgress()))
                .toList();
    }

    // Erledigte Tasks anzeigen (unverändert)
    @GetMapping("/finished")
    public List<Task> getFinishedTasks() {
        return taskRepository.findAll().stream()
                .filter(task -> "Finished".equalsIgnoreCase(task.getProgress()))
                .toList();
    }

    // Task akzeptieren (unverändert)
    @PostMapping("/{id}/accept")
    public ResponseEntity<Void> acceptTask(@PathVariable Long id) {
        Task task = taskRepository.findById(id).orElseThrow();
        task.setStatus("ACCEPTED");
        if (task.getProgress() == null) {
            task.setProgress("Started");
        }
        taskRepository.save(task);
        return ResponseEntity.ok().build();
    }

    // Task ablehnen (unverändert)
    @PostMapping("/{id}/reject")
    public ResponseEntity<Void> rejectTask(@PathVariable Long id) {
        Task task = taskRepository.findById(id).orElseThrow();
        task.setStatus("REJECTED");
        taskRepository.save(task);
        return ResponseEntity.ok().build();
    }

    // Task-Status aktualisieren (unverändert)
    @PostMapping("/{id}/status")
    public ResponseEntity<Void> updateTaskStatus(@PathVariable Long id, @RequestBody StatusUpdateRequest request) {
        Task task = taskRepository.findById(id).orElseThrow();
        task.setProgress(request.getStatus());
        taskRepository.save(task);
        return ResponseEntity.ok().build();
    }

    // Methode zum Hinzufügen eines Kommentars (unverändert, funktioniert)
    @PostMapping("/{taskId}/comments")
    public ResponseEntity<Void> addCommentToTask(@PathVariable Long taskId, @RequestBody CommentRequest commentRequest) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskId));
        Comment newComment = new Comment();
        newComment.setTask(task);
        newComment.setContent(commentRequest.getText());
        newComment.setTitle("User Comment");
        commentRepository.save(newComment);
        return ResponseEntity.ok().build();
    }

    // ⭐ KORRIGIERTE METHODE: Gibt jetzt eine Liste von sicheren DTOs zurück
    @GetMapping("/{taskId}/comments")
    public ResponseEntity<List<CommentDto>> getCommentsForTask(@PathVariable Long taskId) {
        // Überprüfen, ob der Task existiert
        if (!taskRepository.existsById(taskId)) {
            return ResponseEntity.notFound().build();
        }

        // Finde alle Kommentare für diesen Task
        List<Comment> comments = commentRepository.findByTaskId(taskId);

        // Wandle die Liste von Comment-Entitäten in eine Liste von CommentDto-Objekten um
        List<CommentDto> commentDtos = comments.stream().map(comment -> {
            // Platzhalter, da der Autor noch nicht gespeichert wird
            // Später wird hier der richtige Name stehen
            String authorName = "User";

            // Erstelle ein DTO mit dem Inhalt und dem Autorennamen
            return new CommentDto(comment.getContent(), authorName);
        }).collect(Collectors.toList());

        // Sende die sichere DTO-Liste an das Frontend
        return ResponseEntity.ok(commentDtos);
    }
}