package unipi.d3fender.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import unipi.d3fender.dtos.AssessmentResponse;
import unipi.d3fender.dtos.QuestionnaireResponse;

import java.util.Map;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import unipi.d3fender.dtos.SbomAnalysisResponse;


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

    public SbomAnalysisResponse analyzeSbom(MultipartFile file) {
        try {
            ByteArrayResource fileResource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", fileResource);

            return restClient.post()
                    .uri(apiBaseUrl + "/api/sbom/analyze")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(body)
                    .retrieve()
                    .body(SbomAnalysisResponse.class);

        } catch (Exception ex) {
            throw new RuntimeException("Failed to analyze SBOM file", ex);
        }
    }
}
