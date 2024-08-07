package com.theodoremeras.dissertation.user;

import com.theodoremeras.dissertation.department.DepartmentEntity;
import com.theodoremeras.dissertation.role.RoleEntity;
import org.springframework.stereotype.Service;

@Service
public class UserMapper {

    public UserDto mapToDto(UserEntity userEntity) {
        return UserDto.builder()
                .id(userEntity.getId())
                .name(userEntity.getName())
                .email(userEntity.getEmail())
                .isApproved(userEntity.getIsApproved())
                .roleId(userEntity.getRole().getId())
                .departmentId(userEntity.getDepartment().getId())
                .build();
    }

    public UserEntity mapFromDto(UserDto userDto) {
        RoleEntity roleEntity = (userDto.getRoleId() == null) ? null :
                RoleEntity.builder()
                        .id(userDto.getRoleId())
                        .build();

        DepartmentEntity departmentEntity = (userDto.getDepartmentId() == null) ? null :
                DepartmentEntity.builder()
                        .id(userDto.getDepartmentId())
                        .build();

        return UserEntity.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .email(userDto.getEmail())
                .isApproved(userDto.getIsApproved())
                .role(roleEntity)
                .department(departmentEntity)
                .build();
    }

}
