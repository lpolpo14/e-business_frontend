package unipi.d3fender.dtos;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class SbomAnalysisResponse {
    private String filename;
    private int components_count;
    private int vulnerable_components_count;
    private List<Map<String, Object>> components;
    private Map<String, List<Map<String, Object>>> vulnerabilities;
    private int total_vulnerabilities_count;
}