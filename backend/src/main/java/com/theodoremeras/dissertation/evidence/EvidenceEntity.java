package com.theodoremeras.dissertation.evidence;

import com.theodoremeras.dissertation.ec_application.EcApplicationEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "evidence")
public class EvidenceEntity {

    @Id
    @GeneratedValue
    private Integer id;

    private String fileName;

    @ManyToOne
    @JoinColumn(name = "application_id", nullable = false)
    private EcApplicationEntity ecApplication;

}
