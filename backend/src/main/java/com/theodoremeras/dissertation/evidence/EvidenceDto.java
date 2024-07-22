package com.theodoremeras.dissertation.evidence;

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

    private String fileName;

    private Integer ecApplicationId;

}
