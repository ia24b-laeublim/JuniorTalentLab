package ch.ubs.juniorlab.controller;

import ch.ubs.juniorlab.repository.TaskRepository;
import ch.ubs.juniorlab.service.HashService;
import ch.ubs.juniorlab.service.MailService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ch.ubs.juniorlab.dto.*;

import java.util.Optional;

@Controller
public class HomeController {

    private final MailService mailService;
    private final TaskRepository taskRepository;
    private final HashService hashService;

    public HomeController(MailService mailService, TaskRepository taskRepository, HashService hashService) {
        this.mailService = mailService;
        this.taskRepository = taskRepository;
        this.hashService = hashService;
    }



    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("pageTitle", "Home - Junior Talent Lab");
        return "index";
    }

    @GetMapping("/imprint")
    public String imprint() {
        return "imprint"; // templates/imprint.html
    }

    @GetMapping("/contact")
    public String contact() {
        return "contact"; // templates/contact.html
    }

    @GetMapping("/privacy")
    public String privacyPolicy() {
        return "privacyPolicy"; // templates/privacyPolicy.html
    }

    @GetMapping("/createTaskPage")
    public String showCreateTaskPage() {
        return "createTaskPage"; // templates/createTaskPage.html
    }

    @GetMapping("/requestPage")
    public String showRequestPage() {
        return "requestPage"; // templates/requestPage.html
    }

    @GetMapping("/overviewPage")
    public String showOverviewPage() {
        return "overviewPage"; // templates/overviewPage.html
    }

    @GetMapping("/finishedTasksPage")
    public String showFinishedTasksPage() {
        return "finishedTasksPage"; // templates/finishedTasksPage.html
    }

    // Task Pages

    @GetMapping("/flyerTaskPage")
    public String showFlyerTaskPage() {
        return "/task/createFlyerTask"; // templates/task/createFlyerTask.html
    }

    @GetMapping("/pollTaskPage")
    public String showPollTaskPage() {
        return "/task/createPollTask"; // templates/task/createPollTask.html
    }

    // New task type endpoints
    @GetMapping("/videoTaskPage")
    public String showVideoTaskPage() {
        return "/task/createVideoTask"; // templates/task/createVideoTask.html
    }

    @GetMapping("/photoTaskPage")
    public String showPhotoTaskPage() {
        return "/task/createPhotoTask"; // templates/task/createPhotoTask.html
    }

    @GetMapping("/slideshowTaskPage")
    public String showSlideshowTaskPage() {
        return "/task/createSlideshowTask"; // templates/task/createSlideshowTask.html
    }

    @GetMapping("/posterTaskPage")
    public String showPosterTaskPage() {
        return "/task/createPosterTask"; // templates/task/createPosterTask.html
    }

    @GetMapping("/otherTaskPage")
    public String showOtherTaskPage() {
        return "/task/createOtherTask"; // templates/task/createOtherTask.html
    }

    @PostMapping("/api/send-contact-mail")
    public String sendContactEmail(@ModelAttribute ContactFormDto form, Model model) {
        String subject = "Kontaktanfrage von " + form.getFirstName() + " " + form.getLastName();
        String message = "GPN: " + form.getGpn() + "\nEmail: " + form.getEmail() + "\n\nMessage:\n" + form.getMessage();

        mailService.sendEmail("dariangermann@gmail.com", subject, message);
        model.addAttribute("formFeedback", "Thank you for your feedback!");
        return "contact";
    }

    // For reaching the update Task page
    @GetMapping("/test-task-url")
    public String testTaskUrl() {
        String taskUrl = hashService.getInfoUrl(27);
        System.out.println("URL for task 27: " + taskUrl);
        return "redirect:/";
    }


}