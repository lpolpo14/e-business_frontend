package unipi.d3fender.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import unipi.d3fender.dtos.AssessmentResponse;

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
}
