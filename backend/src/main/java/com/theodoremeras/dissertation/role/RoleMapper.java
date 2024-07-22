package com.theodoremeras.dissertation.role;

import org.springframework.stereotype.Service;

@Service
public class RoleMapper {

    public RoleDto mapToDto(RoleEntity roleEntity) {
        return RoleDto.builder()
                .id(roleEntity.getId())
                .name(roleEntity.getName())
                .build();
    }

    public RoleEntity mapFromDto(RoleDto roleDto) {
        return RoleEntity.builder()
                .id(roleDto.getId())
                .name(roleDto.getName())
                .build();
    }

}
