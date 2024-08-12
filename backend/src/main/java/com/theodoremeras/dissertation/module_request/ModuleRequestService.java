package com.theodoremeras.dissertation.module_request;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ModuleRequestService {

    private final ModuleRequestRepository moduleRequestRepository;

    public ModuleRequestService(ModuleRequestRepository moduleRequestRepository) {
        this.moduleRequestRepository = moduleRequestRepository;
    }

    public ModuleRequestEntity save(ModuleRequestEntity moduleRequestEntity) {
        return moduleRequestRepository.save(moduleRequestEntity);
    }

    public List<ModuleRequestEntity> findAll() {
        return moduleRequestRepository.findAll();
    }

    public List<ModuleRequestEntity> findAllByEcApplicationIdIn(List<Integer> ecApplicationIds) {
        return moduleRequestRepository.findAllByEcApplicationIdIn(ecApplicationIds);
    }

    public Optional<ModuleRequestEntity> findOneById(Integer id) {
        return moduleRequestRepository.findById(id);
    }

    public Boolean exists(Integer id) {
        return moduleRequestRepository.existsById(id);
    }

    public void delete(Integer id) {
        moduleRequestRepository.deleteById(id);
    }

}
