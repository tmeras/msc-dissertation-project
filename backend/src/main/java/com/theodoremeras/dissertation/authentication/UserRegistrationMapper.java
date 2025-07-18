package com.theodoremeras.dissertation.authentication;

import com.theodoremeras.dissertation.department.DepartmentEntity;
import com.theodoremeras.dissertation.role.RoleEntity;
import com.theodoremeras.dissertation.user.UserEntity;
import org.springframework.stereotype.Service;

@Service
public class UserRegistrationMapper {

    public UserEntity mapFromDto(UserRegistrationDto userRegistrationDto) {
        RoleEntity roleEntity = (userRegistrationDto.getRoleId() == null) ? null :
                RoleEntity.builder()
                        .id(userRegistrationDto.getRoleId())
                        .build();

        DepartmentEntity departmentEntity = (userRegistrationDto.getDepartmentId() == null) ? null :
                DepartmentEntity.builder()
                        .id(userRegistrationDto.getDepartmentId())
                        .build();

        return UserEntity.builder()
                .id(userRegistrationDto.getId())
                .name(userRegistrationDto.getName())
                .email(userRegistrationDto.getEmail())
                .password(userRegistrationDto.getPassword())
                .isApproved(userRegistrationDto.getIsApproved())
                .role(roleEntity)
                .department(departmentEntity)
                .build();
    }

}
