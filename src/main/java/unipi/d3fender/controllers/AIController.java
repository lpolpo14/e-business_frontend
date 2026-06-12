package unipi.d3fender.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import unipi.d3fender.dtos.AIResponse;
import unipi.d3fender.services.AIExplanation;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AIController {

    private final AIExplanation openAiReportExplanationService;

    @PostMapping("/assessment/ai-explanation")
    public ResponseEntity<?> generateExplanation(@RequestBody Map<String, Object> request) {
        Object report = request.get("report");

        if (report == null) {
            return ResponseEntity.badRequest().body(Map.of(
                    "message", "Missing assessment report."
            ));
        }

        try {
            AIResponse explanation =
                    openAiReportExplanationService.explainAssessmentReport(report);

            return ResponseEntity.ok(explanation);

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(Map.of(
                    "message", "AI explanation is currently unavailable.",
                    "details", ex.getMessage()
            ));
        }
    }
}