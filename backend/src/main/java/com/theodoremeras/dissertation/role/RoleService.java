package com.theodoremeras.dissertation.role;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public RoleEntity save(RoleEntity roleEntity) {
        return roleRepository.save(roleEntity);
    }

    public List<RoleEntity> findAll() {
        return roleRepository.findAll();
    }

    public List<RoleEntity> findAllByRoleName(String roleName) {
        return roleRepository.findAllByName(roleName);
    }

    public Optional<RoleEntity> findOneById(Integer id) {
        return roleRepository.findById(id);
    }

    public Boolean exists(Integer id) {
        return roleRepository.existsById(id);
    }

    public void delete(Integer id) {
        roleRepository.deleteById(id);
    }

}
