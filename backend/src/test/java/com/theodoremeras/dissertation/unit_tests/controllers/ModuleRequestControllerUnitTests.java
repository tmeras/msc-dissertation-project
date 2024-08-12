package com.theodoremeras.dissertation.unit_tests.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.theodoremeras.dissertation.TestDataUtil;
import com.theodoremeras.dissertation.department.DepartmentEntity;
import com.theodoremeras.dissertation.ec_application.EcApplicationEntity;
import com.theodoremeras.dissertation.ec_application.EcApplicationService;
import com.theodoremeras.dissertation.module.ModuleEntity;
import com.theodoremeras.dissertation.module.ModuleService;
import com.theodoremeras.dissertation.module_request.*;
import com.theodoremeras.dissertation.role.RoleEntity;
import com.theodoremeras.dissertation.user.UserController;
import com.theodoremeras.dissertation.user.UserEntity;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@WebMvcTest(ModuleRequestController.class)
@AutoConfigureMockMvc(addFilters = false) // circumvent spring security for unit tests
public class ModuleRequestControllerUnitTests {

    @MockBean
    private ModuleRequestService moduleRequestService;

    @MockBean
    private EcApplicationService ecApplicationService;

    @MockBean
    private ModuleService moduleService;

    @MockBean
    private ModuleRequestMapper moduleRequestMapper;

    private final MockMvc mockMvc;

    private final ObjectMapper objectMapper;

    private EcApplicationEntity testEcApplicationEntity;

    private ModuleEntity testModuleEntity;

    private ModuleRequestEntity testModuleRequestEntity;

    private ModuleRequestDto testModuleRequestDto;

    @Autowired
    public ModuleRequestControllerUnitTests(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @BeforeEach
    public void setUp() throws Exception {
        // Initialize test objects
        DepartmentEntity testDepartmentEntity = TestDataUtil.createTestDepartmentEntityA();
        RoleEntity testRoleEntity = TestDataUtil.createTestRoleEntityA();
        UserEntity testUserEntity = TestDataUtil.createTestUserEntityA(testRoleEntity, testDepartmentEntity);
        testEcApplicationEntity = TestDataUtil.createTestEcApplicationEntityA(testUserEntity);
        testModuleEntity = TestDataUtil.createTestModuleEntityA(testDepartmentEntity);
        testModuleRequestEntity = TestDataUtil.createTestRequestEntityA(testEcApplicationEntity, testModuleEntity);
        testModuleRequestDto = TestDataUtil
                .createTestRequestDtoA(testEcApplicationEntity.getId(), testModuleEntity.getCode());
    }

    @Test
    public void testCreateModuleRequest() throws Exception {
        String moduleRequestJson = objectMapper.writeValueAsString(testModuleRequestDto);

        when(moduleRequestMapper.mapFromDto(any())).thenReturn(testModuleRequestEntity);
        when(ecApplicationService.findOneById(testEcApplicationEntity.getId()))
                .thenReturn(Optional.of(testEcApplicationEntity));
        when(moduleService.findOneByCode(testModuleEntity.getCode())).thenReturn(Optional.of(testModuleEntity));
        when(moduleRequestService.save(testModuleRequestEntity)).thenReturn(testModuleRequestEntity);
        when(moduleRequestMapper.mapToDto(testModuleRequestEntity)).thenReturn(testModuleRequestDto);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/module-requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(moduleRequestJson)
        ).andExpect(
                MockMvcResultMatchers.status().isCreated()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.id")
                        .isNumber()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.requestedOutcome")
                        .value(testModuleRequestDto.getRequestedOutcome())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.ecApplicationId")
                        .value(testEcApplicationEntity.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.moduleCode")
                        .value(testModuleEntity.getCode())
        );
    }

    @Test
    public void testCreateModuleRequestWhenNoApplicationOrModuleIsSpecified() throws Exception {
        testModuleRequestDto.setModuleCode(null);
        testModuleRequestDto.setEcApplicationId(null);
        String moduleRequestJson = objectMapper.writeValueAsString(testModuleRequestDto);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/module-requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(moduleRequestJson)
        ).andExpect(
                MockMvcResultMatchers.status().isBadRequest()
        );
    }

    @Test
    public void testCreateModuleRequestWhenNoApplicationExists() throws Exception {
        String moduleRequestJson = objectMapper.writeValueAsString(testModuleRequestDto);

        when(moduleRequestMapper.mapFromDto(any())).thenReturn(testModuleRequestEntity);
        when(ecApplicationService.findOneById(testEcApplicationEntity.getId()))
                .thenReturn(Optional.empty());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/module-requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(moduleRequestJson)
        ).andExpect(
                MockMvcResultMatchers.status().isNotFound()
        );
    }

    @Test
    public void testCreateModuleRequestWhenNoModuleExists() throws Exception {
        String moduleRequestJson = objectMapper.writeValueAsString(testModuleRequestDto);

        when(moduleRequestMapper.mapFromDto(any())).thenReturn(testModuleRequestEntity);
        when(ecApplicationService.findOneById(testEcApplicationEntity.getId()))
                .thenReturn(Optional.of(testEcApplicationEntity));
        when(moduleService.findOneByCode(testModuleEntity.getCode())).thenReturn(Optional.empty());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/module-requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(moduleRequestJson)
        ).andExpect(
                MockMvcResultMatchers.status().isNotFound()
        );
    }

    @Test
    public void testGetAllModuleRequests() throws Exception {
        when(moduleRequestService.findAll()).thenReturn(List.of(testModuleRequestEntity));
        when(moduleRequestMapper.mapToDto(testModuleRequestEntity)).thenReturn(testModuleRequestDto);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/module-requests")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].id")
                        .value(testModuleRequestEntity.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].requestedOutcome")
                        .value(testModuleRequestEntity.getRequestedOutcome())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].ecApplicationId")
                        .value(testEcApplicationEntity.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].moduleCode")
                        .value(testModuleEntity.getCode())
        );
    }

    @Test
    public void testGetAllModuleRequestsByEcApplicationIds() throws Exception {
        when(moduleRequestService.findAllByEcApplicationIdIn(List.of(testEcApplicationEntity.getId())))
                .thenReturn(List.of(testModuleRequestEntity));
        when(moduleRequestMapper.mapToDto(testModuleRequestEntity)).thenReturn(testModuleRequestDto);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/module-requests?ecApplicationIds="+testEcApplicationEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].id")
                        .value(testModuleRequestEntity.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].requestedOutcome")
                        .value(testModuleRequestEntity.getRequestedOutcome())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].ecApplicationId")
                        .value(testEcApplicationEntity.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].moduleCode")
                        .value(testModuleEntity.getCode())
        );
    }

    @Test
    public void testDeleteModuleRequest() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/module-requests/" + testModuleRequestEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isNoContent()
        );

        verify(moduleRequestService, times(1)).delete(testModuleRequestEntity.getId());
    }

}
