package unipi.d3fender.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "index";
    }


    @GetMapping("/faq")
    public String faq() {
        return "faq";
    }

    @GetMapping("/terms")
    public String terms() {
        return "terms";
    }

    @GetMapping("/security-policy")
    public String securityPolicy() {
        return "security-policy";
    }

    @GetMapping("/on-premise")
    public String onPremise() {
        return "on-premise";
    }

    @GetMapping("/contact-sales")
    public String contactSales() {
        return "redirect:/on-premise";
    }
}
