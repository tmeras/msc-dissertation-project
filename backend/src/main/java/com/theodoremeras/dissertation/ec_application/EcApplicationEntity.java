package com.theodoremeras.dissertation.ec_application;

import com.theodoremeras.dissertation.user.UserEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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

    @NotBlank
    @Size(min = 1, max = 5000)
    private String circumstancesDetails;

    @NotNull
    @Past
    private LocalDate affectedDateStart;

    @NotNull
    @PastOrPresent
    private LocalDate affectedDateEnd;

    @NotNull
    @PastOrPresent
    private LocalDate submittedOn;

    private Boolean requiresFurtherEvidence;

    private Boolean isReferred;

    // Id of student who submitted the application
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private UserEntity student;

}
