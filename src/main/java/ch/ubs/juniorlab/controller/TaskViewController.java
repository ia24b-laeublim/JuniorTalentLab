package ch.ubs.juniorlab.controller;

import ch.ubs.juniorlab.entity.*;
import ch.ubs.juniorlab.repository.*;
import ch.ubs.juniorlab.service.HashService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/tasks")
public class TaskViewController {

    @Autowired private TaskRepository taskRepository;
    @Autowired private FlyerTaskRepository flyerTaskRepository;
    @Autowired private PhotoTaskRepository photoTaskRepository;
    @Autowired private PollTaskRepository pollTaskRepository;
    @Autowired private PosterTaskRepository posterTaskRepository;
    @Autowired private SlideshowTaskRepository slideshowTaskRepository;
    @Autowired private VideoTaskRepository videoTaskRepository;
    @Autowired private HashService hashService;

    @GetMapping("/view/{id}")
    public String viewTask(@PathVariable("id") Long id, Model model) {
        Optional<Task> task = taskRepository.findById(id);
        if (task.isEmpty()) {
            return "error/404";
        }
        model.addAttribute("task", task.get());
        return "task/view";
    }

    @GetMapping("/edit/{id}")
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
                flyerTaskRepository.findById(id).ifPresent(flyerTask -> model.addAttribute("specificTask", flyerTask));
                return "update/updateFlyer";
            case "photo":
                photoTaskRepository.findById(id).ifPresent(photoTask -> model.addAttribute("specificTask", photoTask));
                return "update/updatePhoto";
            case "poll":
                pollTaskRepository.findById(id).ifPresent(pollTask -> model.addAttribute("specificTask", pollTask));
                return "update/updatePoll";
            case "poster":
                posterTaskRepository.findById(id).ifPresent(posterTask -> model.addAttribute("specificTask", posterTask));
                return "update/updatePoster";
            case "slideshow":
                slideshowTaskRepository.findById(id).ifPresent(slideshowTask -> model.addAttribute("specificTask", slideshowTask));
                return "update/updateSlideshow";
            case "video":
                videoTaskRepository.findById(id).ifPresent(videoTask -> model.addAttribute("specificTask", videoTask));
                return "update/updateVideo";
            default:
                return "update/updateOther";
        }
    }

    @PostMapping("/update/{id}")
    public String updateTask(
            @PathVariable("id") Long id,
            @ModelAttribute Task updatedTask,
            @ModelAttribute FlyerTask updatedFlyerTask,
            @ModelAttribute PhotoTask updatedPhotoTask,
            @ModelAttribute PollTask updatedPollTask,
            @ModelAttribute PosterTask updatedPosterTask,
            @ModelAttribute SlideshowTask updatedSlideshowTask,
            @ModelAttribute VideoTask updatedVideoTask,
            RedirectAttributes redirectAttributes) {

        Optional<Task> existingTaskOptional = taskRepository.findById(id);
        if (existingTaskOptional.isEmpty()) {
            return "error/404";
        }

        Task originalTask = existingTaskOptional.get();
        String taskType = originalTask.getTaskType().toLowerCase();

        // Allgemeine Felder aktualisieren
        originalTask.setTitle(updatedTask.getTitle());
        originalTask.setDescription(updatedTask.getDescription());
        originalTask.setTargetAudience(updatedTask.getTargetAudience());
        originalTask.setBudgetChf(updatedTask.getBudgetChf());
        originalTask.setDeadline(updatedTask.getDeadline());
        originalTask.setHandoverMethod(updatedTask.getHandoverMethod());
        originalTask.setSpecificRequirements(updatedTask.getSpecificRequirements());
        originalTask.setChannel(updatedTask.getChannel());
        taskRepository.save(originalTask); // Speichern der allgemeinen Task-Updates

        // Spezifische Task-Felder aktualisieren
        switch (taskType) {
            case "flyer":
                flyerTaskRepository.findById(id).ifPresent(flyerTask -> {
                    flyerTask.setPaperSize(updatedFlyerTask.getPaperSize());
                    flyerTask.setPaperType(updatedFlyerTask.getPaperType());
                    flyerTaskRepository.save(flyerTask);
                });
                break;
            case "photo":
                photoTaskRepository.findById(id).ifPresent(photoTask -> {
                    photoTask.setFormat(updatedPhotoTask.getFormat());
                    photoTask.setFileFormat(updatedPhotoTask.getFileFormat());
                    photoTask.setResolution(updatedPhotoTask.getResolution());
                    photoTask.setSocialMediaPlatforms(updatedPhotoTask.getSocialMediaPlatforms());
                    photoTaskRepository.save(photoTask);
                });
                break;
            case "poll":
                pollTaskRepository.findById(id).ifPresent(pollTask -> {
                    pollTask.setQuestionCount(updatedPollTask.getQuestionCount());
                    pollTask.setQuestionType(updatedPollTask.getQuestionType());
                    pollTask.setStartDate(updatedPollTask.getStartDate());
                    pollTask.setEndDate(updatedPollTask.getEndDate());
                    pollTask.setAnonymous(updatedPollTask.getAnonymous());
                    pollTaskRepository.save(pollTask);
                });
                break;
            case "poster":
                posterTaskRepository.findById(id).ifPresent(posterTask -> {
                    posterTask.setFormat(updatedPosterTask.getFormat());
                    posterTask.setPosterSize(updatedPosterTask.getPosterSize());
                    posterTask.setPaperType(updatedPosterTask.getPaperType());
                    posterTask.setPrintQualityDpi(updatedPosterTask.getPrintQualityDpi());
                    posterTask.setMountingType(updatedPosterTask.getMountingType());
                    posterTaskRepository.save(posterTask);
                });
                break;
            case "slideshow":
                slideshowTaskRepository.findById(id).ifPresent(slideshowTask -> {
                    slideshowTask.setFormat(updatedSlideshowTask.getFormat());
                    slideshowTask.setFileFormat(updatedSlideshowTask.getFileFormat());
                    slideshowTask.setPhotoCount(updatedSlideshowTask.getPhotoCount());
                    slideshowTask.setResolution(updatedSlideshowTask.getResolution());
                    slideshowTask.setSocialMediaPlatforms(updatedSlideshowTask.getSocialMediaPlatforms());
                    slideshowTaskRepository.save(slideshowTask);
                });
                break;
            case "video":
                videoTaskRepository.findById(id).ifPresent(videoTask -> {
                    videoTask.setLengthSec(updatedVideoTask.getLengthSec());
                    videoTask.setVoiceover(updatedVideoTask.getVoiceover());
                    videoTask.setDisclaimer(updatedVideoTask.getDisclaimer());
                    videoTask.setBrandingRequirements(updatedVideoTask.getBrandingRequirements());
                    videoTask.setFormat(updatedVideoTask.getFormat());
                    videoTask.setFileFormat(updatedVideoTask.getFileFormat());
                    videoTask.setResolution(updatedVideoTask.getResolution());
                    videoTask.setSocialMediaPlatforms(updatedVideoTask.getSocialMediaPlatforms());
                    videoTask.setMusicStyle(updatedVideoTask.getMusicStyle());
                    videoTaskRepository.save(videoTask);
                });
                break;
            default:
                break;
        }

        redirectAttributes.addFlashAttribute("message", "Task erfolgreich aktualisiert!");
        return "redirect:" + hashService.getInfoUrl(id);
    }
}