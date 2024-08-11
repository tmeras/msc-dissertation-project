package com.theodoremeras.dissertation.unit_tests.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.theodoremeras.dissertation.TestDataUtil;
import com.theodoremeras.dissertation.department.*;
import com.theodoremeras.dissertation.evidence.EvidenceRepository;
import com.theodoremeras.dissertation.evidence.EvidenceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@WebMvcTest(DepartmentController.class)
@AutoConfigureMockMvc(addFilters = false) // circumvent spring security for unit tests
public class DepartmentControllerUnitTests {

    @MockBean
    private DepartmentService departmentService;

    @MockBean
    private DepartmentMapper departmentMapper;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private DepartmentEntity testDepartmentEntity;

    private DepartmentDto testDepartmentDto;

    @Autowired
    public DepartmentControllerUnitTests(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @BeforeEach
    public void setup() {
        // Initialize test objects
        testDepartmentEntity = TestDataUtil.createTestDepartmentEntityA();
        testDepartmentDto = TestDataUtil.createTestDepartmentDtoA();
    }

    @Test
    public void testCreateDepartment() throws Exception {
        String departmentJson = objectMapper.writeValueAsString(testDepartmentDto);

        when(departmentMapper.mapFromDto(any())).thenReturn(testDepartmentEntity);
        when(departmentService.save(testDepartmentEntity)).thenReturn(testDepartmentEntity);
        when(departmentMapper.mapToDto(testDepartmentEntity)).thenReturn(testDepartmentDto);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/departments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(departmentJson)
        ).andExpect(
                MockMvcResultMatchers.status().isCreated()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.id").isNumber()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.name").value(testDepartmentEntity.getName())
        );
    }

    @Test
    public void testGetAllDepartments() throws Exception {
        when(departmentService.findAll()).thenReturn(List.of(testDepartmentEntity));
        when(departmentMapper.mapToDto(testDepartmentEntity)).thenReturn(testDepartmentDto);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/departments")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].id").value(testDepartmentEntity.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].name").value(testDepartmentEntity.getName())
        );

    }

    @Test
    public void testGetDepartmentById() throws Exception {
        when(departmentService.findOneById(testDepartmentEntity.getId())).
                thenReturn(Optional.of(testDepartmentEntity));
        when(departmentMapper.mapToDto(testDepartmentEntity)).thenReturn(testDepartmentDto);


        mockMvc.perform(
                MockMvcRequestBuilders.get("/departments/" + testDepartmentEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.id").value(testDepartmentEntity.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.name").value(testDepartmentEntity.getName())
        );
    }

    @Test
    public void testGetDepartmentByIdWhenNoDepartmentExists() throws Exception {
        when(departmentService.findOneById(testDepartmentEntity.getId())).thenReturn(Optional.empty());

        mockMvc.perform(
                MockMvcRequestBuilders.get("/departments/1")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isNotFound()
        );
    }

    @Test
    public void testPartialUpdateDepartment() throws Exception {
        DepartmentEntity updatedDepartmentEntity = TestDataUtil.createTestDepartmentEntityB();
        updatedDepartmentEntity.setId(testDepartmentEntity.getId()); // Update name
        String departmentUpdateJson = objectMapper.writeValueAsString(updatedDepartmentEntity);
        DepartmentDto updatedDepartmentDto = TestDataUtil.createTestDepartmentDtoB();
        updatedDepartmentDto.setId(testDepartmentEntity.getId());

        when(departmentService.exists(testDepartmentEntity.getId())).thenReturn(true);
        when(departmentMapper.mapFromDto(any())).thenReturn(testDepartmentEntity);
        when(departmentService.partialUpdate(testDepartmentEntity.getId(), testDepartmentEntity))
                .thenReturn(updatedDepartmentEntity);
        when(departmentMapper.mapToDto(updatedDepartmentEntity)).thenReturn(updatedDepartmentDto);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .patch("/departments/" + testDepartmentEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(departmentUpdateJson)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.id").value(testDepartmentEntity.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.name").value(updatedDepartmentDto.getName())
        );
    }

    @Test
    public void testPartialUpdateDepartmentWhenNoDepartmentExists() throws Exception {
        DepartmentDto updatedDepartmentDto = TestDataUtil.createTestDepartmentDtoB();
        updatedDepartmentDto.setId(testDepartmentEntity.getId());
        String departmentUpdateJson = objectMapper.writeValueAsString(updatedDepartmentDto);

        when(departmentService.exists(testDepartmentEntity.getId())).thenReturn(false);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .patch("/departments/" + testDepartmentEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(departmentUpdateJson)
        ).andExpect(
                MockMvcResultMatchers.status().isNotFound()
        );
    }

    @Test
    public void testDeleteDepartment() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/departments/" + testDepartmentEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.status().isNoContent());

        verify(departmentService, times(1)).delete(testDepartmentEntity.getId());
    }

}
