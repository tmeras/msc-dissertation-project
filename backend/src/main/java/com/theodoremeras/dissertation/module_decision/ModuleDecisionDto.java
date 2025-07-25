package com.theodoremeras.dissertation.module_decision;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ModuleDecisionDto {

    private Integer id;

    private String comments;

    private Boolean isApproved;

    @NotNull
    private Integer moduleRequestId;

    @NotNull
    private Integer staffMemberId;

    @NotNull
    private Integer ecApplicationId;

}
