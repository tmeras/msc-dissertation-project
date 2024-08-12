package com.theodoremeras.dissertation.unit_tests.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.theodoremeras.dissertation.TestDataUtil;
import com.theodoremeras.dissertation.department.DepartmentEntity;
import com.theodoremeras.dissertation.ec_application.EcApplicationEntity;
import com.theodoremeras.dissertation.module_decision.*;
import com.theodoremeras.dissertation.module_request.ModuleRequestEntity;
import com.theodoremeras.dissertation.module_request.ModuleRequestService;
import com.theodoremeras.dissertation.role.RoleEntity;
import com.theodoremeras.dissertation.user.UserController;
import com.theodoremeras.dissertation.user.UserEntity;
import com.theodoremeras.dissertation.user.UserService;
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
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@WebMvcTest(ModuleDecisionController.class)
@AutoConfigureMockMvc(addFilters = false) // circumvent spring security for unit tests
public class ModuleDecisionControllerUnitTests {

    @MockBean
    private ModuleDecisionService moduleDecisionService;

    @MockBean
    private ModuleRequestService moduleRequestService;

    @MockBean
    private UserService userService;

    @MockBean
    private ModuleDecisionMapper moduleDecisionMapper;

    private final MockMvc mockMvc;

    private final ObjectMapper objectMapper;

    private ModuleRequestEntity testModuleRequestEntity;

    private EcApplicationEntity testEcApplicationEntity;

    private UserEntity testUserEntity;

    private ModuleDecisionEntity testModuleDecisionEntity;

    private ModuleDecisionDto testModuleDecisionDto;

