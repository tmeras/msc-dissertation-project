package com.theodoremeras.dissertation.ec_application;

import com.theodoremeras.dissertation.user.UserEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
@ToString
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
    @ToString.Exclude
    private UserEntity student;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        EcApplicationEntity that = (EcApplicationEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
