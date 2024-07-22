package com.theodoremeras.dissertation.ec_application;

import com.theodoremeras.dissertation.user.UserEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "ec_application")
public class EcApplicationEntity {

    @Id
    @GeneratedValue
    private Integer id;

    @Size(min = 1, max = 5000)
    private String circumstancesDetails;

    @Size(min = 1, max = 5000)
    private String additionalDetails;

    @Past
    private LocalDate affectedDateStart;

    @PastOrPresent
    private LocalDate affectedDateEnd;

    private Boolean isReferred;

    // Id of student who submitted the application
    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private UserEntity student;

}
