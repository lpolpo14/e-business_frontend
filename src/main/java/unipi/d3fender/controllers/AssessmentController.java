package unipi.d3fender.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.core.JacksonException;
import unipi.d3fender.dtos.AssessmentRequest;
import unipi.d3fender.dtos.AssessmentResponse;
import unipi.d3fender.dtos.QuestionnaireResponse;
import unipi.d3fender.services.AssessmentClient;
import unipi.d3fender.services.UserService;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/assessment")
@SessionAttributes("questionnaireAnswers")
public class AssessmentController {

    private final AssessmentClient assessmentClient;
    private final UserService userService;
    private final ObjectMapper objectMapper;


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

    @GetMapping("/json")
    public String jsonAssessmentForm(Model model) {
        if (!userService.currentUserHasSubscription()) {
            return "redirect:/pricing?subscriptionRequired";
        }

        if (!userService.currentUserHasProSubscription()) {
            return "redirect:/dashboard?proRequired";
        }

        model.addAttribute("assessmentRequest", new AssessmentRequest());
        return "assessment-json";
    }

    @PostMapping("/json")
    public String submitJsonAssessment(
            @Valid @ModelAttribute AssessmentRequest assessmentRequest,
            BindingResult bindingResult,
            Model model
    ) {
        if (!userService.currentUserHasSubscription()) {
            return "redirect:/pricing?subscriptionRequired";
        }

        if (!userService.currentUserHasProSubscription()) {
            return "redirect:/dashboard?proRequired";
        }

        if (bindingResult.hasErrors()) {
            return "assessment-json";
        }

        try {
            objectMapper.readTree(assessmentRequest.getContent());
        } catch (JacksonException e) {
            model.addAttribute("error", "Invalid JSON input. Please check your syntax and try again.");
            return "assessment-json";
        }

        try {
            AssessmentResponse response =
                    assessmentClient.assessJson(assessmentRequest.getContent());

            model.addAttribute("response", response);

            return "assessment-result";
        } catch (RestClientException e) {
            model.addAttribute("error", "D3FENDer could not process this JSON assessment right now. Please try again.");
            return "assessment-json";
        }
    }

    @GetMapping("/questionnaire")
    public String questionnaireForm() {
        if (!userService.currentUserHasSubscription()) {
            return "redirect:/pricing?subscriptionRequired";
        }

        return "assessment-questionnaire";
    }

    @PostMapping("/questionnaire")
    public String submitQuestionnaire(
            @RequestParam Map<String, String> formData,
            Model model
    ) {
        if (!userService.currentUserHasSubscription()) {
            return "redirect:/pricing?subscriptionRequired";
        }

        Map<String, Boolean> answers = new java.util.HashMap<>();

        formData.forEach((key, value) -> {
            answers.put(key, "true".equalsIgnoreCase(value) || "on".equalsIgnoreCase(value));
        });

        AssessmentResponse response =
                assessmentClient.assessQuestionnaire(answers);

        model.addAttribute("response", response);

        return "assessment-result";
    }

    @ModelAttribute("questionnaireAnswers")
    public Map<String, Boolean> questionnaireAnswers() {
        return new java.util.HashMap<>();
    }

    @GetMapping("/questionnaire/threat-context")
    public String threatContextPage(Model model) {

        if (!userService.currentUserHasSubscription()) {
            return "redirect:/pricing?subscriptionRequired";
        }

        if (!userService.currentUserHasProSubscription()) {
            return "redirect:/dashboard?proRequired";
        }

        QuestionnaireResponse questionnaire = assessmentClient.getQuestionnaire();

        model.addAttribute("questions", questionnaire.getThreat_context());
        model.addAttribute("stepTitle", "Threat Context");
        model.addAttribute("formAction", "/assessment/questionnaire/threat-context");

        return "assessment-questionnaire-step";
    }

    @PostMapping("/questionnaire/threat-context")
    public String submitThreatContext(
            @RequestParam Map<String, String> formData,
            @ModelAttribute("questionnaireAnswers") Map<String, Boolean> answers
    ) {
        if (!userService.currentUserHasSubscription()) {
            return "redirect:/pricing?subscriptionRequired";
        }

        if (!userService.currentUserHasProSubscription()) {
            return "redirect:/dashboard?proRequired";
        }
        saveBooleanAnswers(formData, answers);
        return "redirect:/assessment/questionnaire/controls";
    }

    @GetMapping("/questionnaire/controls")
    public String controlsPage(Model model) {
        QuestionnaireResponse questionnaire = assessmentClient.getQuestionnaire();

        model.addAttribute("questions", questionnaire.getSecurity_controls());
        model.addAttribute("stepTitle", "Security Controls");
        model.addAttribute("formAction", "/assessment/questionnaire/controls");

        return "assessment-questionnaire-step";
    }

    @PostMapping("/questionnaire/controls")
    public String submitControls(
            @RequestParam Map<String, String> formData,
            @ModelAttribute("questionnaireAnswers") Map<String, Boolean> answers
    ) {
        if (!userService.currentUserHasSubscription()) {
            return "redirect:/pricing?subscriptionRequired";
        }

        if (!userService.currentUserHasProSubscription()) {
            return "redirect:/dashboard?proRequired";
        }

        saveBooleanAnswers(formData, answers);
        return "redirect:/assessment/questionnaire/capabilities";
    }

    @GetMapping("/questionnaire/capabilities")
    public String capabilitiesPage(Model model) {

        if (!userService.currentUserHasSubscription()) {
            return "redirect:/pricing?subscriptionRequired";
        }

        if (!userService.currentUserHasProSubscription()) {
            return "redirect:/dashboard?proRequired";
        }

        QuestionnaireResponse questionnaire = assessmentClient.getQuestionnaire();

        model.addAttribute("questions", questionnaire.getDefensive_capabilities());
        model.addAttribute("stepTitle", "Defensive Capabilities");
        model.addAttribute("formAction", "/assessment/questionnaire/capabilities");

        return "assessment-questionnaire-step";
    }

    @PostMapping("/questionnaire/capabilities")
    public String submitCapabilities(
            @RequestParam Map<String, String> formData,
            @ModelAttribute("questionnaireAnswers") Map<String, Boolean> answers,
            Model model,
            org.springframework.web.bind.support.SessionStatus sessionStatus
    ) {

        if (!userService.currentUserHasSubscription()) {
            return "redirect:/pricing?subscriptionRequired";
        }

        if (!userService.currentUserHasProSubscription()) {
            return "redirect:/dashboard?proRequired";
        }

        saveBooleanAnswers(formData, answers);

        AssessmentResponse response =
                assessmentClient.assessQuestionnaire(answers);

        model.addAttribute("response", response);

        sessionStatus.setComplete();

        return "assessment-result";
    }

    private void saveBooleanAnswers(
            Map<String, String> formData,
            Map<String, Boolean> answers
    ) {
        formData.forEach((key, value) -> {
            if (!key.startsWith("_")) {
                answers.put(key, "true".equalsIgnoreCase(value) || "on".equalsIgnoreCase(value));
            }
        });
    }
}
