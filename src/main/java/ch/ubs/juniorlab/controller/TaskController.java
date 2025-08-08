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
                    System.err.println("Rejection email sent");
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

    @Transactional
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
            }
        }

        return ResponseEntity.ok().build();
    }

    @Transactional
    @PostMapping("/{taskId}/comments")
    public ResponseEntity<Void> addCommentToTask(@PathVariable Long taskId, @RequestBody CommentRequest commentRequest) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskId));
        Comment newComment = new Comment();
        newComment.setTask(task);
        newComment.setContent(commentRequest.getText());
        newComment.setTitle("User Comment");
        commentRepository.save(newComment);

        Person client = task.getClient(); // <- Wird korrekt geladen durch offene Session
        if (client != null && client.getEmail() != null && !client.getEmail().isBlank()) {
            try {
                sendCommentEmail(task, client, commentRequest);
            } catch (Exception e) {
                System.err.println("Failed to send comment email to " + client.getEmail() + ": " + e.getMessage());
            }
        }

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
        File pdf = pdfService.checkPDF(id);
        InputStreamResource resource = new InputStreamResource(new FileInputStream(pdf));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + pdf.getName())
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdf.length())
                .body(resource);
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

    private void sendTaskAcceptedMail(Task task, Person client, Person apprentice) {
        String taskUrl = hashService.getInfoUrl(task.getId());

        String subject = "Your Task \"" + task.getTitle() + "\" has been accepted!";

        String html = """
                <!doctype html>
                <html lang="en">
                  <head>
                    <meta charset="UTF-8">
                    <meta name="x-apple-disable-message-reformatting">
                    <meta name="viewport" content="width=device-width, initial-scale=1">
                    <title>Task angenommen</title>
                  </head>
                  <body style="margin:0;padding:0;background:#f6f8fa;">
                    <center style="width:100%%;background:#f6f8fa;">
                      <table role="presentation" cellpadding="0" cellspacing="0" border="0" width="100%%" style="background:#f6f8fa;">
                        <tr>
                          <td align="center" style="padding:24px 12px;">
                            <table role="presentation" cellpadding="0" cellspacing="0" border="0" width="640" style="width:640px;max-width:100%%;background:#ffffff;border:1px solid #e1e3e1;border-radius:12px;overflow:hidden;font-family:Arial,Helvetica,sans-serif;">
                              <tr>
                                <td style="background:#f9f9f9;color:#000000;padding:18px 24px;">
                                  <div style="font-size:18px;font-weight:700;line-height:1.2;">Task angenommen</div>
                                  <div style="height:6px;width:60px;background:#E60100;border-radius:3px;margin-top:10px;"></div>
                                </td>
                              </tr>
                
                              <tr>
                                <td style="padding:24px;">
                                  <table role="presentation" cellpadding="0" cellspacing="0" border="0" width="100%%" style="border-collapse:separate;border-spacing:0 10px;">
                                    <tr>
                                      <td style="width:140px;vertical-align:top;color:#666;font-size:12px;font-weight:700;padding:0;">Kunde</td>
                                      <td style="vertical-align:top;color:#000000;font-size:15px;padding:0;">%s %s</td>
                                    </tr>
                                    <tr>
                                      <td style="width:140px;vertical-align:top;color:#666;font-size:12px;font-weight:700;padding:0;">Task</td>
                                      <td style="vertical-align:top;color:#000000;font-size:15px;padding:0;">%s</td>
                                    </tr>
                                    <tr>
                                      <td style="width:140px;vertical-align:top;color:#666;font-size:12px;font-weight:700;padding:0;">Angenommen von</td>
                                      <td style="vertical-align:top;color:#000000;font-size:15px;padding:0;">%s %s</td>
                                    </tr>
                                    <tr>
                                      <td style="width:140px;vertical-align:top;color:#666;font-size:12px;font-weight:700;padding:0;">GPN</td>
                                      <td style="vertical-align:top;color:#000000;font-size:15px;padding:0;">%s</td>
                                    </tr>
                                    <tr>
                                      <td style="width:140px;vertical-align:top;color:#666;font-size:12px;font-weight:700;padding:0;">Link</td>
                                      <td style="vertical-align:top;color:#000000;font-size:15px;padding:0;"><a href="%s" style="color:#E60100;">Task öffnen</a></td>
                                    </tr>
                                  </table>
                                </td>
                              </tr>
                
                              <tr>
                                <td style="background:#f9f9f9;color:#000000;font-size:12px;padding:14px 24px;border-top:1px solid #eaecef;">
                                  Diese E-Mail wurde automatisch vom Junior Talent Lab gesendet.
                                </td>
                              </tr>
                            </table>
                          </td>
                        </tr>
                      </table>
                    </center>
                  </body>
                </html>
                """.formatted(
                safe(nz(client.getPrename())),
                safe(nz(client.getName())),
                safe(nz(task.getTitle())),
                safe(nz(apprentice.getPrename())),
                safe(nz(apprentice.getName())),
                safe(nz(apprentice.getGpn())),
                safe(taskUrl)
        );

        mailService.sendEmailHtml(client.getEmail(), subject, html);

    }

    private void sendRejectedReasonMail(Task task, Person client, RejectTaskDto rtd) {
        String taskUrl = hashService.getInfoUrl(task.getId());
        String subject = "Your Task \"" + nz(task.getTitle()) + "\" has been rejected";

        String tpl = """
                <!doctype html>
                <html lang="en">
                  <head>
                    <meta charset="UTF-8">
                    <meta name="x-apple-disable-message-reformatting">
                    <meta name="viewport" content="width=device-width, initial-scale=1">
                    <title>Task Rejected</title>
                  </head>
                  <body style="margin:0;padding:0;background:#f6f8fa;">
                    <center style="width:100%;background:#f6f8fa;">
                      <table role="presentation" cellpadding="0" cellspacing="0" border="0" width="100%%" style="background:#f6f8fa;">
                        <tr>
                          <td align="center" style="padding:24px 12px;">
                            <table role="presentation" cellpadding="0" cellspacing="0" border="0" width="640" style="width:640px;max-width:100%%;background:#ffffff;border:1px solid #e1e3e1;border-radius:12px;overflow:hidden;font-family:Arial,Helvetica,sans-serif;">
                              <tr>
                                <td style="background:#f9f9f9;color:#000000;padding:18px 24px;">
                                  <div style="font-size:18px;font-weight:700;line-height:1.2;">Your Task Has Been Rejected</div>
                                  <div style="height:6px;width:60px;background:#E60100;border-radius:3px;margin-top:10px;"></div>
                                </td>
                              </tr>
                              <tr>
                                <td style="padding:24px;">
                                  <table role="presentation" cellpadding="0" cellspacing="0" border="0" width="100%%" style="border-collapse:separate;border-spacing:0 10px;">
                                    <tr><td style="width:140px;color:#666;font-size:12px;font-weight:700;">Task Title</td><td style="color:#000;font-size:15px;">%s</td></tr>
                                    <tr><td style="width:140px;color:#666;font-size:12px;font-weight:700;">Rejected By</td><td style="color:#000;font-size:15px;">%s %s</td></tr>
                                    <tr><td style="width:140px;color:#666;font-size:12px;font-weight:700;">Reason</td><td style="color:#000;font-size:15px;">%s</td></tr>
                                    <tr><td style="width:140px;color:#666;font-size:12px;font-weight:700;">View Task</td><td style="color:#000;font-size:15px;"><a href="%s" style="color:#E60100;">Click Here</a></td></tr>
                                  </table>
                                </td>
                              </tr>
                              <tr>
                                <td style="background:#f9f9f9;color:#000000;font-size:12px;padding:14px 24px;border-top:1px solid #eaecef;">
                                  This email was sent automatically by Junior Talent Lab.
                                </td>
                              </tr>
                            </table>
                          </td>
                        </tr>
                      </table>
                    </center>
                  </body>
                </html>
                """;

        String html = tpl
                .replace("${TITLE}", safe(nz(task.getTitle())))
                .replace("${FIRST}", safe(nz(rtd.getFirstName())))
                .replace("${LAST}", safe(nz(rtd.getLastName())))
                .replace("${REASON}", safe(nz(rtd.getReason())))
                .replace("${URL}", safe(taskUrl));

        mailService.sendEmailHtml(client.getEmail(), subject, html);
    }

    private void sendTaskFinishedEmail(Task task, Person client) {
        String taskUrl = hashService.getInfoUrl(task.getId());
        String subject = "Your Task \"" + task.getTitle() + "\" is now finished!";

        String html = """
                <!doctype html>
                    <html lang="en">
                      <head>
                        <meta charset="UTF-8">
                        <meta name="x-apple-disable-message-reformatting">
                        <meta name="viewport" content="width=device-width, initial-scale=1">
                        <title>Task Finished</title>
                      </head>
                      <body style="margin:0;padding:0;background:#f6f8fa;">
                        <center style="width:100%%;background:#f6f8fa;">
                          <table role="presentation" cellpadding="0" cellspacing="0" border="0" width="100%%" style="background:#f6f8fa;">
                            <tr>
                              <td align="center" style="padding:24px 12px;">
                                <table role="presentation" cellpadding="0" cellspacing="0" border="0" width="640" style="width:640px;max-width:100%%;background:#ffffff;border:1px solid #e1e3e1;border-radius:12px;overflow:hidden;font-family:Arial,Helvetica,sans-serif;">
                                  <tr>
                                    <td style="background:#f9f9f9;color:#000000;padding:18px 24px;">
                                      <div style="font-size:18px;font-weight:700;line-height:1.2;">Your Task Has Been Marked As Finished</div>
                                      <div style="height:6px;width:60px;background:#E60100;border-radius:3px;margin-top:10px;"></div>
                                    </td>
                                  </tr>
                                  <tr>
                                    <td style="padding:24px;">
                                      <table role="presentation" cellpadding="0" cellspacing="0" border="0" width="100%%" style="border-collapse:separate;border-spacing:0 10px;">
                                        <tr><td style="width:140px;color:#666;font-size:12px;font-weight:700;">Task Title</td><td style="color:#000;font-size:15px;">%s</td></tr>
                                        <tr><td style="width:140px;color:#666;font-size:12px;font-weight:700;">View Task</td><td style="color:#000;font-size:15px;"><a href="%s" style="color:#E60100;">Click Here</a></td></tr>
                                      </table>
                                    </td>
                                  </tr>
                                  <tr>
                                    <td style="background:#f9f9f9;color:#000000;font-size:12px;padding:14px 24px;border-top:1px solid #eaecef;">
                                      This email was sent automatically by Junior Talent Lab.
                                    </td>
                                  </tr>
                                </table>
                              </td>
                            </tr>
                          </table>
                        </center>
                      </body>
                    </html>
                """.formatted(task.getTitle(), taskUrl);

        mailService.sendEmailHtml(client.getEmail(), subject, html);
    }

    private void sendTaskStatusChangedMail(Task task, Person client) {
        String taskUrl = hashService.getInfoUrl(task.getId());
        String subject = "Status Update for Your Task \"" + task.getTitle() + "\"";

        String html = """
                <!doctype html>
                <html lang="en">
                  <head>
                    <meta charset="UTF-8">
                    <meta name="x-apple-disable-message-reformatting">
                    <meta name="viewport" content="width=device-width, initial-scale=1">
                    <title>Status Update</title>
                  </head>
                  <body style="margin:0;padding:0;background:#f6f8fa;">
                    <center style="width:100%%;background:#f6f8fa;">
                      <table role="presentation" cellpadding="0" cellspacing="0" border="0" width="100%%" style="background:#f6f8fa;">
                        <tr>
                          <td align="center" style="padding:24px 12px;">
                            <table role="presentation" cellpadding="0" cellspacing="0" border="0" width="640" style="width:640px;max-width:100%%;background:#ffffff;border:1px solid #e1e3e1;border-radius:12px;overflow:hidden;font-family:Arial,Helvetica,sans-serif;">
                              <tr>
                                <td style="background:#f9f9f9;color:#000000;padding:18px 24px;">
                                  <div style="font-size:18px;font-weight:700;line-height:1.2;">Task Status Updated</div>
                                  <div style="height:6px;width:60px;background:#E60100;border-radius:3px;margin-top:10px;"></div>
                                </td>
                              </tr>
                              <tr>
                                <td style="padding:24px;">
                                  <table role="presentation" cellpadding="0" cellspacing="0" border="0" width="100%%" style="border-collapse:separate;border-spacing:0 10px;">
                                    <tr><td style="width:140px;color:#666;font-size:12px;font-weight:700;">Task Title</td><td style="color:#000;font-size:15px;">%s</td></tr>
                                    <tr><td style="width:140px;color:#666;font-size:12px;font-weight:700;">New Status</td><td style="color:#000;font-size:15px;">%s</td></tr>
                                    <tr><td style="width:140px;color:#666;font-size:12px;font-weight:700;">View Task</td><td style="color:#000;font-size:15px;"><a href="%s" style="color:#E60100;">Click Here</a></td></tr>
                                  </table>
                                </td>
                              </tr>
                              <tr>
                                <td style="background:#f9f9f9;color:#000000;font-size:12px;padding:14px 24px;border-top:1px solid #eaecef;">
                                  This email was sent automatically by Junior Talent Lab.
                                </td>
                              </tr>
                            </table>
                          </td>
                        </tr>
                      </table>
                    </center>
                  </body>
                </html>
                """.formatted(task.getTitle(), task.getProgress(), taskUrl);

        mailService.sendEmailHtml(client.getEmail(), subject, html);
    }

    private void sendCommentEmail(Task task, Person client, CommentRequest comment) {
        String taskUrl = hashService.getInfoUrl(task.getId());
        String subject = "Your Task \"" + task.getTitle() + "\" Received a Comment";

        String html = """
                <!doctype html>
                <html lang="en">
                  <head>
                    <meta charset="UTF-8">
                    <meta name="x-apple-disable-message-reformatting">
                    <meta name="viewport" content="width=device-width, initial-scale=1">
                    <title>New Comment</title>
                  </head>
                  <body style="margin:0;padding:0;background:#f6f8fa;">
                    <center style="width:100%%;background:#f6f8fa;">
                      <table role="presentation" cellpadding="0" cellspacing="0" border="0" width="100%%" style="background:#f6f8fa;">
                        <tr>
                          <td align="center" style="padding:24px 12px;">
                            <table role="presentation" cellpadding="0" cellspacing="0" border="0" width="640" style="width:640px;max-width:100%%;background:#ffffff;border:1px solid #e1e3e1;border-radius:12px;overflow:hidden;font-family:Arial,Helvetica,sans-serif;">
                              <tr>
                                <td style="background:#f9f9f9;color:#000000;padding:18px 24px;">
                                  <div style="font-size:18px;font-weight:700;line-height:1.2;">New Comment on Your Task</div>
                                  <div style="height:6px;width:60px;background:#E60100;border-radius:3px;margin-top:10px;"></div>
                                </td>
                              </tr>
                              <tr>
                                <td style="padding:24px;">
                                  <table role="presentation" cellpadding="0" cellspacing="0" border="0" width="100%%" style="border-collapse:separate;border-spacing:0 10px;">
                                    <tr><td style="width:140px;color:#666;font-size:12px;font-weight:700;">Task Title</td><td style="color:#000;font-size:15px;">%s</td></tr>
                                    <tr><td style="width:140px;color:#666;font-size:12px;font-weight:700;">Comment</td><td style="color:#000;font-size:15px;">%s</td></tr>
                                    <tr><td style="width:140px;color:#666;font-size:12px;font-weight:700;">View Task</td><td style="color:#000;font-size:15px;"><a href="%s" style="color:#E60100;">Click Here</a></td></tr>
                                  </table>
                                </td>
                              </tr>
                              <tr>
                                <td style="background:#f9f9f9;color:#000000;font-size:12px;padding:14px 24px;border-top:1px solid #eaecef;">
                                  This email was sent automatically by Junior Talent Lab.
                                </td>
                              </tr>
                            </table>
                          </td>
                        </tr>
                      </table>
                    </center>
                  </body>
                </html>
                """.formatted(task.getTitle(), comment.getText(), taskUrl);

        mailService.sendEmailHtml(client.getEmail(), subject, html);
    }

    private String nz(String s) {
        return s == null ? "" : s;
    }

    private String safe(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

}
