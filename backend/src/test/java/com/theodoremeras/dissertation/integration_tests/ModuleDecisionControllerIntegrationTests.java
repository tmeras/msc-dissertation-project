package com.theodoremeras.dissertation.integration_tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.theodoremeras.dissertation.TestDataUtil;
import com.theodoremeras.dissertation.department.DepartmentEntity;
import com.theodoremeras.dissertation.department.DepartmentService;
import com.theodoremeras.dissertation.ec_application.EcApplicationEntity;
import com.theodoremeras.dissertation.ec_application.EcApplicationService;
import com.theodoremeras.dissertation.module.ModuleEntity;
import com.theodoremeras.dissertation.module.ModuleRepository;
import com.theodoremeras.dissertation.module.ModuleService;
import com.theodoremeras.dissertation.module_decision.ModuleDecisionDto;
import com.theodoremeras.dissertation.module_decision.ModuleDecisionEntity;
import com.theodoremeras.dissertation.module_decision.ModuleDecisionService;
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
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class ModuleDecisionControllerIntegrationTests {

    private ModuleDecisionService moduleDecisionService;

    private ModuleRequestService moduleRequestService;

    private ModuleService moduleService;

    private EcApplicationService ecApplicationService;

    private UserService userService;

    private DepartmentService departmentService;

    private RoleService roleService;

    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @Autowired
    public ModuleDecisionControllerIntegrationTests(
            ModuleDecisionService moduleDecisionService, ModuleRequestService moduleRequestService,
            ModuleService moduleService, EcApplicationService ecApplicationService,
            UserService userService, DepartmentService departmentService, RoleService roleService,
            ObjectMapper objectMapper, MockMvc mockMvc
    ) {
        this.moduleDecisionService = moduleDecisionService;
        this.moduleRequestService = moduleRequestService;
        this.moduleService = moduleService;
        this.ecApplicationService = ecApplicationService;
        this.userService = userService;
        this.departmentService = departmentService;
        this.roleService = roleService;
        this.objectMapper = objectMapper;
        this.mockMvc = mockMvc;
    }

    public DepartmentEntity saveDepartmentParentEntity() {
        return departmentService.save(TestDataUtil.createTestDepartmentEntityA());
    }

    public ModuleRequestEntity saveModuleRequestParentEntity(DepartmentEntity department) {
        RoleEntity role = roleService.save(TestDataUtil.createTestRoleEntityA());
        UserEntity student = userService.save(TestDataUtil.createTestUserEntityA(role, department));
        EcApplicationEntity ecApplication =
                ecApplicationService.save(TestDataUtil.createTestEcApplicationEntityA(student));
        ModuleEntity module = moduleService.save(TestDataUtil.createTestModuleEntityA(department));
        return moduleRequestService.save(TestDataUtil.createTestRequestEntityA(ecApplication, module));
    }

    public UserEntity saveUserParentEntity() {
        RoleEntity role = roleService.save(TestDataUtil.createTestRoleEntityA());
        DepartmentEntity department = departmentService.save(TestDataUtil.createTestDepartmentEntityA());
        return userService.save(TestDataUtil.createTestUserEntityA(role, department));
    }

    @Test
    public void testCreateModuleDecision() throws Exception {
        DepartmentEntity department = saveDepartmentParentEntity();
        ModuleRequestEntity savedModuleRequest = saveModuleRequestParentEntity(department);
        UserEntity savedStaff = saveUserParentEntity();

        ModuleDecisionDto testModuleDecisionDto =
                TestDataUtil.createTestModuleDecisionDtoA(savedModuleRequest.getId(), savedStaff.getId());
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
                        .value(testModuleDecisionDto.getModuleRequestId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.staffMemberId")
                        .value(testModuleDecisionDto.getStaffMemberId())
        );
    }

    @Test
    public void testCreateModuleDecisionWhenNoModuleRequestOrStaffIsSpecified() throws Exception {
        ModuleDecisionDto testModuleDecisionDto =
                TestDataUtil.createTestModuleDecisionDtoA(null, null);
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
        UserEntity savedStaff = saveUserParentEntity();

        ModuleDecisionDto testModuleDecisionDto =
                TestDataUtil.createTestModuleDecisionDtoA(1, savedStaff.getId());
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
        DepartmentEntity department = saveDepartmentParentEntity();
        ModuleRequestEntity savedModuleRequest = saveModuleRequestParentEntity(department);

        ModuleDecisionDto testModuleDecisionDto =
                TestDataUtil.createTestModuleDecisionDtoA(savedModuleRequest.getId(), 2);
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
    public void testGetAllModuleDecisions() throws  Exception {
        DepartmentEntity department = saveDepartmentParentEntity();
        ModuleRequestEntity savedModuleRequest = saveModuleRequestParentEntity(department);
        UserEntity savedStaff = saveUserParentEntity();

        ModuleDecisionEntity testModuleDecisionEntityA =
                TestDataUtil.createTestModuleDecisionEntityA(savedModuleRequest, savedStaff);
        ModuleDecisionEntity savedModuleDecisionEntityA = moduleDecisionService.save(testModuleDecisionEntityA);
        ModuleDecisionEntity testModuleDecisionEntityB =
                TestDataUtil.createTestModuleDecisionEntityB(savedModuleRequest, savedStaff);
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
        );
    }

    @Test
    public void testGetAllModuleDecisionsByModuleRequestId() throws  Exception {
        DepartmentEntity department = saveDepartmentParentEntity();
        ModuleRequestEntity savedModuleRequest = saveModuleRequestParentEntity(department);
        UserEntity savedStaff = saveUserParentEntity();

        ModuleDecisionEntity testModuleDecisionEntityA =
                TestDataUtil.createTestModuleDecisionEntityA(savedModuleRequest, savedStaff);
        ModuleDecisionEntity savedModuleDecisionEntityA = moduleDecisionService.save(testModuleDecisionEntityA);
        ModuleDecisionEntity testModuleDecisionEntityB =
                TestDataUtil.createTestModuleDecisionEntityB(savedModuleRequest, savedStaff);
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
        );
    }

    @Test
    public void testGetModuleDecisionById() throws  Exception {
        DepartmentEntity department = saveDepartmentParentEntity();
        ModuleRequestEntity savedModuleRequest = saveModuleRequestParentEntity(department);
        UserEntity savedStaff = saveUserParentEntity();

        ModuleDecisionEntity testModuleDecisionEntity =
                TestDataUtil.createTestModuleDecisionEntityA(savedModuleRequest, savedStaff);
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
        );
    }

    @Test
    public void testGetModuleDecisionByIdWhenNoModuleDecisionExists() throws  Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/module-decisions/1")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isNotFound()
        );
    }

    @Test
    public void testDeleteModuleDecision() throws  Exception {
        DepartmentEntity department = saveDepartmentParentEntity();
        ModuleRequestEntity savedModuleRequest = saveModuleRequestParentEntity(department);
        UserEntity savedStaff = saveUserParentEntity();

        ModuleDecisionEntity testModuleDecisionEntity =
                TestDataUtil.createTestModuleDecisionEntityA(savedModuleRequest, savedStaff);
        ModuleDecisionEntity savedModuleDecisionEntity = moduleDecisionService.save(testModuleDecisionEntity);

        mockMvc.perform(
                MockMvcRequestBuilders.delete("/module-decisions/" + savedModuleDecisionEntity.getId())
        ).andExpect(
                MockMvcResultMatchers.status().isNoContent()
        );
    }

   @Test
    public void testDeleteModuleDecisionWhenNoModuleDecisionExists() throws  Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/module-decisions/1")
        ).andExpect(
                MockMvcResultMatchers.status().isNoContent()
        );
    }

}
