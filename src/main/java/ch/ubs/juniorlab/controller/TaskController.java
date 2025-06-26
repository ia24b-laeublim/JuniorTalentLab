package ch.ubs.juniorlab.controller;

import ch.ubs.juniorlab.entity.Comment;
import ch.ubs.juniorlab.entity.Person;
import ch.ubs.juniorlab.entity.Task;
import ch.ubs.juniorlab.repository.CommentRepository;
import ch.ubs.juniorlab.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import ch.ubs.juniorlab.service.PDFService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskRepository taskRepository;

    private final CommentRepository commentRepository;

    private final PDFService pdfService;

    public TaskController(TaskRepository taskRepository, CommentRepository commentRepository, PDFService pdfService) {
        this.taskRepository = taskRepository;
        this.commentRepository = commentRepository;
        this.pdfService = pdfService;
    }

    @GetMapping("/open")
    public List<Task> getOpenTasks() {
        List<Task> allTasks = taskRepository.findAllWithClients();


        return allTasks.stream()
                .filter(task -> {
                    String status = task.getStatus();
                    boolean isOpen = status == null ||
                            "open".equalsIgnoreCase(status) ||
                            "REJECTED".equalsIgnoreCase(status);
                    return isOpen;
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/accepted")
    public List<Task> getAcceptedTasks() {
        return taskRepository.findAllWithClients().stream()
                .filter(task -> {
                    String status = task.getStatus();
                    String progress = task.getProgress();
                    return "ACCEPTED".equalsIgnoreCase(status) &&
                            !"Finished".equalsIgnoreCase(progress);
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/finished")
    public List<Task> getFinishedTasks() {
        return taskRepository.findAllWithClients().stream()
                .filter(task -> "Finished".equalsIgnoreCase(task.getProgress()))
                .collect(Collectors.toList());
    }

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

    @PostMapping("/{id}/reject")
    public ResponseEntity<Void> rejectTask(@PathVariable Long id) {
        Task task = taskRepository.findById(id).orElseThrow();
        task.setStatus("REJECTED");
        taskRepository.save(task);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/status")
    public ResponseEntity<Void> updateTaskStatus(@PathVariable Long id, @RequestBody StatusUpdateRequest request) {
        Task task = taskRepository.findById(id).orElseThrow();
        task.setProgress(request.getStatus());
        taskRepository.save(task);
        return ResponseEntity.ok().build();
    }

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

    @GetMapping("/{taskId}/comments")
    public ResponseEntity<List<CommentDto>> getCommentsForTask(@PathVariable Long taskId) {
        if (!taskRepository.existsById(taskId)) {
            return ResponseEntity.notFound().build();
        }

        List<Comment> comments = commentRepository.findByTaskId(taskId);
        List<CommentDto> commentDtos = comments.stream().map(comment -> {
            String authorName = "User";
            return new CommentDto(comment.getContent(), authorName);
        }).collect(Collectors.toList());

        return ResponseEntity.ok(commentDtos);
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<Resource> downloadTaskPdf(@PathVariable Long id) throws IOException {
        Task task = taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found"));
        File pdf = pdfService.checkPDF(task);

        InputStreamResource resource = new InputStreamResource(new FileInputStream(pdf));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + pdf.getName())
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdf.length())
                .body(resource);
    }
}