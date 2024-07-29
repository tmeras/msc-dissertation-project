package com.theodoremeras.dissertation.integration_tests;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.theodoremeras.dissertation.ParentCreationService;
import com.theodoremeras.dissertation.TestDataUtil;
import com.theodoremeras.dissertation.department.DepartmentEntity;
import com.theodoremeras.dissertation.department.DepartmentService;
import com.theodoremeras.dissertation.ec_application.EcApplicationEntity;
import com.theodoremeras.dissertation.ec_application.EcApplicationService;
import com.theodoremeras.dissertation.module.ModuleEntity;
import com.theodoremeras.dissertation.module.ModuleService;
import com.theodoremeras.dissertation.module_request.ModuleRequestDto;
import com.theodoremeras.dissertation.module_request.ModuleRequestEntity;
import com.theodoremeras.dissertation.module_request.ModuleRequestService;
import com.theodoremeras.dissertation.role.RoleEntity;
import com.theodoremeras.dissertation.role.RoleService;
import com.theodoremeras.dissertation.user.UserEntity;
import com.theodoremeras.dissertation.user.UserService;
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
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class ModuleRequestControllerIntegrationTests {

    private ModuleRequestService moduleRequestService;

    private ParentCreationService parentCreationService;

    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @Autowired
    public ModuleRequestControllerIntegrationTests(
            ModuleRequestService moduleRequestService, ParentCreationService parentCreationService,
            MockMvc mockMvc, ObjectMapper objectMapper)
    {
        this.moduleRequestService = moduleRequestService;
        this.parentCreationService = parentCreationService;
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @Test
    public void testCreateModuleRequest() throws Exception {
        EcApplicationEntity savedEcApplication = parentCreationService.createEcApplicationParentEntity();
        ModuleEntity savedModule = parentCreationService.createModuleParentEntity();

        ModuleRequestDto testRequestDto =
                TestDataUtil.createTestRequestDtoA(savedEcApplication.getId(), savedModule.getCode());
        String requestJson = objectMapper.writeValueAsString(testRequestDto);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/module-requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
        ).andExpect(
                MockMvcResultMatchers.status().isCreated()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.id")
                        .isNumber()
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
    public void testCreateModuleRequestWhenNoApplicationOrModuleIsSpecified() throws Exception {
        ModuleRequestDto testRequestDto =
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
    public void testCreateModuleRequestWhenNoEcApplicationExists() throws Exception {
        ModuleEntity savedModule = parentCreationService.createModuleParentEntity();

        ModuleRequestDto testRequestDto =
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
    public void testCreateModuleRequestWhenNoModuleExists() throws Exception {
        EcApplicationEntity savedEcApplication = parentCreationService.createEcApplicationParentEntity();

        ModuleRequestDto testRequestDto =
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
    public void testGetAllModuleRequests() throws Exception {
        EcApplicationEntity savedEcApplication = parentCreationService.createEcApplicationParentEntity();
        ModuleEntity savedModule = parentCreationService.createModuleParentEntity();

        ModuleRequestEntity testRequestEntityA =
                TestDataUtil.createTestRequestEntityA(savedEcApplication, savedModule);
        ModuleRequestEntity savedRequestEntityA = moduleRequestService.save(testRequestEntityA);
        ModuleRequestEntity testRequestEntityB =
                TestDataUtil.createTestRequestEntityB(savedEcApplication, savedModule);
        ModuleRequestEntity savedRequestEntityB = moduleRequestService.save(testRequestEntityB);

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
                MockMvcResultMatchers.jsonPath("$[0].ecApplicationId")
                        .value(savedEcApplication.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].moduleCode")
                        .value(savedModule.getCode())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].id")
                        .value(savedRequestEntityB.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].requestedOutcome")
                        .value(savedRequestEntityB.getRequestedOutcome())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].ecApplicationId")
                        .value(savedEcApplication.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].moduleCode")
                        .value(savedModule.getCode())
        );
    }

    @Test
    public void testGetAllModuleRequestsByEcApplicationIds() throws Exception {
        EcApplicationEntity savedEcApplication = parentCreationService.createEcApplicationParentEntity();
        ModuleEntity savedModule = parentCreationService.createModuleParentEntity();

        ModuleRequestEntity testRequestEntityA =
                TestDataUtil.createTestRequestEntityA(savedEcApplication, savedModule);
        ModuleRequestEntity savedRequestEntityA = moduleRequestService.save(testRequestEntityA);
        ModuleRequestEntity testRequestEntityB =
                TestDataUtil.createTestRequestEntityB(savedEcApplication, savedModule);
          ModuleRequestEntity savedRequestEntityB = moduleRequestService.save(testRequestEntityB);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/module-requests?ecApplicationIds=" + savedEcApplication.getId())
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
                MockMvcResultMatchers.jsonPath("$[0].ecApplicationId")
                        .value(savedEcApplication.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].moduleCode")
                        .value(savedModule.getCode())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].id")
                        .value(savedRequestEntityB.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].requestedOutcome")
                        .value(savedRequestEntityB.getRequestedOutcome())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].ecApplicationId")
                        .value(savedEcApplication.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].moduleCode")
                        .value(savedModule.getCode())
        );
    }

    @Test
    public void testGetModuleRequestById() throws Exception {
        EcApplicationEntity savedEcApplication = parentCreationService.createEcApplicationParentEntity();
        ModuleEntity savedModule = parentCreationService.createModuleParentEntity();

        ModuleRequestEntity testRequestEntity =
                TestDataUtil.createTestRequestEntityA(savedEcApplication, savedModule);
        ModuleRequestEntity savedRequestEntity = moduleRequestService.save(testRequestEntity);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/module-requests/" + savedRequestEntity.getId())
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
    public void testGetModuleRequestByIdWhenNoRequestExists() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/module-requests/1")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isNotFound()
        );
    }

    @Test
    public void testPartialUpdateModuleRequest() throws Exception {
        EcApplicationEntity savedEcApplication = parentCreationService.createEcApplicationParentEntity();
        ModuleEntity savedModule = parentCreationService.createModuleParentEntity();

        ModuleRequestEntity testRequestEntity =
                TestDataUtil.createTestRequestEntityA(savedEcApplication, savedModule);
        ModuleRequestEntity savedRequestEntity = moduleRequestService.save(testRequestEntity);

        ModuleRequestDto testRequestDto =
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
    public void testPartialUpdateModuleRequestWhenNoRequestExists() throws Exception {
        ModuleRequestDto testRequestDto =
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
    public void testDeleteModuleRequest() throws Exception {
        EcApplicationEntity savedEcApplication = parentCreationService.createEcApplicationParentEntity();
        ModuleEntity savedModule = parentCreationService.createModuleParentEntity();

        ModuleRequestEntity testRequestEntity =
                TestDataUtil.createTestRequestEntityA(savedEcApplication, savedModule);
        ModuleRequestEntity savedRequestEntity = moduleRequestService.save(testRequestEntity);

        mockMvc.perform(
                MockMvcRequestBuilders.delete("/module-requests/" + savedRequestEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isNoContent()
        );
    }

    @Test
    public void testDeleteModuleRequestWhenNoRequestExists() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/module-requests/1")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isNoContent()
        );
    }

}
