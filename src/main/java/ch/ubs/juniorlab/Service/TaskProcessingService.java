package ch.ubs.juniorlab.Service;

import ch.ubs.juniorlab.entity.*;
import ch.ubs.juniorlab.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskProcessingService {

    private final TaskRepository taskRepository;

    // Konstruktor-Injektion des Repositories
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
                System.out.println("Verteilungsmethode: " + poll.getDistributionMethod());

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
                System.out.println("Unbekannter Task-Typ: " + task.getClass().getSimpleName());
            }
        }
    }

}
