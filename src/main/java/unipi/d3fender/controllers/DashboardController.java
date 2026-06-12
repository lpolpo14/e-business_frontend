package unipi.d3fender.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import unipi.d3fender.services.UserService;

@Controller
@RequiredArgsConstructor
public class DashboardController {
    private final UserService userService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("isPro", userService.currentUserHasProSubscription());

        try {
            model.addAttribute("currentPlan", userService.getCurrentUser().getSubscriptionPlan());
        } catch (Exception ignored) {
            model.addAttribute("currentPlan", null);
        }

        return "dashboard";
    }
}
