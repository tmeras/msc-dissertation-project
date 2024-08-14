package com.theodoremeras.dissertation.module_request;


import com.theodoremeras.dissertation.ec_application.EcApplicationEntity;
import com.theodoremeras.dissertation.module.ModuleEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;

@Getter
@Setter
@ToString
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
    @ToString.Exclude
    private EcApplicationEntity ecApplication;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_code", nullable = false)
    @ToString.Exclude
    private ModuleEntity module;

    @Generated
    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        ModuleRequestEntity that = (ModuleRequestEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Generated
    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
