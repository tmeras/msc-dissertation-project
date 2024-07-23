package com.theodoremeras.dissertation.module_request;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ModuleRequestService {

    private ModuleRequestRepository moduleRequestRepository;

    public ModuleRequestService(ModuleRequestRepository moduleRequestRepository) {
        this.moduleRequestRepository = moduleRequestRepository;
    }

    public ModuleRequestEntity save(ModuleRequestEntity moduleRequestEntity) {
        return moduleRequestRepository.save(moduleRequestEntity);
    }

    public List<ModuleRequestEntity> findAll() {
        return moduleRequestRepository.findAll();
    }

    public List<ModuleRequestEntity> findAllByEcApplicationId(Integer ecApplicationId) {
        return moduleRequestRepository.findAllByEcApplicationId(ecApplicationId);
    }

    public Optional<ModuleRequestEntity> findOneById(Integer id) {
        return moduleRequestRepository.findById(id);
    }

    public Boolean exists(Integer id) {
        return moduleRequestRepository.existsById(id);
    }

    public ModuleRequestEntity partialUpdate(Integer id, ModuleRequestEntity moduleRequestEntity) {
        moduleRequestEntity.setId(id);

        return moduleRequestRepository.findById(id).map(existingRequest -> {
            Optional.ofNullable(moduleRequestEntity.getRequestedOutcome())
                    .ifPresent(existingRequest::setRequestedOutcome);
            return moduleRequestRepository.save(existingRequest);
        }).orElseThrow(() -> new RuntimeException("Could not find module request with id " + id));
    }

    public void delete(Integer id) {
        moduleRequestRepository.deleteById(id);
    }

}
