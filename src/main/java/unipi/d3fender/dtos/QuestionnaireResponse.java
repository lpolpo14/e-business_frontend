package unipi.d3fender.dtos;

import lombok.Data;
import java.util.List;

@Data
public class QuestionnaireResponse {
    private List<QuestionnaireQuestion> threat_context;
    private List<QuestionnaireQuestion> security_controls;
    private List<QuestionnaireQuestion> defensive_capabilities;
}