package com.theodoremeras.dissertation.department;

import com.theodoremeras.dissertation.module.ModuleEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "department")
public class DepartmentEntity {

    @Id
    @GeneratedValue
    private Integer id;

    @NotNull
    private String name;
}
