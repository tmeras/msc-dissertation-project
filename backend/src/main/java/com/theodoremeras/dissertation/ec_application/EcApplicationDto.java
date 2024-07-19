package com.theodoremeras.dissertation.ec_application;

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

    private String circumstancesDetails;

    private String additionalDetails;

    private LocalDate affectedDateStart;

    private LocalDate affectedDateEnd;

    private Boolean isReferred;

}
