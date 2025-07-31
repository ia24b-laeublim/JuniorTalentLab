package ch.ubs.juniorlab.controller;

import ch.ubs.juniorlab.entity.*;
import ch.ubs.juniorlab.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@Controller
public class TaskViewController {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private FlyerTaskRepository flyerTaskRepository;

    @Autowired
    private PhotoTaskRepository photoTaskRepository;

    @Autowired
    private PollTaskRepository pollTaskRepository;

    @Autowired
    private PosterTaskRepository posterTaskRepository;

    @Autowired
    private SlideshowTaskRepository slideshowTaskRepository;

    @Autowired
    private VideoTaskRepository videoTaskRepository;

    @GetMapping("/tasks/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Optional<Task> taskOptional = taskRepository.findById(id);

        if (taskOptional.isEmpty()) {
            return "error/404";
        }

        Task task = taskOptional.get();
        model.addAttribute("task", task);

        String taskType = task.getTaskType().toLowerCase();

        switch (taskType) {
            case "flyer":
                Optional<FlyerTask> flyerTaskOptional = flyerTaskRepository.findById(id);
                flyerTaskOptional.ifPresent(flyerTask -> model.addAttribute("specificTask", flyerTask));
                return "update/updateFlyer";

            case "photo":
                Optional<PhotoTask> photoTaskOptional = photoTaskRepository.findById(id);
                photoTaskOptional.ifPresent(photoTask -> model.addAttribute("specificTask", photoTask));
                return "update/updatePhoto";

            case "poll":
                Optional<PollTask> pollTaskOptional = pollTaskRepository.findById(id);
                pollTaskOptional.ifPresent(pollTask -> model.addAttribute("specificTask", pollTask));
                return "update/updatePoll";

            case "poster":
                Optional<PosterTask> posterTaskOptional = posterTaskRepository.findById(id);
                posterTaskOptional.ifPresent(posterTask -> model.addAttribute("specificTask", posterTask));
                return "update/updatePoster";

            case "slideshow":
                Optional<SlideshowTask> slideshowTaskOptional = slideshowTaskRepository.findById(id);
                slideshowTaskOptional.ifPresent(slideshowTask -> model.addAttribute("specificTask", slideshowTask));
                return "update/updateSlideshow";

            case "video":
                Optional<VideoTask> videoTaskOptional = videoTaskRepository.findById(id);
                videoTaskOptional.ifPresent(videoTask -> model.addAttribute("specificTask", videoTask));
                return "update/updateVideo";

            default:
                // Wenn ein unbekannter Task-Typ auftritt, gib eine 404-Seite zurück.
                // Es ist besser, einen Fehler zu melden als eine nicht existierende Seite zu suchen.
                return "error/404";
        }
    }
}