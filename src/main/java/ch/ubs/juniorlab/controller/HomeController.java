package ch.ubs.juniorlab.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

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
}