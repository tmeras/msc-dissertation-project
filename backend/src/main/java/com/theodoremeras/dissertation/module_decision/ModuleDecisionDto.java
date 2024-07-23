package com.theodoremeras.dissertation.module_decision;

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

    private Integer moduleRequestId;

    private Integer staffMemberId;

}
