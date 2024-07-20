package com.theodoremeras.dissertation.department;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class DepartmentController {

    private DepartmentService departmentService;

    private DepartmentMapper departmentMapper;

    public DepartmentController(DepartmentService departmentService, DepartmentMapper departmentMapper) {
        this.departmentService = departmentService;
        this.departmentMapper = departmentMapper;
    }

    @PostMapping(path = "/departments")
    public ResponseEntity<DepartmentDto> createDepartment(@RequestBody DepartmentDto departmentDto) {
        // Department with the same id already exists
        if (departmentDto.getId() != null && departmentService.exists(departmentDto.getId()))
            return new ResponseEntity<>(HttpStatus.CONFLICT);

        DepartmentEntity departmentEntity = departmentMapper.mapFromDto(departmentDto);
        DepartmentEntity savedDepartmentEntity = departmentService.save(departmentEntity);
        return new ResponseEntity<>(departmentMapper.mapToDto(savedDepartmentEntity), HttpStatus.CREATED);
    }

    @GetMapping(path = "/departments")
    public List<DepartmentDto> getAllDepartments() {
        List<DepartmentEntity> departmentEntities = departmentService.findAll();
        return departmentEntities.stream()
                .map(departmentMapper::mapToDto)
                .collect(Collectors.toList());
    }

    @GetMapping(path = "/departments/{id}")
    public ResponseEntity<DepartmentDto> getDepartmentById(@PathVariable("id") Integer id) {
        Optional<DepartmentEntity> foundDepartment = departmentService.findOneById(id);
        return foundDepartment.map(departmentEntity ->{
            DepartmentDto departmentDto = departmentMapper.mapToDto(departmentEntity);
            return new ResponseEntity<>(departmentDto, HttpStatus.OK);
        }).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping(path = "/departments/{id}")
    public ResponseEntity<String> deleteDepartment(@PathVariable("id") Integer id) {
        departmentService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
