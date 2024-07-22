package com.theodoremeras.dissertation.integration_tests;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.theodoremeras.dissertation.TestDataUtil;
import com.theodoremeras.dissertation.department.DepartmentEntity;
import com.theodoremeras.dissertation.department.DepartmentService;
import com.theodoremeras.dissertation.ec_application.EcApplicationEntity;
import com.theodoremeras.dissertation.ec_application.EcApplicationService;
import com.theodoremeras.dissertation.module.ModuleEntity;
import com.theodoremeras.dissertation.module.ModuleService;
import com.theodoremeras.dissertation.module_outcome_request.ModuleOutcomeRequestDto;
import com.theodoremeras.dissertation.module_outcome_request.ModuleOutcomeRequestEntity;
import com.theodoremeras.dissertation.module_outcome_request.ModuleOutcomeRequestService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class ModuleOutcomeRequestIntegrationTests {

    private ModuleOutcomeRequestService moduleOutcomeRequestService;

    private ModuleService moduleService;

    private DepartmentService departmentService;

    private EcApplicationService ecApplicationService;

    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @Autowired
    public ModuleOutcomeRequestIntegrationTests(
            ModuleOutcomeRequestService moduleOutcomeRequestService, ModuleService moduleService,
            DepartmentService departmentService, EcApplicationService ecApplicationService,
            MockMvc mockMvc, ObjectMapper objectMapper)
    {
        this.moduleOutcomeRequestService = moduleOutcomeRequestService;
        this.moduleService = moduleService;
        this.departmentService = departmentService;
        this.ecApplicationService = ecApplicationService;
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    public EcApplicationEntity saveEcApplicationParentEntity() {
        return ecApplicationService.save(TestDataUtil.createTestEcApplicationEntityA());
    }

    public ModuleEntity saveModuleParentEntity() {
        DepartmentEntity savedDepartmentEntity = departmentService.save(TestDataUtil.createTestDepartmentEntityA());
        return moduleService.save(TestDataUtil.createTestModuleEntityA(savedDepartmentEntity));
    }

    @Test
    public void testCreateModuleOutcomeRequest() throws Exception {
        EcApplicationEntity savedEcApplication = saveEcApplicationParentEntity();
        ModuleEntity savedModule = saveModuleParentEntity();

        ModuleOutcomeRequestDto testRequestDto =
                TestDataUtil.createTestRequestDtoA(savedEcApplication.getId(), savedModule.getCode());
        String requestJson = objectMapper.writeValueAsString(testRequestDto);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/module-requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
        ).andExpect(
                MockMvcResultMatchers.status().isCreated()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.requestedOutcome")
                        .value(testRequestDto.getRequestedOutcome())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.ecApplicationId")
                        .value(savedEcApplication.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.moduleCode")
                        .value(savedModule.getCode())
        );
    }

    @Test
    public void testCreateModuleOutcomeRequestWhenNoApplicationOrModuleIsSpecified() throws Exception {
        ModuleOutcomeRequestDto testRequestDto =
                TestDataUtil.createTestRequestDtoA(null,null);
        String requestJson = objectMapper.writeValueAsString(testRequestDto);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/module-requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
        ).andExpect(
                MockMvcResultMatchers.status().isBadRequest()
        );
    }

    @Test
    public void testCreateModuleOutcomeRequestWhenEcApplicationIsNotFound() throws Exception {
        ModuleEntity savedModule = saveModuleParentEntity();

        ModuleOutcomeRequestDto testRequestDto =
                TestDataUtil.createTestRequestDtoA(1,savedModule.getCode());
        String requestJson = objectMapper.writeValueAsString(testRequestDto);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/module-requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
        ).andExpect(
                MockMvcResultMatchers.status().isNotFound()
        );
    }

    @Test
    public void testCreateModuleOutcomeRequestWhenModuleIsNotFound() throws Exception {
        EcApplicationEntity savedEcApplication = saveEcApplicationParentEntity();

        ModuleOutcomeRequestDto testRequestDto =
                TestDataUtil.createTestRequestDtoA(savedEcApplication.getId(),"COM123");
        String requestJson = objectMapper.writeValueAsString(testRequestDto);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/module-requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
        ).andExpect(
                MockMvcResultMatchers.status().isNotFound()
        );
    }

    @Test
    public void testGetAllModuleOutcomeRequests() throws Exception {
        EcApplicationEntity savedEcApplication = saveEcApplicationParentEntity();
        ModuleEntity savedModule = saveModuleParentEntity();

        ModuleOutcomeRequestEntity testRequestEntityA =
                TestDataUtil.createTestRequestEntityA(savedEcApplication, savedModule);
        ModuleOutcomeRequestEntity savedRequestEntityA = moduleOutcomeRequestService.save(testRequestEntityA);
        ModuleOutcomeRequestEntity testRequestEntityB =
                TestDataUtil.createTestRequestEntityB(savedEcApplication, savedModule);
        ModuleOutcomeRequestEntity savedRequestEntityB = moduleOutcomeRequestService.save(testRequestEntityB);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/module-requests")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].id")
                        .value(savedRequestEntityA.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].requestedOutcome")
                        .value(savedRequestEntityA.getRequestedOutcome())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].id")
                        .value(savedRequestEntityB.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].requestedOutcome")
                        .value(savedRequestEntityB.getRequestedOutcome())
        );
    }

    @Test
    public void testGetAllModuleOutcomeRequestsByEcApplicationId() throws Exception {
        EcApplicationEntity savedEcApplication = saveEcApplicationParentEntity();
        ModuleEntity savedModule = saveModuleParentEntity();

        ModuleOutcomeRequestEntity testRequestEntityA =
                TestDataUtil.createTestRequestEntityA(savedEcApplication, savedModule);
        ModuleOutcomeRequestEntity savedRequestEntityA = moduleOutcomeRequestService.save(testRequestEntityA);
        ModuleOutcomeRequestEntity testRequestEntityB =
                TestDataUtil.createTestRequestEntityB(savedEcApplication, savedModule);
          ModuleOutcomeRequestEntity savedRequestEntityB = moduleOutcomeRequestService.save(testRequestEntityB);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/module-requests?ecApplicationId=" + savedEcApplication.getId())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].id")
                        .value(savedRequestEntityA.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].requestedOutcome")
                        .value(savedRequestEntityA.getRequestedOutcome())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].id")
                        .value(savedRequestEntityB.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].requestedOutcome")
                        .value(savedRequestEntityB.getRequestedOutcome())
        );
    }

    @Test
    public void testGetModuleOutcomeRequestWhenRequestExists() throws Exception {
        EcApplicationEntity savedEcApplication = saveEcApplicationParentEntity();
        ModuleEntity savedModule = saveModuleParentEntity();

        ModuleOutcomeRequestEntity testRequestEntity =
                TestDataUtil.createTestRequestEntityA(savedEcApplication, savedModule);
        ModuleOutcomeRequestEntity savedRequestEntity =moduleOutcomeRequestService.save(testRequestEntity);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/module-requests/" + testRequestEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.id")
                        .value(savedRequestEntity.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.requestedOutcome")
                        .value(savedRequestEntity.getRequestedOutcome())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.ecApplicationId")
                        .value(savedEcApplication.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.moduleCode")
                        .value(savedModule.getCode())
        );
    }

    @Test
    public void testGetModuleOutcomeRequestWhenNoRequestExists() throws Exception {

        mockMvc.perform(
                MockMvcRequestBuilders.get("/module-requests/1")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isNotFound()
        );
    }

    @Test
    public void testPartialUpdateModuleOutcomeRequest() throws Exception {
        EcApplicationEntity savedEcApplication = saveEcApplicationParentEntity();
        ModuleEntity savedModule = saveModuleParentEntity();

        ModuleOutcomeRequestEntity testRequestEntity =
                TestDataUtil.createTestRequestEntityA(savedEcApplication, savedModule);
        ModuleOutcomeRequestEntity savedRequestEntity =moduleOutcomeRequestService.save(testRequestEntity);

        ModuleOutcomeRequestDto testRequestDto =
                TestDataUtil.createTestRequestDtoB(null, null);
        String requestUpdateJson = objectMapper.writeValueAsString(testRequestDto);

        mockMvc.perform(
                MockMvcRequestBuilders.patch("/module-requests/" + savedRequestEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestUpdateJson)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.id")
                        .value(savedRequestEntity.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.requestedOutcome")
                        .value(testRequestDto.getRequestedOutcome())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.ecApplicationId")
                        .value(savedEcApplication.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.moduleCode")
                        .value(savedModule.getCode())
        );
    }

    @Test
    public void testPartialUpdateModuleOutcomeRequestWhenNoRequestExists() throws Exception {
        ModuleOutcomeRequestDto testRequestDto =
                TestDataUtil.createTestRequestDtoB(null, null);
        String requestUpdateJson = objectMapper.writeValueAsString(testRequestDto);

        mockMvc.perform(
                MockMvcRequestBuilders.patch("/module-requests/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestUpdateJson)
        ).andExpect(
                MockMvcResultMatchers.status().isNotFound()
        );
    }


    @Test
    public void testDeleteModuleOutcomeRequest() throws Exception {
        EcApplicationEntity savedEcApplication = saveEcApplicationParentEntity();
        ModuleEntity savedModule = saveModuleParentEntity();

        ModuleOutcomeRequestEntity testRequestEntity =
                TestDataUtil.createTestRequestEntityA(savedEcApplication, savedModule);
        ModuleOutcomeRequestEntity savedRequestEntity =moduleOutcomeRequestService.save(testRequestEntity);

        mockMvc.perform(
                MockMvcRequestBuilders.delete("/module-requests/" + savedRequestEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isNoContent()
        );
    }

    @Test
    public void testDeleteModuleOutcomeRequestWhenNoRequestExists() throws Exception {

        mockMvc.perform(
                MockMvcRequestBuilders.delete("/module-requests/1")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isNoContent()
        );
    }

}
