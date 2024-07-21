package com.theodoremeras.dissertation.module;

import com.theodoremeras.dissertation.department.DepartmentEntity;
import com.theodoremeras.dissertation.department.DepartmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class ModuleController {

    private ModuleService moduleService;

    private DepartmentService departmentService;

    private ModuleMapper moduleMapper;

    public ModuleController(
            ModuleService moduleService, DepartmentService departmentService, ModuleMapper moduleMapper
    ) {
        this.moduleService = moduleService;
        this.departmentService = departmentService;
        this.moduleMapper = moduleMapper;
    }

    @PostMapping(path = "/modules")
    public ResponseEntity<ModuleDto> createModule(@RequestBody ModuleDto moduleDto
    ) {
        // Module with the same code already exists
        if (moduleService.exists(moduleDto.getCode()))
            return new ResponseEntity<>(HttpStatus.CONFLICT);

        ModuleEntity moduleEntity = moduleMapper.mapFromDto(moduleDto);

        // Department must be specified when creating new module
        if (moduleEntity.getDepartment() == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        // Verify that the specified department exists
        Optional<DepartmentEntity> department = departmentService.findOneById(moduleEntity.getDepartment().getId());
        if (department.isPresent())
            moduleEntity.setDepartment(department.get());
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        ModuleEntity savedModuleEntity = moduleService.save(moduleEntity);

        return new ResponseEntity<>(moduleMapper.mapToDto(savedModuleEntity), HttpStatus.CREATED);
    }

    @GetMapping(path = "/modules")
    public List<ModuleDto> getAllModules() {
        List<ModuleEntity> moduleEntities = moduleService.findAll();
        return moduleEntities.stream()
                .map(moduleMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @GetMapping(path = "/modules/{code}")
    public ResponseEntity<ModuleDto> getModuleByCode(@PathVariable("code") String moduleCode) {
        Optional<ModuleEntity> foundModule = moduleService.findOneByCode(moduleCode);
        return foundModule.map(moduleEntity -> {
          ModuleDto moduleDto = moduleMapper.mapToDto(moduleEntity);
          return new ResponseEntity<>(moduleDto, HttpStatus.OK);
        }).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PatchMapping(path = "/modules/{code}")
    public ResponseEntity<ModuleDto> partialUpdateModule(
            @PathVariable("code") String moduleCode, @RequestBody ModuleDto moduleDto
    ) {
        if (!moduleService.exists(moduleCode))
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        moduleDto.setCode(moduleCode);
        ModuleEntity moduleEntity = moduleMapper.mapFromDto(moduleDto);
        ModuleEntity updatedModuleEntity = moduleService.partialUpdate(moduleCode, moduleEntity);
        return new ResponseEntity<>(moduleMapper.mapToDto(updatedModuleEntity), HttpStatus.OK);
    }

    @DeleteMapping(path = "/modules/{code}")
    public ResponseEntity<String> deleteModule(@PathVariable("code") String moduleCode) {
        moduleService.delete(moduleCode);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
