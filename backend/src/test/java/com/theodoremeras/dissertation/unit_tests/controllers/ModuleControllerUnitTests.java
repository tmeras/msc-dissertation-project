package com.theodoremeras.dissertation.unit_tests.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.theodoremeras.dissertation.TestDataUtil;
import com.theodoremeras.dissertation.department.DepartmentEntity;
import com.theodoremeras.dissertation.department.DepartmentService;
import com.theodoremeras.dissertation.module.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@WebMvcTest(ModuleController.class)
@AutoConfigureMockMvc(addFilters = false) // circumvent spring security for unit tests
public class ModuleControllerUnitTests {

    @MockBean
    private ModuleService moduleService;

    @MockBean
    private DepartmentService departmentService;

    @MockBean
    private ModuleMapper moduleMapper;

    private final MockMvc mockMvc;

    private final ObjectMapper objectMapper;

    private DepartmentEntity testDepartmentEntity;

    private ModuleEntity testModuleEntity;

    private ModuleDto testModuleDto;

    @Autowired
    public ModuleControllerUnitTests(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @BeforeEach
    public void setUp() {
        testDepartmentEntity = TestDataUtil.createTestDepartmentEntityA();
        testModuleEntity = TestDataUtil.createTestModuleEntityA(testDepartmentEntity);
        testModuleDto = TestDataUtil.createTestModuleDtoA(testDepartmentEntity.getId());
    }

    @Test
    public void testCreateModule() throws Exception {
        String moduleJson = objectMapper.writeValueAsString(testModuleDto);

        when(moduleService.exists(testModuleDto.getCode())).thenReturn(false);
        when(moduleMapper.mapFromDto(any())).thenReturn(testModuleEntity);
        when(departmentService.findOneById(testDepartmentEntity.getId()))
                .thenReturn(Optional.of(testDepartmentEntity));
        when(moduleService.save(testModuleEntity)).thenReturn(testModuleEntity);
        when(moduleMapper.mapToDto(testModuleEntity)).thenReturn(testModuleDto);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/modules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(moduleJson)
        ).andExpect(
                MockMvcResultMatchers.status().isCreated()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.code").value(testModuleDto.getCode())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.name").value(testModuleDto.getName())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.departmentId").value(testDepartmentEntity.getId())
        );
    }

    @Test
    public void testCreateModuleWhenModuleExists() throws Exception {
        String moduleJson = objectMapper.writeValueAsString(testModuleDto);

        when(moduleService.exists(testModuleDto.getCode())).thenReturn(true);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/modules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(moduleJson)
        ).andExpect(
                MockMvcResultMatchers.status().isConflict()
        );
    }

    @Test
    public void testCreateModuleWhenNoDepartmentIsSpecified() throws Exception {
        testModuleDto.setDepartmentId(null);
        String moduleJson = objectMapper.writeValueAsString(testModuleDto);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/modules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(moduleJson)
        ).andExpect(
                MockMvcResultMatchers.status().isBadRequest()
        );
    }

    @Test
    public void testCreateModuleWhenNoDepartmentExists() throws Exception {
        String moduleJson = objectMapper.writeValueAsString(testModuleDto);

        when(moduleService.exists(testModuleDto.getCode())).thenReturn(false);
        when(moduleMapper.mapFromDto(any())).thenReturn(testModuleEntity);
        when(departmentService.findOneById(testDepartmentEntity.getId()))
                .thenReturn(Optional.empty());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/modules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(moduleJson)
        ).andExpect(
                MockMvcResultMatchers.status().isNotFound()
        );
    }

    @Test
    public void testGetAllModules() throws Exception {
        when(moduleService.findAll()).thenReturn(List.of(testModuleEntity));
        when(moduleMapper.mapToDto(testModuleEntity)).thenReturn(testModuleDto);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/modules")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].code").value(testModuleEntity.getCode())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].name").value(testModuleEntity.getName())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].departmentId").value(testDepartmentEntity.getId())
        );
    }

    @Test
    public void testGetAllModulesByCodes() throws Exception {
        when(moduleService.findAllByModuleCodeIn(List.of(testModuleEntity.getCode())))
                .thenReturn(List.of(testModuleEntity));
        when(moduleMapper.mapToDto(testModuleEntity)).thenReturn(testModuleDto);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/modules?codes=" + testModuleEntity.getCode())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].code").value(testModuleEntity.getCode())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].name").value(testModuleEntity.getName())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].departmentId").value(testDepartmentEntity.getId())
        );
    }

    @Test
    public void testGetModuleById() throws Exception {
        when(moduleService.findOneByCode(testModuleEntity.getCode())).thenReturn(Optional.of(testModuleEntity));
        when(moduleMapper.mapToDto(testModuleEntity)).thenReturn(testModuleDto);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/modules/" + testModuleEntity.getCode())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.code").value(testModuleEntity.getCode())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.name").value(testModuleEntity.getName())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.departmentId").value(testDepartmentEntity.getId())
        );
    }

    @Test
    public void testGetModuleByIdWhenNoModuleExists() throws Exception {
        when(moduleService.findOneByCode(testModuleEntity.getCode())).thenReturn(Optional.empty());

        mockMvc.perform(
                MockMvcRequestBuilders.get("/modules/" + testModuleEntity.getCode())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isNotFound()
        );
    }

    @Test
    public void testPartialUpdateModule() throws Exception {
        ModuleEntity updatedModuleEntity = TestDataUtil.createTestModuleEntityB(testDepartmentEntity);
        updatedModuleEntity.setCode(testModuleEntity.getCode());
        ModuleDto updatedModuleDto = TestDataUtil.createTestModuleDtoB(testDepartmentEntity.getId());
        updatedModuleDto.setCode(testModuleEntity.getCode());
        String moduleUpdateJson = objectMapper.writeValueAsString(updatedModuleDto);

        when(moduleService.exists(testModuleEntity.getCode())).thenReturn(true);
        when(moduleMapper.mapFromDto(any())).thenReturn(updatedModuleEntity);
        when(moduleService.partialUpdate(testModuleEntity.getCode(), updatedModuleEntity)).thenReturn(updatedModuleEntity);
        when(moduleMapper.mapToDto(updatedModuleEntity)).thenReturn(updatedModuleDto);

        mockMvc.perform(
                MockMvcRequestBuilders.patch("/modules/" + testModuleEntity.getCode())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(moduleUpdateJson)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.code").value(testModuleEntity.getCode())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.name").value(updatedModuleDto.getName())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.departmentId").value(testDepartmentEntity.getId())
        );
    }

    @Test
    public void testPartialUpdateModuleWhenNoModuleExists() throws Exception {
        ModuleDto updatedModuleDto = TestDataUtil.createTestModuleDtoB(testDepartmentEntity.getId());
        updatedModuleDto.setCode(testModuleEntity.getCode());
        String moduleUpdateJson = objectMapper.writeValueAsString(updatedModuleDto);

        when(moduleService.exists(testModuleEntity.getCode())).thenReturn(false);

        mockMvc.perform(
                MockMvcRequestBuilders.patch("/modules/" + testModuleEntity.getCode())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(moduleUpdateJson)
        ).andExpect(
                MockMvcResultMatchers.status().isNotFound()
        );
    }

    @Test
    public void testDeleteModule() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/modules/" + testModuleEntity.getCode())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isNoContent()
        );

        verify(moduleService, times(1)).delete(testModuleEntity.getCode());
    }

}
