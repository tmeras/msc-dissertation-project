package com.theodoremeras.dissertation.module_outcome_request;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ModuleOutcomeRequestService {

    private ModuleOutcomeRequestRepository moduleOutcomeRequestRepository;

    public ModuleOutcomeRequestService(ModuleOutcomeRequestRepository moduleOutcomeRequestRepository) {
        this.moduleOutcomeRequestRepository = moduleOutcomeRequestRepository;
    }

    public ModuleOutcomeRequestEntity save(ModuleOutcomeRequestEntity moduleOutcomeRequestEntity) {
        return moduleOutcomeRequestRepository.save(moduleOutcomeRequestEntity);
    }

    public List<ModuleOutcomeRequestEntity> findAll() {
        return moduleOutcomeRequestRepository.findAll();
    }

    public List<ModuleOutcomeRequestEntity> findAllByEcApplicationId(Integer ecApplicationId) {
        return moduleOutcomeRequestRepository.findAllByEcApplicationId(ecApplicationId);
    }

    public Optional<ModuleOutcomeRequestEntity> findOneById(Integer id) {
        return moduleOutcomeRequestRepository.findById(id);
    }

    public Boolean exists(Integer id) {
        return moduleOutcomeRequestRepository.existsById(id);
    }

    public ModuleOutcomeRequestEntity partialUpdate(Integer id, ModuleOutcomeRequestEntity moduleOutcomeRequestEntity) {
        moduleOutcomeRequestEntity.setId(id);

        return moduleOutcomeRequestRepository.findById(id).map(existingRequest -> {
            Optional.ofNullable(moduleOutcomeRequestEntity.getRequestedOutcome())
                    .ifPresent(existingRequest::setRequestedOutcome);
            return moduleOutcomeRequestRepository.save(existingRequest);
        }).orElseThrow(() -> new RuntimeException("Could not module outcome request with id " + id));
    }

    public void delete(Integer id) {
        moduleOutcomeRequestRepository.deleteById(id);
    }

}
