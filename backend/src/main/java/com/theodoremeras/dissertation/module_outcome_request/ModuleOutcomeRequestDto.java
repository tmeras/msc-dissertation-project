package com.theodoremeras.dissertation.module_outcome_request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ModuleOutcomeRequestDto {

    private Integer id;

    private String requestedOutcome;

    private Integer ecApplicationId;

    private String moduleCode;

}
