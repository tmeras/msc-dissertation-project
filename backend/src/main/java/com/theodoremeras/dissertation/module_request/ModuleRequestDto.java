package com.theodoremeras.dissertation.module_request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ModuleRequestDto {

    private Integer id;

    @NotBlank
    private String requestedOutcome;

    private String relatedAssessment;

    @NotNull
    private Integer ecApplicationId;

    @NotBlank
    private String moduleCode;

}
