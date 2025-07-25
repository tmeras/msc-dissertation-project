package com.theodoremeras.dissertation.student_information;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentInformationDto {

    private Integer id;

    private Boolean hasHealthIssues;

    private Boolean hasDisability;

    private Boolean hasLsp;

    private String additionalDetails;

    @NotNull
    private Integer studentId;
}
