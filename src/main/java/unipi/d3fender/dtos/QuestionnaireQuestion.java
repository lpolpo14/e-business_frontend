package unipi.d3fender.dtos;

import lombok.Data;

@Data
public class QuestionnaireQuestion {
    private String id;
    private String title;
    private String category;
    private String text;
}