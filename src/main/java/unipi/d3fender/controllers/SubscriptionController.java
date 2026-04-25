package unipi.d3fender.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import unipi.d3fender.services.UserService;

@Controller
@RequiredArgsConstructor
public class SubscriptionController {

    private final UserService userService;

    @GetMapping("/pricing")
    public String pricing(Model model) {
        try {
            model.addAttribute("currentPlan", userService.getCurrentUser().getSubscriptionPlan());
        } catch (Exception ignored) {
            model.addAttribute("currentPlan", null);
        }

        return "pricing";
    }

    @PostMapping("/subscription/select")
    public String selectSubscription(@RequestParam String plan) {
        userService.selectSubscription(plan);
        return "redirect:/dashboard?subscriptionSelected";
    }
}
