package com.theodoremeras.dissertation.integration_tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.theodoremeras.dissertation.ParentCreationService;
import com.theodoremeras.dissertation.TestDataUtil;
import com.theodoremeras.dissertation.ec_application.EcApplicationEntity;
import com.theodoremeras.dissertation.ec_application.EcApplicationService;
import com.theodoremeras.dissertation.module_decision.ModuleDecisionDto;
import com.theodoremeras.dissertation.module_decision.ModuleDecisionEntity;
import com.theodoremeras.dissertation.module_decision.ModuleDecisionService;
import com.theodoremeras.dissertation.module_request.ModuleRequestEntity;
import com.theodoremeras.dissertation.user.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@WithMockUser(roles = {"Academic_Staff"})
public class ModuleDecisionControllerIntegrationTests {

    private ModuleDecisionService moduleDecisionService;

    private ParentCreationService parentCreationService;

    private EcApplicationService ecApplicationService;

    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @Autowired
    public ModuleDecisionControllerIntegrationTests(
            ModuleDecisionService moduleDecisionService, ParentCreationService parentCreationService,
            EcApplicationService ecApplicationService, ObjectMapper objectMapper, MockMvc mockMvc
    ) {
        this.moduleDecisionService = moduleDecisionService;
        this.parentCreationService = parentCreationService;
        this.ecApplicationService = ecApplicationService;
        this.objectMapper = objectMapper;
        this.mockMvc = mockMvc;
    }

    @Test
    public void testCreateModuleDecision() throws Exception {
        ModuleRequestEntity savedModuleRequest = parentCreationService.createModuleRequestParentEntity();
        UserEntity savedStaff = parentCreationService.createUserParentEntity();
        EcApplicationEntity savedEcApplication =
                ecApplicationService.findOneById(savedModuleRequest.getEcApplication().getId()).get();

        ModuleDecisionDto testModuleDecisionDto =
                TestDataUtil.createTestModuleDecisionDtoA(
                        savedModuleRequest.getId(),
                        savedStaff.getId(),
                        savedEcApplication.getId()
                );
        String moduleDecisionJson = objectMapper.writeValueAsString(testModuleDecisionDto);

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
                        .value(savedModuleRequest.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.staffMemberId")
                        .value(savedStaff.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.ecApplicationId")
                        .value(savedEcApplication.getId())
        );
    }

    @Test
    public void testCreateModuleDecisionWhenNoModuleRequestOrStaffOrEcApplicationIsSpecified() throws Exception {
        ModuleDecisionDto testModuleDecisionDto =
                TestDataUtil.createTestModuleDecisionDtoA(null, null, null);
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
        UserEntity savedStaff = parentCreationService.createUserParentEntity();
        EcApplicationEntity savedEcApplication = parentCreationService.createEcApplicationParentEntity();

        ModuleDecisionDto testModuleDecisionDto =
                TestDataUtil.createTestModuleDecisionDtoA(
                        1,
                        savedStaff.getId(),
                        savedEcApplication.getId()
                );
        String moduleDecisionJson = objectMapper.writeValueAsString(testModuleDecisionDto);

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
        ModuleRequestEntity savedModuleRequest = parentCreationService.createModuleRequestParentEntity();
        EcApplicationEntity savedEcApplication =
                ecApplicationService.findOneById(savedModuleRequest.getEcApplication().getId()).get();

        ModuleDecisionDto testModuleDecisionDto =
                TestDataUtil.createTestModuleDecisionDtoA(
                        savedModuleRequest.getId(),
                        2,
                        savedEcApplication.getId()
                );
        String moduleDecisionJson = objectMapper.writeValueAsString(testModuleDecisionDto);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/module-decisions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(moduleDecisionJson)
        ).andExpect(
                MockMvcResultMatchers.status().isNotFound()
        );
    }

