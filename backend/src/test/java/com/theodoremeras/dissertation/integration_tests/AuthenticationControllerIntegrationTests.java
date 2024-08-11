package com.theodoremeras.dissertation.integration_tests;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.theodoremeras.dissertation.ParentCreationService;
import com.theodoremeras.dissertation.TestDataUtil;
import com.theodoremeras.dissertation.authentication.AuthenticationService;
import com.theodoremeras.dissertation.authentication.UserLoginRequestDto;
import com.theodoremeras.dissertation.authentication.UserRegistrationDto;
import com.theodoremeras.dissertation.department.DepartmentEntity;
import com.theodoremeras.dissertation.role.RoleEntity;
import com.theodoremeras.dissertation.user.UserDto;
import com.theodoremeras.dissertation.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class AuthenticationControllerIntegrationTests {

    private UserService userService;

    private ParentCreationService parentCreationService;

    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @Autowired
    public AuthenticationControllerIntegrationTests(
            UserService userService, ParentCreationService parentCreationService,
            ObjectMapper objectMapper, MockMvc mockMvc
    ) {
        this.userService = userService;
        this.parentCreationService = parentCreationService;
        this.objectMapper = objectMapper;
        this.mockMvc = mockMvc;
    }

    @Test
    public void testRegisterAndLoginUser() throws Exception {
        RoleEntity savedRoleEntity = parentCreationService.createRoleParentEntity();
        DepartmentEntity savedDepartmentEntity = parentCreationService.createDepartmentParentEntity();

        // Register user
        UserRegistrationDto testUserRegistrationDto =
                TestDataUtil.createTestUserRegistrationDto(savedRoleEntity.getId(), savedDepartmentEntity.getId());
        String registrationJson = objectMapper.writeValueAsString(testUserRegistrationDto);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registrationJson)
        ).andExpect(
                MockMvcResultMatchers.status().isCreated()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.id").isNumber()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.name").value(testUserRegistrationDto.getName())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.email").value(testUserRegistrationDto.getEmail())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.isApproved").value(testUserRegistrationDto.getIsApproved())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.roleId").value(savedRoleEntity.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.departmentId").value(savedDepartmentEntity.getId())
        );


        // Login with the same user
        UserLoginRequestDto testUserLoginDto = TestDataUtil.createTestUserLoginRequestDto();
        String loginJson = objectMapper.writeValueAsString(testUserLoginDto);

        MvcResult loginResult = mockMvc.perform(
                MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.user.id").isNumber()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.user.name").value(testUserRegistrationDto.getName())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.user.email").value(testUserLoginDto.getEmail())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.user.isApproved").value(testUserRegistrationDto.getIsApproved())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.user.roleId").value(savedRoleEntity.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.user.departmentId").value(savedDepartmentEntity.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.jwt").isString()
        ).andReturn();


        // Fetch the logged-in user
        String jwt = JsonPath.read(loginResult.getResponse().getContentAsString(), "$.jwt");

        mockMvc.perform(
                MockMvcRequestBuilders.get("/auth/me")
                        .header("Authorization", "Bearer " + jwt)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.id").isNumber()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.name").value(testUserRegistrationDto.getName())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.email").value(testUserRegistrationDto.getEmail())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.isApproved").value(testUserRegistrationDto.getIsApproved())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.roleId").value(savedRoleEntity.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.departmentId").value(savedDepartmentEntity.getId())
        );

    }

    @Test
    public void testRegisterUserWhenEmailIsNotUnique() throws Exception {
        RoleEntity savedRoleEntity = parentCreationService.createRoleParentEntity();
        DepartmentEntity savedDepartmentEntity = parentCreationService.createDepartmentParentEntity();

        UserRegistrationDto testUserRegistrationDto =
                TestDataUtil.createTestUserRegistrationDto(savedRoleEntity.getId(), savedDepartmentEntity.getId());
        String registrationJson = objectMapper.writeValueAsString(testUserRegistrationDto);

        // Save user with the same email
        userService.save(TestDataUtil.createTestUserEntityA(savedRoleEntity, savedDepartmentEntity));

        mockMvc.perform(
                MockMvcRequestBuilders.post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registrationJson)
        ).andExpect(
                MockMvcResultMatchers.status().isConflict()
        );

    }

    @Test
    public void testRegisterUserWhenNoRoleOrDepartmentIsSpecified() throws Exception {
        UserRegistrationDto testUserRegistrationDto =
                TestDataUtil.createTestUserRegistrationDto(null, null);
        String registrationJson = objectMapper.writeValueAsString(testUserRegistrationDto);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registrationJson)
        ).andExpect(
                MockMvcResultMatchers.status().isBadRequest()
        );
    }

    @Test
    public void testRegisterUserWhenNoDepartmentExists() throws Exception {
        RoleEntity savedRoleEntity = parentCreationService.createRoleParentEntity();

        UserRegistrationDto testUserRegistrationDto =
                TestDataUtil.createTestUserRegistrationDto(savedRoleEntity.getId(), 1);
        String registrationJson = objectMapper.writeValueAsString(testUserRegistrationDto);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registrationJson)
        ).andExpect(
                MockMvcResultMatchers.status().isNotFound()
        );
    }

    @Test
    public void testRegisterUserWhenNoRoleExists() throws Exception {
        DepartmentEntity savedDepartmentEntity = parentCreationService.createDepartmentParentEntity();

        UserRegistrationDto testUserRegistrationDto =
                TestDataUtil.createTestUserRegistrationDto(1, savedDepartmentEntity.getId());
        String registrationJson = objectMapper.writeValueAsString(testUserRegistrationDto);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registrationJson)
        ).andExpect(
                MockMvcResultMatchers.status().isNotFound()
        );
    }
}
