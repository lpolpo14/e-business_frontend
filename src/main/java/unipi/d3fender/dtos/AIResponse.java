package unipi.d3fender.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AIResponse {

    private String executiveSummary;
    private String riskLevel;

    private List<String> keyObservations = new ArrayList<>();
    private List<PriorityRecommendation> priorityRecommendations = new ArrayList<>();

    private String attackDefendExplanation;

    private List<String> nextSteps = new ArrayList<>();

    private String disclaimer;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PriorityRecommendation {
        private String title;
        private String reason;
        private List<String> relatedAttackTechniques = new ArrayList<>();
        private List<String> relatedD3fendTechniques = new ArrayList<>();
    }
}