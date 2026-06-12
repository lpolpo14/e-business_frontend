package unipi.d3fender.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestParam;
import unipi.d3fender.dtos.SbomAnalysisResponse;
import unipi.d3fender.services.AssessmentClient;
import unipi.d3fender.services.UserService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/sbom")
public class SbomController {

    private final AssessmentClient assessmentClient;
    private final UserService userService;

    @GetMapping
    public String sbomForm() {
        if (!userService.currentUserHasSubscription()) {
            return "redirect:/pricing?subscriptionRequired";
        }

        return "sbom";
    }

    @PostMapping
    public String analyzeSbom(
            @RequestParam("file") MultipartFile file,
            Model model
    ) {
        if (!userService.currentUserHasSubscription()) {
            return "redirect:/pricing?subscriptionRequired";
        }

        if (!userService.currentUserHasProSubscription()) {
            return "redirect:/dashboard?proRequired";
        }

        if (file.isEmpty()) {
            model.addAttribute("error", "Please upload a valid SBOM JSON file.");
            return "sbom";
        }

        SbomAnalysisResponse response = assessmentClient.analyzeSbom(file);

        model.addAttribute("response", response);

        return "sbom-result";
    }
}