package com.theodoremeras.dissertation.module;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ModuleService {

    private final ModuleRepository moduleRepository;

    public ModuleService(ModuleRepository moduleRepository) {
        this.moduleRepository = moduleRepository;
    }

    public ModuleEntity save(ModuleEntity moduleEntity) {
        return moduleRepository.save(moduleEntity);
    }

    public List<ModuleEntity> findAll() {
        return moduleRepository.findAll();
    }

    public List<ModuleEntity> findAllByModuleCodeIn(List<String> moduleCodes) {
        return moduleRepository.findAllByCodeIn(moduleCodes);
    }

    public Optional<ModuleEntity> findOneByCode(String moduleCode) {
        return moduleRepository.findById(moduleCode);
    }

    public Boolean exists(String moduleCode) {
        return moduleRepository.existsById(moduleCode);
    }

    public ModuleEntity partialUpdate(String moduleCode, ModuleEntity moduleEntity) {
        moduleEntity.setCode(moduleCode);

        return moduleRepository.findById(moduleCode).map(existingModule -> {
            Optional.ofNullable(moduleEntity.getName()).ifPresent(existingModule::setName);
            Optional.ofNullable(moduleEntity.getDepartment()).ifPresent(existingModule::setDepartment);
            return moduleRepository.save(existingModule);
        }).orElseThrow(() -> new RuntimeException("Could not find module with code " + moduleCode));
    }

    public void delete(String moduleCode) {
        moduleRepository.deleteById(moduleCode);
    }

}
