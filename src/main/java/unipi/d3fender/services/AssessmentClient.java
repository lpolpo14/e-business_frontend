package unipi.d3fender.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import unipi.d3fender.dtos.AssessmentResponse;
import unipi.d3fender.dtos.QuestionnaireResponse;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AssessmentClient {

    private final RestClient restClient;

    @Value("${d3fender.api.base-url}")
    private String apiBaseUrl;

    public AssessmentResponse assessPlainText(String content) {
        return restClient.post()
                .uri(apiBaseUrl + "/api/assess/text")
                .body(Map.of("content", content))
                .retrieve()
                .body(AssessmentResponse.class);
    }

    public AssessmentResponse assessJson(String content) {
        return restClient.post()
                .uri(apiBaseUrl + "/api/assess/json")
                .body(Map.of("content", content))
                .retrieve()
                .body(AssessmentResponse.class);
    }

    public AssessmentResponse assessQuestionnaire(Map<String, Boolean> answers) {
        return restClient.post()
                .uri(apiBaseUrl + "/api/assess/questionnaire")
                .body(Map.of("answers", answers))
                .retrieve()
                .body(AssessmentResponse.class);
    }

    public QuestionnaireResponse getQuestionnaire() {
        return restClient.get()
                .uri(apiBaseUrl + "/api/questionnaire")
                .retrieve()
                .body(QuestionnaireResponse.class);
    }
}
