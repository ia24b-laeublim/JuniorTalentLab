package ch.ubs.juniorlab.service;

import ch.ubs.juniorlab.entity.*;
import ch.ubs.juniorlab.repository.CommentRepository;
import ch.ubs.juniorlab.repository.TaskRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskProcessingService {


    @Autowired
    private MailService mailService;

    @Autowired
    private final TaskRepository taskRepository;

    @Autowired
    private HashService hashService;



    public TaskProcessingService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }
    public void printAllTasks() {
        List<Task> tasks = getAllTasks();
        for (Task task : tasks) {
            System.out.println("=== Task ===");
            System.out.println("Titel: " + task.getTitle());
            System.out.println("Beschreibung: " + task.getDescription());

            if (task instanceof FlyerTask flyer) {
                System.out.println("FlyerTask");
                System.out.println("Papiergrösse: " + flyer.getPaperSize());
                System.out.println("Papiertyp: " + flyer.getPaperType());

            } else if (task instanceof PosterTask poster) {
                System.out.println("PosterTask");
                System.out.println("Format: " + poster.getFormat());
                System.out.println("Postergrösse: " + poster.getPosterSize());
                System.out.println("Papiertyp: " + poster.getPaperType());
                System.out.println("Druckqualität (DPI): " + poster.getPrintQualityDpi());
                System.out.println("Montageart: " + poster.getMountingType());

            } else if (task instanceof PhotoTask photo) {
                System.out.println("PhotoTask");
                System.out.println("Format: " + photo.getFormat());
                System.out.println("Dateiformat: " + photo.getFileFormat());
                System.out.println("Plattformen: " + photo.getSocialMediaPlatforms());
                System.out.println("Auflösung: " + photo.getResolution());

            } else if (task instanceof PollTask poll) {
                System.out.println("PollTask");
                System.out.println("Umfragetitel: " + poll.getTitle());
                System.out.println("Fragenanzahl: " + poll.getQuestionCount());
                System.out.println("Fragetyp: " + poll.getQuestionType());
                System.out.println("Anonym: " + poll.getAnonymous());

            } else if (task instanceof SlideshowTask slideshow) {
                System.out.println("SlideshowTask");
                System.out.println("Format: " + slideshow.getFormat());
                System.out.println("Dateiformat: " + slideshow.getFileFormat());
                System.out.println("Plattformen: " + slideshow.getSocialMediaPlatforms());
                System.out.println("Fotoanzahl: " + slideshow.getPhotoCount());
                System.out.println("Auflösung: " + slideshow.getResolution());

            } else if (task instanceof VideoTask video) {
                System.out.println("VideoTask");
                System.out.println("Länge (Sekunden): " + video.getLengthSec());
                System.out.println("Voiceover: " + video.getVoiceover());
                System.out.println("Disclaimer: " + video.getDisclaimer());
                System.out.println("Branding: " + video.getBrandingRequirements());
                System.out.println("Format: " + video.getFormat());
                System.out.println("Dateiformat: " + video.getFileFormat());
                System.out.println("Plattformen: " + video.getSocialMediaPlatforms());
                System.out.println("Auflösung: " + video.getResolution());
                System.out.println("Musikstil: " + video.getMusicStyle());

            } else {
                System.out.println("GeneralTask (Other)");
                System.out.println("Typ: " + task.getTaskType());
            }
        }
    }

    @Autowired
    private CommentRepository commentRepository;

    public void deleteTasksPastDeadline(int daysAfterDeadline) {
        LocalDate cutoff = LocalDate.now().minusDays(daysAfterDeadline);
        commentRepository.deleteByTaskDeadlineBefore(cutoff);     // 1. Erst Comments löschen!
        taskRepository.deleteTasksWithDeadlineOlderThan(cutoff);  // 2. Dann Tasks löschen!
    }

    // Wird ausgelöst wenn man das Programm laufen lässt, zum testen -> löschen der überfälligen Tasks
    @PostConstruct
    public void testDeleteTasksPastDeadline() {
        deleteTasksPastDeadline(7);
        System.out.println("Alte Tasks wurden testweise gelöscht!");
    }

    // Wird beim Starten des Programmes ausgelöst, zum testen -> Mail Reminder Deadline
    // @PostConstruct - Entfernt da LazyInitializationException bei @PostConstruct auftritt
    @Transactional
    public void testMailReminder() {
        sendDeadlineReminderMails();
        System.out.println("Reminder-Testlauf wurde ausgeführt!");
    }


    @Transactional
    public void sendDeadlineReminderMails() {
        LocalDate today = LocalDate.now();
        List<Task> tasksExpiringToday = taskRepository.findByDeadline(today);

        for (Task task : tasksExpiringToday) {
            Person client = task.getClient();
            if (client == null || client.getEmail() == null) continue;

            String subject = "Reminder: Your Task \"" + task.getTitle() + "\" expires today";
            String message = String.format(
                    """
                    Hello %s,
                    
                    This is a reminder that your task "%s" expires today (%s).

                    If you wish to edit, delete, or review your task, here is the corresponding link:
                    %s

                    Thank you for using Junior Talent Lab!

                    Best regards,
                    Junior Talent Lab Team
                    """,
                    client.getPrename(),
                    task.getTitle(),
                    today,
                    hashService.getInfoUrl(task.getId())
            );
            mailService.sendEmail(client.getEmail(), subject, message);
        }
    }


}
