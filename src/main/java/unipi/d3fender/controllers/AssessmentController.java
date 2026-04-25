package unipi.d3fender.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import unipi.d3fender.dtos.AssessmentRequest;
import unipi.d3fender.dtos.AssessmentResponse;
import unipi.d3fender.services.AssessmentClient;
import unipi.d3fender.services.UserService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/assessment")
public class AssessmentController {

    private final AssessmentClient assessmentClient;
    private final UserService userService;


    @GetMapping
    public String assessmentForm(Model model) {
        if (!userService.currentUserHasSubscription()) {
            return "redirect:/pricing?subscriptionRequired";
        }

        model.addAttribute("assessmentRequest", new AssessmentRequest());
        return "assessment";
    }

    @PostMapping
    public String submitAssessment(
            @Valid @ModelAttribute AssessmentRequest assessmentRequest,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            return "assessment";
        }
        if (!userService.currentUserHasSubscription()) {
            return "redirect:/pricing?subscriptionRequired";
        }

        AssessmentResponse response =
                assessmentClient.assessPlainText(assessmentRequest.getContent());

        model.addAttribute("response", response);

        return "assessment-result";
    }
}
