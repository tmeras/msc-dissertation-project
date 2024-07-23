package com.theodoremeras.dissertation.module_decision;

import com.theodoremeras.dissertation.module_request.ModuleRequestEntity;
import com.theodoremeras.dissertation.user.UserEntity;
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
@Table(name = "module_request_decision")
public class ModuleDecisionEntity {

    @Id
    @GeneratedValue
    private Integer id;

    private String comments;

    private Boolean isApproved;

    @ManyToOne
    @JoinColumn(name = "module_request_id", nullable = false)
    private ModuleRequestEntity moduleRequest;

    @ManyToOne
    @JoinColumn(name = "staff_id", nullable = false)
    private UserEntity staffMember;

}
