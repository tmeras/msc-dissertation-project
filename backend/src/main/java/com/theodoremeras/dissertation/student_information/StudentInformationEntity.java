package com.theodoremeras.dissertation.student_information;

import com.theodoremeras.dissertation.user.UserEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "student_information")
public class StudentInformationEntity {

    @Id
    @GeneratedValue
    private Integer id;

    private Boolean hasHealthIssues;

    private Boolean hasDisability;

    private Boolean hasLsp;

    @Size(min = 1, max = 5000)
    private String additionalDetails;

    // Id of student whose data is stored
    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private UserEntity student;

}
