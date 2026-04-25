package unipi.d3fender.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssessmentRequest {

    @NotBlank
    @Size(min = 10, max = 10000)
    private String content;
}

