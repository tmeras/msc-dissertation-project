package com.theodoremeras.dissertation.module;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class ModuleController {

    private ModuleService moduleService;

    private ModuleMapper moduleMapper;

    public ModuleController(ModuleService moduleService, ModuleMapper moduleMapper) {
        this.moduleService = moduleService;
        this.moduleMapper = moduleMapper;
    }

    @PutMapping(path = "/modules/{code}")
    public ResponseEntity<ModuleDto> createUpdateModule(
            @PathVariable("code") String moduleCode, @RequestBody ModuleDto moduleDto
    ) {
        ModuleEntity moduleEntity = moduleMapper.mapFromDto(moduleDto);
        boolean moduleExists = moduleService.exists(moduleCode);
        ModuleEntity savedModuleEntity = moduleService.save(moduleCode, moduleEntity);
        ModuleDto savedModuleDto = moduleMapper.mapToDto(savedModuleEntity);

        if (moduleExists) {
            return new ResponseEntity<>(savedModuleDto, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(savedModuleDto, HttpStatus.CREATED);
        }
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

    @PatchMapping("/modules/{code}")
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
