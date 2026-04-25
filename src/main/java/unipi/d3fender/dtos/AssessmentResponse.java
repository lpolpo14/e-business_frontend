package unipi.d3fender.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class AssessmentResponse {

    private String input_type;
    private int findings_count;
    private List<Map<String, Object>> findings;
}