    @Autowired
    public ModuleDecisionControllerUnitTests(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @BeforeEach
    public void setUp() throws Exception {
        // Initialize test objects
        DepartmentEntity testDepartmentEntity = TestDataUtil.createTestDepartmentEntityA();
        RoleEntity testRoleEntity = TestDataUtil.createTestRoleEntityA();
        testEcApplicationEntity = TestDataUtil.createTestEcApplicationEntityA(testUserEntity);
        testUserEntity = TestDataUtil
                .createTestUserEntityA(testRoleEntity, testDepartmentEntity);
        testModuleRequestEntity = TestDataUtil.createTestRequestEntityA(
                testEcApplicationEntity,
                TestDataUtil.createTestModuleEntityA(testDepartmentEntity)
        );
        testModuleDecisionEntity = TestDataUtil.createTestModuleDecisionEntityA(
                testModuleRequestEntity,
                testUserEntity,
                testEcApplicationEntity
        );
        testModuleDecisionDto = TestDataUtil.createTestModuleDecisionDtoA(
                testModuleRequestEntity.getId(),
                testUserEntity.getId(),
                testEcApplicationEntity.getId()
        );
    }

    @Test
    public void testCreateModuleDecision() throws Exception {
        String moduleDecisionJson = objectMapper.writeValueAsString(testModuleDecisionDto);

        when(moduleDecisionMapper.mapFromDto(any())).thenReturn(testModuleDecisionEntity);
        when(moduleRequestService.findOneById(testModuleRequestEntity.getId()))
                .thenReturn(Optional.of(testModuleRequestEntity));
        when(userService.findOneById(testUserEntity.getId()))
                .thenReturn(Optional.of(testUserEntity));
        when(moduleDecisionService.save(testModuleDecisionEntity)).thenReturn(testModuleDecisionEntity);
        when(moduleDecisionMapper.mapToDto(testModuleDecisionEntity)).thenReturn(testModuleDecisionDto);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/module-decisions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(moduleDecisionJson)
        ).andExpect(
                MockMvcResultMatchers.status().isCreated()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.id")
                        .isNumber()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.comments")
                        .value(testModuleDecisionDto.getComments())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.isApproved")
                        .value(testModuleDecisionDto.getIsApproved())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.moduleRequestId")
                        .value(testModuleRequestEntity.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.staffMemberId")
                        .value(testUserEntity.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.ecApplicationId")
                        .value(testEcApplicationEntity.getId())
        );
    }

    @Test
    public void testCreateModuleDecisionWhenNoModuleRequestOrStaffOrApplicationIsSpecified() throws Exception {
        testModuleDecisionDto.setModuleRequestId(null);
        testModuleDecisionDto.setEcApplicationId(null);
        testModuleDecisionDto.setStaffMemberId(null);
        String moduleDecisionJson = objectMapper.writeValueAsString(testModuleDecisionDto);

         mockMvc.perform(
                MockMvcRequestBuilders.post("/module-decisions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(moduleDecisionJson)
        ).andExpect(
                MockMvcResultMatchers.status().isBadRequest()
        );
    }

    @Test
    public void testCreateModuleDecisionWhenNoModuleRequestExists() throws Exception {
        String moduleDecisionJson = objectMapper.writeValueAsString(testModuleDecisionDto);

        when(moduleDecisionMapper.mapFromDto(any())).thenReturn(testModuleDecisionEntity);
        when(moduleRequestService.findOneById(testModuleRequestEntity.getId()))
                .thenReturn(Optional.empty());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/module-decisions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(moduleDecisionJson)
        ).andExpect(
                MockMvcResultMatchers.status().isNotFound()
        );
    }

    @Test
    public void testCreateModuleDecisionWhenNoStaffExists() throws Exception {
        String moduleDecisionJson = objectMapper.writeValueAsString(testModuleDecisionDto);

        when(moduleDecisionMapper.mapFromDto(any())).thenReturn(testModuleDecisionEntity);
        when(moduleRequestService.findOneById(testModuleRequestEntity.getId()))
                .thenReturn(Optional.of(testModuleRequestEntity));
        when(userService.findOneById(testUserEntity.getId()))
                .thenReturn(Optional.empty());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/module-decisions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(moduleDecisionJson)
        ).andExpect(
                MockMvcResultMatchers.status().isNotFound()
        );
    }

    @Test
    public void testCreateModuleDecisionWhenInvalidApplicationId() throws Exception {
        // Set module request to be associated with a different EC application
        testModuleRequestEntity = TestDataUtil.createTestRequestEntityB(
                TestDataUtil.createTestEcApplicationEntityB(testUserEntity),
                TestDataUtil.createTestModuleEntityA(TestDataUtil.createTestDepartmentEntityA())
        );
        testModuleDecisionEntity.setModuleRequest(testModuleRequestEntity);

        String moduleDecisionJson = objectMapper.writeValueAsString(testModuleDecisionDto);

        when(moduleDecisionMapper.mapFromDto(any())).thenReturn(testModuleDecisionEntity);
        when(moduleRequestService.findOneById(testModuleRequestEntity.getId()))
                .thenReturn(Optional.of(testModuleRequestEntity));
        when(userService.findOneById(testUserEntity.getId()))
                .thenReturn(Optional.of(testUserEntity));

        mockMvc.perform(
                MockMvcRequestBuilders.post("/module-decisions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(moduleDecisionJson)
        ).andExpect(
                MockMvcResultMatchers.status().isBadRequest()
        );
    }

    @Test
    public void testGetAllModuleDecisions() throws Exception {
        when(moduleDecisionService.findAll()).thenReturn(List.of(testModuleDecisionEntity));
        when(moduleDecisionMapper.mapToDto(testModuleDecisionEntity)).thenReturn(testModuleDecisionDto);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/module-decisions")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].id")
                        .value(testModuleDecisionEntity.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].comments")
                        .value(testModuleDecisionEntity.getComments())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].isApproved")
                        .value(testModuleDecisionEntity.getIsApproved())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].moduleRequestId")
                        .value(testModuleRequestEntity.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].staffMemberId")
                        .value(testUserEntity.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].ecApplicationId")
                        .value(testEcApplicationEntity.getId())
        );
    }

    @Test
    public void testGetAllModuleDecisionsByModuleRequestId() throws Exception {
        when(moduleDecisionService.findAllByModuleRequestId(testModuleRequestEntity.getId())).
                thenReturn(List.of(testModuleDecisionEntity));
        when(moduleDecisionMapper.mapToDto(testModuleDecisionEntity)).thenReturn(testModuleDecisionDto);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/module-decisions?moduleRequestId=" + testModuleRequestEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].id")
                        .value(testModuleDecisionEntity.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].comments")
                        .value(testModuleDecisionEntity.getComments())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].isApproved")
                        .value(testModuleDecisionEntity.getIsApproved())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].moduleRequestId")
                        .value(testModuleRequestEntity.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].staffMemberId")
                        .value(testUserEntity.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].ecApplicationId")
                        .value(testEcApplicationEntity.getId())
        );
    }

    @Test
    public void testGetAllModuleDecisionsByStaffMemberId() throws Exception {
        when(moduleDecisionService.findAllByStaffMemberId(testUserEntity.getId())).
                thenReturn(List.of(testModuleDecisionEntity));
        when(moduleDecisionMapper.mapToDto(testModuleDecisionEntity)).thenReturn(testModuleDecisionDto);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/module-decisions?staffMemberId=" + testUserEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].id")
                        .value(testModuleDecisionEntity.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].comments")
                        .value(testModuleDecisionEntity.getComments())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].isApproved")
                        .value(testModuleDecisionEntity.getIsApproved())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].moduleRequestId")
                        .value(testModuleRequestEntity.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].staffMemberId")
                        .value(testUserEntity.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].ecApplicationId")
                        .value(testEcApplicationEntity.getId())
        );
    }

    @Test
    public void testGetAllModuleDecisionsByApplicationIds() throws Exception {
        when(moduleDecisionService.findAllByEcApplicationIdIn(List.of(testEcApplicationEntity.getId())))
                .thenReturn(List.of(testModuleDecisionEntity));
        when(moduleDecisionMapper.mapToDto(testModuleDecisionEntity)).thenReturn(testModuleDecisionDto);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/module-decisions?ecApplicationIds=" + testEcApplicationEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].id")
                        .value(testModuleDecisionEntity.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].comments")
                        .value(testModuleDecisionEntity.getComments())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].isApproved")
                        .value(testModuleDecisionEntity.getIsApproved())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].moduleRequestId")
                        .value(testModuleRequestEntity.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].staffMemberId")
                        .value(testUserEntity.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].ecApplicationId")
                        .value(testEcApplicationEntity.getId())
        );
    }

    @Test
    public void testGetModuleDecisionById() throws Exception {
        when(moduleDecisionService.findOneById(testModuleDecisionEntity.getId()))
                .thenReturn(Optional.of(testModuleDecisionEntity));
        when(moduleDecisionMapper.mapToDto(testModuleDecisionEntity)).thenReturn(testModuleDecisionDto);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/module-decisions/" + testModuleDecisionEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.id")
                        .isNumber()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.comments")
                        .value(testModuleDecisionEntity.getComments())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.isApproved")
                        .value(testModuleDecisionEntity.getIsApproved())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.moduleRequestId")
                        .value(testModuleRequestEntity.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.staffMemberId")
                        .value(testUserEntity.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.ecApplicationId")
                        .value(testEcApplicationEntity.getId())
        );
    }

    @Test
    public void testGetModuleDecisionByIdWhenNoModuleExists() throws Exception {
        when(moduleDecisionService.findOneById(testModuleDecisionEntity.getId()))
                .thenReturn(Optional.empty());

        mockMvc.perform(
                MockMvcRequestBuilders.get("/module-decisions/" + testModuleDecisionEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isNotFound()
        );
    }

    @Test
    public void testDeleteModuleDecision() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/module-decisions/" + testModuleDecisionEntity.getId())
        ).andExpect(
                MockMvcResultMatchers.status().isNoContent()
        );
    }

}
