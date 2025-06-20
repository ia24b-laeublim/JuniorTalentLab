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

    @GetMapping("/apprenticePage")
    public String showApprenticePage() {
        return "/apprentice";
    }

    @GetMapping("/createTaskPage")
    public String showEmployeePage() {
        return "/employee";
    }

    @GetMapping("/requestPage")
    public String showRequestPage() {
        return "requestPage"; // entspricht requestPage.html in /templates
    }

    @GetMapping("/overviewPage")
    public String showOverviewPage() {
        return "overviewPage"; // entspricht overviewPage.html
    }

    @GetMapping("/finishedTasksPage")
    public String showFinishedTasksPage() {
        return "finishedTasksPage"; // entspricht finishedTasksPage.html
    }

    @PostMapping("/api/send-contact-mail")
    public String sendContactEmail(@ModelAttribute ContactFormDto form, Model model) {
        String subject = "Kontaktanfrage von " + form.getFirstName() + " " + form.getLastName();
        String message = "GPN: " + form.getGpn() + "\nEmail: " + form.getEmail() + "\n\nConcern:\n" + form.getConcern();

        mailService.sendEmail("dariangermann@gmail.com", subject, message);
        model.addAttribute("formFeedback", "Thank you for your feedback!");
        return "contact";
    }

}