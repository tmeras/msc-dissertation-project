package com.theodoremeras.dissertation.user;

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
public class UserDto {

    private Integer id;

    @NotBlank
    private String name;

    @NotBlank
    private String email;

    private Boolean isApproved;

    @NotNull
    private Integer roleId;

    @NotNull
    private Integer departmentId;

}
