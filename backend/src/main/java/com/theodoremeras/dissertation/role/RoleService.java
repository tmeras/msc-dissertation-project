package com.theodoremeras.dissertation.role;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

    private RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public RoleEntity save(RoleEntity roleEntity) {
        return roleRepository.save(roleEntity);
    }

    public List<RoleEntity> findAll() {
        return roleRepository.findAll();
    }

    public Optional<RoleEntity> findOneById(Integer id) {
        return roleRepository.findById(id);
    }

    public Boolean exists(Integer id) {
        return roleRepository.existsById(id);
    }

    public RoleEntity partialUpdate(Integer id, RoleEntity roleEntity) {
        roleEntity.setId(id);

        return roleRepository.findById(id).map(existingRole -> {
            Optional.ofNullable(roleEntity.getName()).ifPresent(existingRole::setName);
            return roleRepository.save(existingRole);
        }).orElseThrow(() -> new RuntimeException("Could not find role with id + " + id));
    }

    public void delete(Integer id) {
        roleRepository.deleteById(id);
    }

}
