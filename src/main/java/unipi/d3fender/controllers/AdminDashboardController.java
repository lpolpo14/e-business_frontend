package unipi.d3fender.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import unipi.d3fender.services.AssessmentClient;
import unipi.d3fender.dtos.SbomAnalysisResponse;
import unipi.d3fender.repositories.UserRepository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final UserRepository userRepository;
    private final AssessmentClient assessmentClient;
    private final ObjectMapper objectMapper;

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) throws IOException {
        SbomSummary sbomSummary = loadSbomSummary();

        model.addAttribute("totalUsers", userRepository.count());
        model.addAttribute("sbomSummary", sbomSummary);

        return "admin-dashboard";
    }

    @GetMapping("/sbom")
    public String viewSbom(Model model) throws IOException {
        SbomSummary sbomSummary = loadSbomSummary();
        String rawSbomJson = loadSbomJson();

        model.addAttribute("sbomSummary", sbomSummary);
        model.addAttribute("rawSbomJson", rawSbomJson);

        return "admin-sbom";
    }

    @PostMapping("/sbom/analyze")
    public String analyzeBackendSbom(Model model) throws IOException {
        byte[] sbomBytes = loadSbomBytes();

        SbomAnalysisResponse response = assessmentClient.analyzeSbomBytes(sbomBytes, "sbom.json");

        model.addAttribute("response", response);

        return "sbom-result";
    }

    private SbomSummary loadSbomSummary() throws IOException {
        String sbomJson = loadSbomJson();
        JsonNode root = objectMapper.readTree(sbomJson);

        String bomFormat = getTextOrDefault(root, "bomFormat", "Unknown");
        String specVersion = getTextOrDefault(root, "specVersion", "Unknown");
        String serialNumber = getTextOrDefault(root, "serialNumber", "Unknown");

        JsonNode componentsNode = root.path("components");

        int componentCount = componentsNode.isArray() ? componentsNode.size() : 0;

        List<SbomComponentView> components = new ArrayList<>();

        if (componentsNode.isArray()) {
            for (JsonNode componentNode : componentsNode) {
                components.add(new SbomComponentView(
                        getTextOrDefault(componentNode, "name", "Unknown"),
                        getTextOrDefault(componentNode, "version", "Unknown"),
                        getTextOrDefault(componentNode, "type", "Unknown"),
                        getTextOrDefault(componentNode, "purl", "")
                ));
            }
        }

        return new SbomSummary(
                bomFormat,
                specVersion,
                serialNumber,
                componentCount,
                components
        );
    }

    private String loadSbomJson() throws IOException {
        return new String(loadSbomBytes(), StandardCharsets.UTF_8);
    }

    private byte[] loadSbomBytes() throws IOException {
        ClassPathResource resource = new ClassPathResource("sbom/sbom.json");
        return resource.getInputStream().readAllBytes();
    }

    private String getTextOrDefault(JsonNode node, String fieldName, String defaultValue) {
        JsonNode value = node.get(fieldName);

        if (value == null || value.isNull() || value.asText().isBlank()) {
            return defaultValue;
        }

        return value.asText();
    }

    public record SbomSummary(
            String bomFormat,
            String specVersion,
            String serialNumber,
            int componentCount,
            List<SbomComponentView> components
    ) {
    }

    public record SbomComponentView(
            String name,
            String version,
            String type,
            String purl
    ) {
    }
}