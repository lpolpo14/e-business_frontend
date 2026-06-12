package unipi.d3fender.services;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import unipi.d3fender.dtos.AIResponse;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AIExplanation {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    @Value("${openai.api-key}")
    private String apiKey;

    @Value("${openai.model:gpt-4.1-mini}")
    private String model;

    @Value("${openai.base-url:https://api.openai.com/v1/responses}")
    private String openAiResponsesUrl;

    @Value("${openai.temperature:0.2}")
    private double temperature;

    @Value("${openai.max-output-tokens:1200}")
    private int maxOutputTokens;

    public AIResponse explainAssessmentReport(Object assessmentReport) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("OPENAI_API_KEY is not configured.");
        }

        try {
            String reportJson = objectMapper.writeValueAsString(assessmentReport);

            Map<String, Object> requestBody = new LinkedHashMap<>();
            requestBody.put("model", model);
            requestBody.put("instructions", buildInstructions());
            requestBody.put("input", buildUserInput(reportJson));
            requestBody.put("temperature", temperature);
            requestBody.put("max_output_tokens", maxOutputTokens);
            requestBody.put("text", Map.of(
                    "format", buildJsonSchema(),
                    "verbosity", "medium"
            ));

            JsonNode openAiResponse = restClient.post()
                    .uri(openAiResponsesUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + apiKey)
                    .body(requestBody)
                    .retrieve()
                    .body(JsonNode.class);

            String outputText = extractOutputText(openAiResponse);

            if (outputText == null || outputText.isBlank()) {
                throw new IllegalStateException("OpenAI returned an empty response.");
            }

            return objectMapper.readValue(outputText, AIResponse.class);

        } catch (RestClientResponseException ex) {
            throw new RuntimeException("OpenAI API request failed: " + ex.getResponseBodyAsString(), ex);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to generate AI explanation.", ex);
        }
    }

    private String buildInstructions() {
        return """
                You are the AI Security Analyst of D3FENDer, a cybersecurity SaaS platform.

                Your task is to explain a structured security gap assessment report.

                Rules:
                - Do not invent findings that are not present in the report.
                - Do not claim that a vulnerability definitely exists unless the report says so.
                - Treat the report as the source of truth.
                - Prioritize high severity findings first.
                - Explain ATT&CK techniques as attacker behaviors.
                - Explain D3FEND techniques as defensive mitigations or capabilities.
                - Use professional but understandable language.
                - Keep the answer useful for a business or security administrator.
                - Return only valid JSON matching the required schema.
                """;
    }

    private String buildUserInput(String reportJson) {
        return """
                Explain the following D3FENDer assessment report.

                Focus on:
                1. Executive summary
                2. Overall risk level
                3. Key observations
                4. Prioritized recommendations
                5. ATT&CK/D3FEND interpretation
                6. Concrete next steps

                Assessment report JSON:
                """ + reportJson;
    }

    private Map<String, Object> buildJsonSchema() {
        Map<String, Object> recommendationSchema = Map.of(
                "type", "object",
                "properties", Map.of(
                        "title", Map.of("type", "string"),
                        "reason", Map.of("type", "string"),
                        "relatedAttackTechniques", Map.of(
                                "type", "array",
                                "items", Map.of("type", "string")
                        ),
                        "relatedD3fendTechniques", Map.of(
                                "type", "array",
                                "items", Map.of("type", "string")
                        )
                ),
                "required", List.of(
                        "title",
                        "reason",
                        "relatedAttackTechniques",
                        "relatedD3fendTechniques"
                ),
                "additionalProperties", false
        );

        Map<String, Object> schema = new LinkedHashMap<>();
        schema.put("type", "object");
        schema.put("properties", Map.of(
                "executiveSummary", Map.of("type", "string"),
                "riskLevel", Map.of(
                        "type", "string",
                        "enum", List.of("LOW", "MEDIUM", "HIGH", "CRITICAL")
                ),
                "keyObservations", Map.of(
                        "type", "array",
                        "items", Map.of("type", "string")
                ),
                "priorityRecommendations", Map.of(
                        "type", "array",
                        "items", recommendationSchema
                ),
                "attackDefendExplanation", Map.of("type", "string"),
                "nextSteps", Map.of(
                        "type", "array",
                        "items", Map.of("type", "string")
                ),
                "disclaimer", Map.of("type", "string")
        ));
        schema.put("required", List.of(
                "executiveSummary",
                "riskLevel",
                "keyObservations",
                "priorityRecommendations",
                "attackDefendExplanation",
                "nextSteps",
                "disclaimer"
        ));
        schema.put("additionalProperties", false);

        return Map.of(
                "type", "json_schema",
                "name", "d3fender_ai_explanation",
                "strict", true,
                "schema", schema
        );
    }

    private String extractOutputText(JsonNode response) {
        if (response == null) {
            return null;
        }

        JsonNode directOutputText = response.get("output_text");
        if (directOutputText != null && directOutputText.isTextual()) {
            return directOutputText.asText();
        }

        JsonNode output = response.get("output");
        if (output == null || !output.isArray()) {
            return null;
        }

        for (JsonNode outputItem : output) {
            JsonNode content = outputItem.get("content");

            if (content == null || !content.isArray()) {
                continue;
            }

            for (JsonNode contentItem : content) {
                String type = contentItem.path("type").asText();

                if ("output_text".equals(type)) {
                    return contentItem.path("text").asText();
                }
            }
        }

        return null;
    }
}