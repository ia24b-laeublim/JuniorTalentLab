package ch.ubs.juniorlab.controller;

import ch.ubs.juniorlab.service.MailService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class HomeController {

    private final MailService mailService;

    public HomeController(MailService mailService) {
        this.mailService = mailService;
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
        String message = "GPN: " + form.getGpn() + "\nEmail: " + form.getEmail() + "\n\nConcern:\n" + form.getConcern();

        mailService.sendEmail("dariangermann@gmail.com", subject, message);
        model.addAttribute("formFeedback", "Thank you for your feedback!");
        return "contact";
    }

    @PostMapping("/api/create-task")
    public String createTask(@ModelAttribute CreateTaskDto taskDto, Model model) {
        // TODO: Implement task creation logic
        // For now, redirect to a success page or back to the form with a success message
        model.addAttribute("taskCreated", "Task created successfully!");
        return "createTaskPage";
    }

    // Specific task creation endpoints
    @PostMapping("/api/create-task/flyer")
    public String createFlyerTask(@ModelAttribute CreateTaskDto taskDto, Model model) {
        // TODO: Implement flyer task creation logic
        model.addAttribute("taskCreated", "Flyer task created successfully!");
        return "task/createFlyerTask";
    }

    @PostMapping("/api/create-task/poll")
    public String createPollTask(@ModelAttribute CreateTaskDto taskDto, Model model) {
        // TODO: Implement poll task creation logic
        model.addAttribute("taskCreated", "Poll task created successfully!");
        return "task/createPollTask";
    }

    @PostMapping("/api/create-task/video")
    public String createVideoTask(@ModelAttribute CreateTaskDto taskDto, Model model) {
        // TODO: Implement video task creation logic
        model.addAttribute("taskCreated", "Video task created successfully!");
        return "task/createVideoTask";
    }

    @PostMapping("/api/create-task/photo")
    public String createPhotoTask(@ModelAttribute CreateTaskDto taskDto, Model model) {
        // TODO: Implement photo task creation logic
        model.addAttribute("taskCreated", "Photo task created successfully!");
        return "task/createPhotoTask";
    }

    @PostMapping("/api/create-task/slideshow")
    public String createSlideshowTask(@ModelAttribute CreateTaskDto taskDto, Model model) {
        // TODO: Implement slideshow task creation logic
        model.addAttribute("taskCreated", "Slideshow task created successfully!");
        return "task/createSlideshowTask";
    }

    @PostMapping("/api/create-task/poster")
    public String createPosterTask(@ModelAttribute CreateTaskDto taskDto, Model model) {
        // TODO: Implement poster task creation logic
        model.addAttribute("taskCreated", "Poster task created successfully!");
        return "task/createPosterTask";
    }

    @PostMapping("/api/create-task/other")
    public String createOtherTask(@ModelAttribute CreateTaskDto taskDto, Model model) {
        // TODO: Implement other task creation logic
        model.addAttribute("taskCreated", "Custom task created successfully!");
        return "task/createOtherTask";
    }
}