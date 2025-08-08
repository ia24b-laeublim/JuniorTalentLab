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
        if (form.getEmail() == null || !form.getEmail().endsWith("@ubs.com")) {
            model.addAttribute("formFeedback", "Please use a valid @ubs.com email address.");
            return "contact";
        }

        String subject = "Kontaktanfrage von " + form.getFirstName() + " " + form.getLastName();

        String text = "GPN: " + form.getGpn() +
                "\nEmail: " + form.getEmail() +
                "\n\nMessage:\n" + form.getMessage();

        String html = renderContactEmail(
                safe(form.getFirstName()),
                safe(form.getLastName()),
                safe(form.getGpn()),
                safe(form.getEmail()),
                safe(form.getMessage())
        );

        mailService.sendEmail("dariangermann@gmail.com", subject, text, html);
        model.addAttribute("formFeedback", "Thank you for your feedback!");
        return "contact";
    }

    // For testing the update Task page
    @GetMapping("/test-task-url")
    public String testTaskUrl() {
        String taskUrl = hashService.getInfoUrl(27);
        System.out.println("URL for task 27: " + taskUrl);
        return "redirect:/";
    }

    private String renderContactEmail(String first, String last, String gpn, String email, String message) {
        String tpl = """
                <!doctype html>
                       <html lang="en">
                         <head>
                           <meta charset="UTF-8">
                           <meta name="x-apple-disable-message-reformatting">
                           <meta name="viewport" content="width=device-width, initial-scale=1">
                           <title>Contact Request</title>
                         </head>
                         <body style="margin:0;padding:0;background:#f6f8fa;">
                           <center style="width:100%;background:#f6f8fa;">
                             <table role="presentation" cellpadding="0" cellspacing="0" border="0" width="100%" style="background:#f6f8fa;">
                               <tr>
                                 <td align="center" style="padding:24px 12px;">
                                   <table role="presentation" cellpadding="0" cellspacing="0" border="0" width="640" style="width:640px;max-width:100%;background:#ffffff;border:1px solid #e1e3e1;border-radius:12px;overflow:hidden;font-family:Arial,Helvetica,sans-serif;">
                                     <tr>
                                       <td style="background:#f9f9f9;color:#000000;padding:18px 24px;">
                                         <div style="font-size:18px;font-weight:700;line-height:1.2;">New Contact Request</div>
                                         <div style="height:6px;width:60px;background:#E60100;border-radius:3px;margin-top:10px;"></div>
                                       </td>
                                     </tr>
                
                                     <tr>
                                       <td style="padding:24px;">
                                         <table role="presentation" cellpadding="0" cellspacing="0" border="0" width="100%" style="border-collapse:separate;border-spacing:0 10px;">
                                           <tr>
                                             <td style="width:140px;vertical-align:top;color:#666;font-size:12px;font-weight:700;padding:0;">First Name</td>
                                             <td style="vertical-align:top;color:#000000;font-size:15px;padding:0;">${FIRST}</td>
                                           </tr>
                                           <tr>
                                             <td style="width:140px;vertical-align:top;color:#666;font-size:12px;font-weight:700;padding:0;">Last Name</td>
                                             <td style="vertical-align:top;color:#000000;font-size:15px;padding:0;">${LAST}</td>
                                           </tr>
                                           <tr>
                                             <td style="width:140px;vertical-align:top;color:#666;font-size:12px;font-weight:700;padding:0;">GPN</td>
                                             <td style="vertical-align:top;color:#000000;font-size:15px;padding:0;">${GPN}</td>
                                           </tr>
                                           <tr>
                                             <td style="width:140px;vertical-align:top;color:#666;font-size:12px;font-weight:700;padding:0;">Email</td>
                                             <td style="vertical-align:top;color:#000000;font-size:15px;padding:0;">${EMAIL}</td>
                                           </tr>
                                         </table>
                
                                         <div style="margin-top:18px;background:#f9f9f9;border:1px solid #e0e0e0;border-radius:6px;padding:14px;">
                                           <div style="color:#666;font-size:12px;font-weight:700;margin-bottom:6px;">Message</div>
                                           <div style="color:#000000;font-size:15px;line-height:1.55;white-space:pre-wrap;">${MESSAGE}</div>
                                         </div>
                
                                         <table role="presentation" cellpadding="0" cellspacing="0" border="0" style="margin-top:22px;">
                                         </table>
                                       </td>
                                     </tr>
                
                                     <tr>
                                       <td style="background:#f9f9f9;color:#000000;font-size:12px;padding:14px 24px;border-top:1px solid #eaecef;">
                                         This email was automatically sent from the Junior Talent Lab contact form.
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

        return tpl
                .replace("${FIRST}", safe(first))
                .replace("${LAST}", safe(last))
                .replace("${GPN}", safe(gpn))
                .replace("${EMAIL}", safe(email))
                .replace("${MESSAGE}", safe(message));
    }

    private String safe(String s) {
        if (s == null) return "";
        return s
                .replace("&","&amp;")
                .replace("<","&lt;")
                .replace(">","&gt;")
                .replace("\"","&quot;")
                .replace("'","&#39;");
    }

}