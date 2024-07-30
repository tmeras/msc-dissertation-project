package com.theodoremeras.dissertation.ec_application;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EcApplicationDto {

    private Integer id;

    @NotBlank
    private String circumstancesDetails;

    @NotNull
    private LocalDate affectedDateStart;

    @NotNull
    private LocalDate affectedDateEnd;

    @NotNull
    private LocalDate submittedOn;

    private Boolean requiresFurtherEvidence;

    private Boolean isReferred;

    @NotNull
    private Integer studentId;

}
