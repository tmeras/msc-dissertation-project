package com.theodoremeras.dissertation.evidence;

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
public class EvidenceDto {

    private Integer id;

    @NotBlank
    private String fileName;

    @NotNull
    private Integer ecApplicationId;

}
