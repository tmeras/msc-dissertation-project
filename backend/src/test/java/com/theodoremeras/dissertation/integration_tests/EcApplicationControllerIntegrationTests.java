package com.theodoremeras.dissertation.integration_tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.theodoremeras.dissertation.TestDataUtil;
import com.theodoremeras.dissertation.department.DepartmentEntity;
import com.theodoremeras.dissertation.department.DepartmentService;
import com.theodoremeras.dissertation.ec_application.EcApplicationDto;
import com.theodoremeras.dissertation.ec_application.EcApplicationEntity;
import com.theodoremeras.dissertation.ec_application.EcApplicationService;
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
public class EcApplicationControllerIntegrationTests {

    private EcApplicationService ecApplicationService;

    private UserService userService;

    private RoleService roleService;

    private DepartmentService departmentService;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;


    @Autowired
    public EcApplicationControllerIntegrationTests(
            EcApplicationService ecApplicationService, UserService userService,
            RoleService roleService, DepartmentService departmentService,
            MockMvc mockMvc, ObjectMapper objectMapper
    ) {
        this.ecApplicationService = ecApplicationService;
        this.userService = userService;
        this.roleService = roleService;
        this.departmentService = departmentService;
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    public UserEntity saveUserParentEntity() {
        RoleEntity role = roleService.save(TestDataUtil.createTestRoleEntityA());
        DepartmentEntity department = departmentService.save(TestDataUtil.createTestDepartmentEntityA());
        return userService.save(TestDataUtil.createTestUserEntityA(role, department));
    }

    @Test
    public void testCreateEcApplication() throws Exception {
        UserEntity savedUser = saveUserParentEntity();

        EcApplicationDto testEcApplicationDto = TestDataUtil.createTestEcApplicationDtoA(savedUser.getId());
        String applicationJson = objectMapper.writeValueAsString(testEcApplicationDto);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/ec-applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(applicationJson)
        ).andExpect(
                MockMvcResultMatchers.status().isCreated()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.id").isNumber()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.circumstancesDetails")
                        .value(testEcApplicationDto.getCircumstancesDetails())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.additionalDetails")
                        .value(testEcApplicationDto.getAdditionalDetails())
        );
    }

    @Test
    public void testGetAllEcApplications() throws Exception {
        UserEntity savedUser = saveUserParentEntity();

        EcApplicationEntity testEcApplicationA = TestDataUtil.createTestEcApplicationEntityA(savedUser);
        EcApplicationEntity savedEcApplicationA = ecApplicationService.save(testEcApplicationA);
        EcApplicationEntity testEcApplicationB = TestDataUtil.createTestEcApplicationEntityB(savedUser);
        EcApplicationEntity savedEcApplicationB = ecApplicationService.save(testEcApplicationB);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/ec-applications")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].id").value(savedEcApplicationA.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].circumstancesDetails")
                        .value(savedEcApplicationA.getCircumstancesDetails())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].additionalDetails")
                        .value(savedEcApplicationA.getAdditionalDetails())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].isReferred")
                        .value(savedEcApplicationA.getIsReferred())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].id").value(savedEcApplicationB.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].circumstancesDetails")
                        .value(savedEcApplicationB.getCircumstancesDetails())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].additionalDetails")
                        .value(savedEcApplicationB.getAdditionalDetails())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].isReferred")
                        .value(savedEcApplicationB.getIsReferred())
        );
    }

    @Test
    public void testGetEcApplicationById() throws Exception {
        UserEntity savedUser = saveUserParentEntity();

        EcApplicationEntity testEcApplication = TestDataUtil.createTestEcApplicationEntityA(savedUser);
        EcApplicationEntity savedEcApplication = ecApplicationService.save(testEcApplication);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/ec-applications/" + savedEcApplication.getId())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.id").value(savedEcApplication.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.circumstancesDetails")
                        .value(savedEcApplication.getCircumstancesDetails())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.additionalDetails")
                        .value(savedEcApplication.getAdditionalDetails())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.isReferred")
                        .value(savedEcApplication.getIsReferred())
        );
    }

    @Test
    public void testGetEcApplicationByIdWhenNoApplicationExists() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/ec-applications/123")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isNotFound()
        );
    }

    @Test
    public void testPartialUpdateEcApplication() throws Exception {
        UserEntity savedUser = saveUserParentEntity();

        EcApplicationEntity testEcApplication = TestDataUtil.createTestEcApplicationEntityA(savedUser);
        EcApplicationEntity savedEcApplication = ecApplicationService.save(testEcApplication);

        EcApplicationDto testEcApplicationDto = TestDataUtil.createTestEcApplicationDtoB(null);
        testEcApplicationDto.setCircumstancesDetails(testEcApplication.getCircumstancesDetails());
        String applicationUpdateJson = objectMapper.writeValueAsString(testEcApplicationDto);

        mockMvc.perform(
                MockMvcRequestBuilders.patch("/ec-applications/" + savedEcApplication.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(applicationUpdateJson)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.circumstancesDetails")
                        .value(testEcApplication.getCircumstancesDetails())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.additionalDetails")
                        .value(testEcApplicationDto.getAdditionalDetails())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.isReferred")
                        .value(testEcApplicationDto.getIsReferred())
        );
    }

    @Test
    public void testPartialUpdateEcApplicationWhenNoApplicationExists() throws Exception {
        EcApplicationDto testEcApplicationDto = TestDataUtil.createTestEcApplicationDtoA(null);
        String applicationUpdateJson = objectMapper.writeValueAsString(testEcApplicationDto);

        mockMvc.perform(
                MockMvcRequestBuilders.patch("/ec-applications/123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(applicationUpdateJson)
        ).andExpect(
                MockMvcResultMatchers.status().isNotFound()
        );
    }

    @Test
    public void testDeleteEcApplication() throws Exception {
        UserEntity savedUser = saveUserParentEntity();

        EcApplicationEntity testEcApplication = TestDataUtil.createTestEcApplicationEntityA(savedUser);
        EcApplicationEntity savedEcApplication = ecApplicationService.save(testEcApplication);

        mockMvc.perform(
                MockMvcRequestBuilders.delete("/ec-applications/" + savedEcApplication.getId())
                            .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isNoContent()
        );
    }

    @Test
    public void testDeleteEcApplicationWhenNoApplicationExists() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/ec-applications/123")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isNoContent()
        );
    }



}