    @Test
    public void testGetAllModuleDecisions() throws Exception {
        ModuleRequestEntity savedModuleRequest = parentCreationService.createModuleRequestParentEntity();
        UserEntity savedStaff = parentCreationService.createUserParentEntity();
        EcApplicationEntity savedEcApplication =
                ecApplicationService.findOneById(savedModuleRequest.getEcApplication().getId()).get();

        ModuleDecisionEntity testModuleDecisionEntityA =
                TestDataUtil.createTestModuleDecisionEntityA(savedModuleRequest, savedStaff, savedEcApplication);
        ModuleDecisionEntity savedModuleDecisionEntityA = moduleDecisionService.save(testModuleDecisionEntityA);
        ModuleDecisionEntity testModuleDecisionEntityB =
                TestDataUtil.createTestModuleDecisionEntityB(savedModuleRequest, savedStaff, savedEcApplication);
        ModuleDecisionEntity savedModuleDecisionEntityB = moduleDecisionService.save(testModuleDecisionEntityB);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/module-decisions")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].id")
                        .value(savedModuleDecisionEntityA.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].comments")
                        .value(savedModuleDecisionEntityA.getComments())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].isApproved")
                        .value(savedModuleDecisionEntityA.getIsApproved())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].moduleRequestId")
                        .value(savedModuleRequest.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].staffMemberId")
                        .value(savedStaff.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].ecApplicationId")
                        .value(savedEcApplication.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].id")
                        .value(savedModuleDecisionEntityB.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].comments")
                        .value(savedModuleDecisionEntityB.getComments())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].isApproved")
                        .value(savedModuleDecisionEntityB.getIsApproved())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].moduleRequestId")
                        .value(savedModuleRequest.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].staffMemberId")
                        .value(savedStaff.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].ecApplicationId")
                        .value(savedEcApplication.getId())
        );
    }

    @Test
    public void testGetAllModuleDecisionsByModuleRequestId() throws Exception {
        ModuleRequestEntity savedModuleRequest = parentCreationService.createModuleRequestParentEntity();
        UserEntity savedStaff = parentCreationService.createUserParentEntity();
        EcApplicationEntity savedEcApplication =
                ecApplicationService.findOneById(savedModuleRequest.getEcApplication().getId()).get();

        ModuleDecisionEntity testModuleDecisionEntityA =
                TestDataUtil.createTestModuleDecisionEntityA(savedModuleRequest, savedStaff, savedEcApplication);
        ModuleDecisionEntity savedModuleDecisionEntityA = moduleDecisionService.save(testModuleDecisionEntityA);
        ModuleDecisionEntity testModuleDecisionEntityB =
                TestDataUtil.createTestModuleDecisionEntityB(savedModuleRequest, savedStaff, savedEcApplication);
        ModuleDecisionEntity savedModuleDecisionEntityB = moduleDecisionService.save(testModuleDecisionEntityB);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/module-decisions?moduleRequestId=" + savedModuleRequest.getId())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].id")
                        .value(savedModuleDecisionEntityA.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].comments")
                        .value(savedModuleDecisionEntityA.getComments())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].isApproved")
                        .value(savedModuleDecisionEntityA.getIsApproved())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].moduleRequestId")
                        .value(savedModuleRequest.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].staffMemberId")
                        .value(savedStaff.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].ecApplicationId")
                        .value(savedEcApplication.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].id")
                        .value(savedModuleDecisionEntityB.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].comments")
                        .value(savedModuleDecisionEntityB.getComments())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].isApproved")
                        .value(savedModuleDecisionEntityB.getIsApproved())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].moduleRequestId")
                        .value(savedModuleRequest.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].staffMemberId")
                        .value(savedStaff.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].ecApplicationId")
                        .value(savedEcApplication.getId())
        );
    }

    @Test
    public void testGetAllModuleDecisionsByStaffMemberId() throws Exception {
        ModuleRequestEntity savedModuleRequest = parentCreationService.createModuleRequestParentEntity();
        UserEntity savedStaff = parentCreationService.createUserParentEntity();
        EcApplicationEntity savedEcApplication =
                ecApplicationService.findOneById(savedModuleRequest.getEcApplication().getId()).get();

        ModuleDecisionEntity testModuleDecisionEntityA =
                TestDataUtil.createTestModuleDecisionEntityA(savedModuleRequest, savedStaff, savedEcApplication);
        ModuleDecisionEntity savedModuleDecisionEntityA = moduleDecisionService.save(testModuleDecisionEntityA);
        ModuleDecisionEntity testModuleDecisionEntityB =
                TestDataUtil.createTestModuleDecisionEntityB(savedModuleRequest, savedStaff, savedEcApplication);
        ModuleDecisionEntity savedModuleDecisionEntityB = moduleDecisionService.save(testModuleDecisionEntityB);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/module-decisions?staffMemberId=" + savedStaff.getId())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].id")
                        .value(savedModuleDecisionEntityA.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].comments")
                        .value(savedModuleDecisionEntityA.getComments())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].isApproved")
                        .value(savedModuleDecisionEntityA.getIsApproved())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].moduleRequestId")
                        .value(savedModuleRequest.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].staffMemberId")
                        .value(savedStaff.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].ecApplicationId")
                        .value(savedEcApplication.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].id")
                        .value(savedModuleDecisionEntityB.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].comments")
                        .value(savedModuleDecisionEntityB.getComments())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].isApproved")
                        .value(savedModuleDecisionEntityB.getIsApproved())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].moduleRequestId")
                        .value(savedModuleRequest.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].staffMemberId")
                        .value(savedStaff.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].ecApplicationId")
                        .value(savedEcApplication.getId())
        );
    }

    @Test
    public void testGetAllModuleDecisionsByEcApplicationIds() throws Exception {
        ModuleRequestEntity savedModuleRequest = parentCreationService.createModuleRequestParentEntity();
        UserEntity savedStaff = parentCreationService.createUserParentEntity();
        EcApplicationEntity savedEcApplication =
                ecApplicationService.findOneById(savedModuleRequest.getEcApplication().getId()).get();

        ModuleDecisionEntity testModuleDecisionEntityA =
                TestDataUtil.createTestModuleDecisionEntityA(savedModuleRequest, savedStaff, savedEcApplication);
        ModuleDecisionEntity savedModuleDecisionEntityA = moduleDecisionService.save(testModuleDecisionEntityA);
        ModuleDecisionEntity testModuleDecisionEntityB =
                TestDataUtil.createTestModuleDecisionEntityB(savedModuleRequest, savedStaff, savedEcApplication);
        ModuleDecisionEntity savedModuleDecisionEntityB = moduleDecisionService.save(testModuleDecisionEntityB);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/module-decisions?ecApplicationIds=" + savedEcApplication.getId())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].id")
                        .value(savedModuleDecisionEntityA.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].comments")
                        .value(savedModuleDecisionEntityA.getComments())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].isApproved")
                        .value(savedModuleDecisionEntityA.getIsApproved())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].moduleRequestId")
                        .value(savedModuleRequest.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].staffMemberId")
                        .value(savedStaff.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].ecApplicationId")
                        .value(savedEcApplication.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].id")
                        .value(savedModuleDecisionEntityB.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].comments")
                        .value(savedModuleDecisionEntityB.getComments())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].isApproved")
                        .value(savedModuleDecisionEntityB.getIsApproved())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].moduleRequestId")
                        .value(savedModuleRequest.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].staffMemberId")
                        .value(savedStaff.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].ecApplicationId")
                        .value(savedEcApplication.getId())
        );
    }

    @Test
    public void testGetModuleDecisionById() throws Exception {
        ModuleRequestEntity savedModuleRequest = parentCreationService.createModuleRequestParentEntity();
        UserEntity savedStaff = parentCreationService.createUserParentEntity();
        EcApplicationEntity savedEcApplication =
                ecApplicationService.findOneById(savedModuleRequest.getEcApplication().getId()).get();


        ModuleDecisionEntity testModuleDecisionEntity =
                TestDataUtil.createTestModuleDecisionEntityA(savedModuleRequest, savedStaff, savedEcApplication);
        ModuleDecisionEntity savedModuleDecisionEntity = moduleDecisionService.save(testModuleDecisionEntity);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/module-decisions/" + savedModuleDecisionEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.id")
                        .isNumber()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.comments")
                        .value(savedModuleDecisionEntity.getComments())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.isApproved")
                        .value(savedModuleDecisionEntity.getIsApproved())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.moduleRequestId")
                        .value(savedModuleRequest.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.staffMemberId")
                        .value(savedStaff.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.ecApplicationId")
                        .value(savedEcApplication.getId())
        );
    }

    @Test
    public void testGetModuleDecisionByIdWhenNoModuleDecisionExists() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/module-decisions/1")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isNotFound()
        );
    }

    @Test
    @WithMockUser(roles = {"Administrator"})
    public void testDeleteModuleDecision() throws Exception {
        ModuleRequestEntity savedModuleRequest = parentCreationService.createModuleRequestParentEntity();
        UserEntity savedStaff = parentCreationService.createUserParentEntity();
        EcApplicationEntity savedEcApplication =
                ecApplicationService.findOneById(savedModuleRequest.getEcApplication().getId()).get();


        ModuleDecisionEntity testModuleDecisionEntity =
                TestDataUtil.createTestModuleDecisionEntityA(savedModuleRequest, savedStaff, savedEcApplication);
        ModuleDecisionEntity savedModuleDecisionEntity = moduleDecisionService.save(testModuleDecisionEntity);

        mockMvc.perform(
                MockMvcRequestBuilders.delete("/module-decisions/" + savedModuleDecisionEntity.getId())
        ).andExpect(
                MockMvcResultMatchers.status().isNoContent()
        );
    }

    @Test
    @WithMockUser(roles = {"Administrator"})
    public void testDeleteModuleDecisionWhenNoModuleDecisionExists() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/module-decisions/1")
        ).andExpect(
                MockMvcResultMatchers.status().isNoContent()
        );
    }

}
