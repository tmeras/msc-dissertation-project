package com.theodoremeras.dissertation.module_request;


import com.theodoremeras.dissertation.ec_application.EcApplicationEntity;
import com.theodoremeras.dissertation.module.ModuleEntity;
import jakarta.persistence.*;
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
@Entity
@Table(name = "module_outcome_request")
public class ModuleRequestEntity {

    @Id
    @GeneratedValue
    private Integer id;

    @NotBlank
    private String requestedOutcome;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private EcApplicationEntity ecApplication;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_code", nullable = false)
    private ModuleEntity module;

}
