package ch.ubs.juniorlab.controller;

import ch.ubs.juniorlab.entity.Comment;
import ch.ubs.juniorlab.entity.Person;

import ch.ubs.juniorlab.dto.*;
import ch.ubs.juniorlab.entity.Task;
import ch.ubs.juniorlab.repository.CommentRepository;
import ch.ubs.juniorlab.repository.PersonRepository;
import ch.ubs.juniorlab.repository.TaskRepository;

import ch.ubs.juniorlab.service.HashService;
import ch.ubs.juniorlab.service.MailService;
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

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.System.out;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final Integer paginationSize = 5;

    @Autowired
    private final TaskRepository taskRepository;
    private final PersonRepository personRepository;
    private final CommentRepository commentRepository;
    private final PDFService pdfService;

    @Autowired
    private final MailService mailService;

    @Autowired
    private final HashService hashService;

    public TaskController(TaskRepository taskRepository, PersonRepository personRepository, CommentRepository commentRepository, PDFService pdfService, MailService mailService, HashService hashService) {
        this.taskRepository = taskRepository;
        this.personRepository = personRepository;
        this.commentRepository = commentRepository;
        this.pdfService = pdfService;
        this.mailService = mailService;
        this.hashService = hashService;
    }

    private <T> List<T> paginate(List<T> list, int page, int pageSize) {
        int fromIndex = (page - 1) * pageSize;
        if (fromIndex >= list.size()) {
            return List.of();
        }
        int toIndex = Math.min(fromIndex + pageSize, list.size());
        return list.subList(fromIndex, toIndex);
    }

    @GetMapping("/open/pageAmount")
    public int getOpenPageAmount() {
        List<Task> allTasks = taskRepository.findAllWithClients();

        long openCount = allTasks.stream()
                .filter(task -> {
                    String status = task.getStatus();
                    return status == null
                            || "open".equalsIgnoreCase(status)
                            || "REJECTED".equalsIgnoreCase(status);
                })
                .count();

        return (int) Math.ceil((double) openCount / paginationSize);
    }

    @GetMapping("/accepted/pageAmount")
    public int getAcceptedPageAmount() {
        List<Task> allTasks = taskRepository.findAllWithClients();

        long acceptedCount = allTasks.stream()
                .filter(task -> {
                    String status = task.getStatus();
                    String progress = task.getProgress();
                    return "ACCEPTED".equalsIgnoreCase(status)
                            && !"Finished".equalsIgnoreCase(progress);
                })
                .count();

        return (int) Math.ceil((double) acceptedCount / paginationSize);
    }


    @GetMapping("/finished/pageAmount")
    public int getFinishedPageAmount() {
        List<Task> allTasks = taskRepository.findAllWithClients();

        long finishedCount = allTasks.stream()
                .filter(task -> "Finished".equalsIgnoreCase(task.getProgress()))
                .count();

        return (int) Math.ceil((double) finishedCount / paginationSize);
    }




    @GetMapping("/open")
    public List<TaskWithAttachmentDto> getOpenTasks(@RequestParam(defaultValue = "1") int page) {
        List<Task> allTasks = taskRepository.findAllWithClients();

        List<TaskWithAttachmentDto> allDtos = allTasks.stream()
                .filter(task -> {
                    String status = task.getStatus();
                    boolean isOpen = status == null ||
                            "open".equalsIgnoreCase(status) ||
                            "REJECTED".equalsIgnoreCase(status);
                    return isOpen;
                })
                .sorted(Comparator.comparing(
                        Task::getDeadline,
                        Comparator.nullsLast(Comparator.naturalOrder())
                ))
                .map(TaskWithAttachmentDto::new)
                .collect(Collectors.toList());

        return paginate(allDtos, page, paginationSize);
    }

    @GetMapping("/accepted")
    public List<TaskWithAttachmentDto> getAcceptedTasks(@RequestParam(defaultValue = "1") int page) {
        List<TaskWithAttachmentDto> allDtos = taskRepository.findAllWithClients().stream()
                .filter(task -> {
                    String status = task.getStatus();
                    String progress = task.getProgress();
                    return "ACCEPTED".equalsIgnoreCase(status) &&
                            !"Finished".equalsIgnoreCase(progress);
                })
                .sorted(Comparator.comparing(
                        Task::getDeadline,
                        Comparator.nullsLast(Comparator.naturalOrder())
                ))
                .map(TaskWithAttachmentDto::new)
                .collect(Collectors.toList());

        return paginate(allDtos, page, paginationSize);
    }

    @GetMapping("/finished")
    public List<TaskWithAttachmentDto> getFinishedTasks(@RequestParam(defaultValue = "1") int page) {
        List<TaskWithAttachmentDto> allDtos = taskRepository.findAllWithClients().stream()
                .filter(task -> "Finished".equalsIgnoreCase(task.getProgress()))
                .sorted(Comparator.comparing(
                        Task::getDeadline,
                        Comparator.nullsLast(Comparator.naturalOrder())
                ))
                .map(TaskWithAttachmentDto::new)
                .collect(Collectors.toList());

        return paginate(allDtos, page, paginationSize);
    }


    @PostMapping("/{id}/accept")
    public ResponseEntity<Void> acceptTask(@PathVariable Long id, @RequestBody AcceptTaskRequest request) {
        try {
            Task task = taskRepository.findById(id).orElse(null);
            
            if (task == null) {
                return ResponseEntity.notFound().build();
            }

            // Find or create a person based on first name, last name, and GPN
            String gpnString = String.valueOf(request.getGpn());
            Person apprentice = personRepository.findByGpn(gpnString)
                    .orElseGet(() -> {
                        Person newPerson = new Person();
                        newPerson.setPrename(request.getFirstName());
                        newPerson.setName(request.getLastName());
                        newPerson.setGpn(gpnString);
                        newPerson.setEmail(request.getFirstName().toLowerCase() + "." + request.getLastName().toLowerCase() + "@example.com");
                        return personRepository.save(newPerson);
                    });

            task.setStatus("ACCEPTED");
            task.setApprentice(apprentice);
            if (task.getProgress() == null) {
                task.setProgress("Started");
            }
            taskRepository.save(task);

            // Try to send email, but don't fail if it doesn't work
            try {
                Person client = task.getClient();
                if (client != null && client.getEmail() != null && !client.getEmail().isBlank()) {
                    sendTaskAcceptedMail(task, client, apprentice);
                }
            } catch (Exception e) {
                System.err.println("Failed to send acceptance email: " + e.getMessage());
            }

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.err.println("Error accepting task: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<Void> rejectTask(@PathVariable Long id, @RequestBody RejectTaskDto request) {
        try {
            Task task = taskRepository.findById(id).orElse(null);
            
            if (task == null) {
                return ResponseEntity.notFound().build();
            }

            task.setStatus("REJECTED");
            taskRepository.save(task);

            // Try to send email, but don't fail if it doesn't work
            try {
                Person client = task.getClient();
                if (client != null && client.getEmail() != null && !client.getEmail().isBlank()) {
                    sendRejectedReasonMail(task, client, request);
                }
            } catch (Exception e) {
                System.err.println("Failed to send rejection email: " + e.getMessage());
            }

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.err.println("Error rejecting task: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    private void sendRejectedReasonMail(Task task, Person client, RejectTaskDto rtd) {
        String taskUrl = hashService.getInfoUrl(task.getId());

        String subject = "Your Task \"" + task.getTitle() + "\" has been rejected";
        String message = String.format(
                """
                Hello %s,
        
                Your Task "%s" has been rejected by %s %s because: "%s".
                
                If you wish to edit, delete, or review your task, here is the corresponding link:
                %s
        
                Thank you for using Junior Talent Lab!
        
                Best regards,
                Junior Talent Lab Team
                """,
                client.getPrename(),
                task.getTitle(),
                rtd.getFirstName(),
                rtd.getLastName(),
                rtd.getReason(),
                taskUrl
        );

        mailService.sendEmail(client.getEmail(), subject, message);

        out.println("Task created with URL: " + taskUrl);
        System.out.println("Status‑Mail sent to: " + client.getEmail());
    }

    @PostMapping("/{id}/status")
    public ResponseEntity<Void> updateTaskStatus(@PathVariable Long id, @RequestBody StatusUpdateRequest request) {
        Task task = taskRepository.findById(id).orElseThrow();

        task.setProgress(request.getStatus());
        taskRepository.save(task);

        Person client = task.getClient();
        if (client != null && client.getEmail() != null && !client.getEmail().isBlank()) {
            try {
                if ("Finished".equalsIgnoreCase(task.getProgress())) {
                    sendTaskFinishedEmail(task, client);
                } else {
                    sendTaskStatusChangedMail(task, client);
                }
            } catch (Exception e) {
                System.err.println("Failed to send status update email to " + client.getEmail() + ": " + e.getMessage());
                // Continue without failing the entire operation
            }
        }

        return ResponseEntity.ok().build();
    }

    private void sendTaskStatusChangedMail(Task task, Person client) {
        String taskUrl = hashService.getInfoUrl(task.getId());

        String subject = "Status‑Update to your Task \"" + task.getTitle() + "\"";
        String message = String.format(
                """
                Hello %s,
        
                The status on your Task "%s" has just been updated to: %s
                
                If you wish to edit, delete, or review your task, here is the corresponding link:
                %s
        
                Thank you for using Junior Talent Lab!
        
                Best regards,
                Junior Talent Lab Team
                """,
                client.getPrename(),
                task.getTitle(),
                task.getProgress(),
                taskUrl
        );

        mailService.sendEmail(client.getEmail(), subject, message);

        out.println("Task created with URL: " + taskUrl);
        System.out.println("Status‑Mail sent to: " + client.getEmail());
    }

    private void sendTaskFinishedEmail(Task task, Person client) {
        String taskUrl = hashService.getInfoUrl(task.getId());

        String subject = "Your Task \"" + task.getTitle() + "\" is now finished!";
        String message = String.format(
                """
                Hello %s,
        
                Your Task "%s" has been marked as finished.
                
                If you wish to edit, delete, or review your task, here is the corresponding link:
                %s
        
                Thank you for using Junior Talent Lab!
        
                Best regards,
                Junior Talent Lab Team
                """,
                client.getPrename(),
                task.getTitle(),
                taskUrl
        );

        mailService.sendEmail(client.getEmail(), subject, message);

        out.println("Task created with URL: " + taskUrl);
        System.out.println("Finished‑Mail sent to: " + client.getEmail());
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

        Person client = task.getClient();
        if (client != null && client.getEmail() != null && !client.getEmail().isBlank()) {
            try {
                sendCommentEmail(task, client, commentRequest);
            } catch (Exception e) {
                System.err.println("Failed to send comment email to " + client.getEmail() + ": " + e.getMessage());
                // Continue without failing the entire operation
            }
        }

        return ResponseEntity.ok().build();
    }

    private void sendCommentEmail(Task task, Person client, CommentRequest comment) {
        String taskUrl = hashService.getInfoUrl(task.getId());

        String subject = "Your Task \"" + task.getTitle() + "\" was commented";
        String message = String.format(
                """
                Hello %s,
        
                Your Task "%s" has been commented.
                
                Comment: "%s"
                
                If you wish to edit, delete, or review your task, here is the corresponding link:
                %s
        
                Thank you for using Junior Talent Lab!
        
                Best regards,
                Junior Talent Lab Team
                """,
                client.getPrename(),
                task.getTitle(),
                comment.getText(),
                taskUrl
        );

        mailService.sendEmail(client.getEmail(), subject, message);

        out.println("Task created with URL: " + taskUrl);
        System.out.println("Finished‑Mail sent to: " + client.getEmail());
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
        File pdf = pdfService.checkPDF(id);
        InputStreamResource resource = new InputStreamResource(new FileInputStream(pdf));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + pdf.getName())
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdf.length())
                .body(resource);
    }

    private void sendTaskAcceptedMail(Task task, Person client, Person apprentice) {
        String taskUrl = hashService.getInfoUrl(task.getId());

        String subject = "Your Task \"" + task.getTitle() + "\" has been accepted!";
        String message = String.format(
                """
                Hello %s,
                
                Your Task "%s" has been accepted by %s %s (%s).
                
                If you wish to edit, delete, or review your task, here is the corresponding link:
                %s
                
                Thank you for using Junior Talent Lab!
                
                Best regards,
                Junior Talent Lab Team
                """,
                client.getPrename(),
                task.getTitle(),
                apprentice.getPrename(),
                apprentice.getName(),
                apprentice.getGpn(),
                taskUrl
        );

        mailService.sendEmail(client.getEmail(), subject, message);

        out.println("Task created with URL: " + taskUrl);
        System.out.println("Acceptance‑Mail sent to: " + client.getEmail());
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        try {
            commentRepository.deleteByTaskId(id);  // benötigt aktive Transaktion
            taskRepository.deleteById(id);         // Task selbst löschen
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

}
