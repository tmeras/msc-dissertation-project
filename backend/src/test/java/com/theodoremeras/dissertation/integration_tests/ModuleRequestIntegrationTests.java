package com.theodoremeras.dissertation.integration_tests;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.theodoremeras.dissertation.TestDataUtil;
import com.theodoremeras.dissertation.department.DepartmentEntity;
import com.theodoremeras.dissertation.department.DepartmentService;
import com.theodoremeras.dissertation.ec_application.EcApplicationEntity;
import com.theodoremeras.dissertation.ec_application.EcApplicationService;
import com.theodoremeras.dissertation.module.ModuleEntity;
import com.theodoremeras.dissertation.module.ModuleService;
import com.theodoremeras.dissertation.module_outcome_request.ModuleRequestDto;
import com.theodoremeras.dissertation.module_outcome_request.ModuleRequestEntity;
import com.theodoremeras.dissertation.module_outcome_request.ModuleRequestService;
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
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class ModuleRequestIntegrationTests {

    private ModuleRequestService moduleRequestService;

    private ModuleService moduleService;

    private DepartmentService departmentService;

    private EcApplicationService ecApplicationService;

    private RoleService roleService;

    private UserService userService;

    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @Autowired
    public ModuleRequestIntegrationTests(
            ModuleRequestService moduleRequestService, ModuleService moduleService,
            DepartmentService departmentService, EcApplicationService ecApplicationService, RoleService roleService,
            UserService userService, MockMvc mockMvc, ObjectMapper objectMapper)
    {
        this.moduleRequestService = moduleRequestService;
        this.moduleService = moduleService;
        this.departmentService = departmentService;
        this.ecApplicationService = ecApplicationService;
        this.roleService = roleService;
        this.userService = userService;
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    public DepartmentEntity saveDepartmentParentEntity() {
        return departmentService.save(TestDataUtil.createTestDepartmentEntityA());
    }

    public EcApplicationEntity saveEcApplicationParentEntity(DepartmentEntity department) {
        RoleEntity role = roleService.save(TestDataUtil.createTestRoleEntityA());
        UserEntity student = userService.save(TestDataUtil.createTestUserEntityA(role, department));
        return ecApplicationService.save(TestDataUtil.createTestEcApplicationEntityA(student));
    }

    public ModuleEntity saveModuleParentEntity(DepartmentEntity department) {
        return moduleService.save(TestDataUtil.createTestModuleEntityA(department));
    }

    @Test
    public void testCreateModuleRequest() throws Exception {
        DepartmentEntity department = saveDepartmentParentEntity();
        EcApplicationEntity savedEcApplication = saveEcApplicationParentEntity(department);
        ModuleEntity savedModule = saveModuleParentEntity(department);

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
        DepartmentEntity department = saveDepartmentParentEntity();
        ModuleEntity savedModule = saveModuleParentEntity(department);

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
        DepartmentEntity department = saveDepartmentParentEntity();
        EcApplicationEntity savedEcApplication = saveEcApplicationParentEntity(department);

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
        DepartmentEntity department = saveDepartmentParentEntity();
        EcApplicationEntity savedEcApplication = saveEcApplicationParentEntity(department);
        ModuleEntity savedModule = saveModuleParentEntity(department);

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
    public void testGetAllModuleRequestsByEcApplicationId() throws Exception {
        DepartmentEntity department = saveDepartmentParentEntity();
        EcApplicationEntity savedEcApplication = saveEcApplicationParentEntity(department);
        ModuleEntity savedModule = saveModuleParentEntity(department);

        ModuleRequestEntity testRequestEntityA =
                TestDataUtil.createTestRequestEntityA(savedEcApplication, savedModule);
        ModuleRequestEntity savedRequestEntityA = moduleRequestService.save(testRequestEntityA);
        ModuleRequestEntity testRequestEntityB =
                TestDataUtil.createTestRequestEntityB(savedEcApplication, savedModule);
          ModuleRequestEntity savedRequestEntityB = moduleRequestService.save(testRequestEntityB);

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
        DepartmentEntity department = saveDepartmentParentEntity();
        EcApplicationEntity savedEcApplication = saveEcApplicationParentEntity(department);
        ModuleEntity savedModule = saveModuleParentEntity(department);

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
        DepartmentEntity department = saveDepartmentParentEntity();
        EcApplicationEntity savedEcApplication = saveEcApplicationParentEntity(department);
        ModuleEntity savedModule = saveModuleParentEntity(department);

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
        DepartmentEntity department = saveDepartmentParentEntity();
        EcApplicationEntity savedEcApplication = saveEcApplicationParentEntity(department);
        ModuleEntity savedModule = saveModuleParentEntity(department);

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
