package com.theodoremeras.dissertation.module_decision;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ModuleDecisionService {

    private final ModuleDecisionRepository moduleDecisionRepository;

    public ModuleDecisionService(ModuleDecisionRepository moduleDecisionRepository) {
        this.moduleDecisionRepository = moduleDecisionRepository;
    }

    public ModuleDecisionEntity save(ModuleDecisionEntity moduleDecisionEntity) {
        return moduleDecisionRepository.save(moduleDecisionEntity);
    }

    public List<ModuleDecisionEntity> findAll() {
        return moduleDecisionRepository.findAll();
    }

    public List<ModuleDecisionEntity> findAllByModuleRequestId(Integer moduleRequestId) {
        return moduleDecisionRepository.findAllByModuleRequestId(moduleRequestId);
    }

    public List<ModuleDecisionEntity> findAllByStaffMemberId(Integer staffMemberId) {
        return moduleDecisionRepository.findAllByStaffMemberId(staffMemberId);
    }

    public List<ModuleDecisionEntity> findAllByApplicationIdIn(List<Integer> ecApplicationIds) {
        return moduleDecisionRepository.findAllByEcApplicationIdIn(ecApplicationIds);
    }

    public Optional<ModuleDecisionEntity> findOneById(Integer id) {
        return moduleDecisionRepository.findById(id);
    }

    public Boolean exists(Integer id) {
        return moduleDecisionRepository.existsById(id);
    }

    public void delete(Integer id) {
        moduleDecisionRepository.deleteById(id);
    }

}
