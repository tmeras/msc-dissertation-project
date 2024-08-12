package com.theodoremeras.dissertation.unit_tests.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.theodoremeras.dissertation.TestDataUtil;
import com.theodoremeras.dissertation.authentication.AuthenticationController;
import com.theodoremeras.dissertation.authentication.AuthenticationService;
import com.theodoremeras.dissertation.authentication.UserRegistrationDto;
import com.theodoremeras.dissertation.authentication.UserRegistrationMapper;
import com.theodoremeras.dissertation.department.DepartmentEntity;
import com.theodoremeras.dissertation.department.DepartmentService;
import com.theodoremeras.dissertation.role.RoleEntity;
import com.theodoremeras.dissertation.role.RoleService;
import com.theodoremeras.dissertation.user.UserEntity;
import com.theodoremeras.dissertation.user.UserMapper;
import com.theodoremeras.dissertation.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@WebMvcTest(AuthenticationController.class)
@AutoConfigureMockMvc(addFilters = false) // circumvent spring security for unit tests
public class AuthenticationControllerUnitTests {

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private UserService userService;

    @MockBean
    private RoleService roleService;

    @MockBean
    private DepartmentService departmentService;

    @MockBean
    private UserRegistrationMapper userRegistrationMapper;

    @MockBean
    private UserMapper userMapper;

    @MockBean
    private JwtDecoder jwtDecoder;

    private final MockMvc mockMvc;

    private final ObjectMapper objectMapper;

    private RoleEntity testRoleEntity;

    private DepartmentEntity testDepartmentEntity;

    private UserEntity testUserEntity;

    private UserRegistrationDto testUserRegistrationDto;

    @Autowired
    public AuthenticationControllerUnitTests(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @BeforeEach
    public void setUp() {
        // Initialize test objects
        testRoleEntity = TestDataUtil.createTestRoleEntityA();
        testDepartmentEntity = TestDataUtil.createTestDepartmentEntityA();
        testUserEntity = TestDataUtil.createTestUserEntityA(testRoleEntity, testDepartmentEntity);
        testUserRegistrationDto =
                TestDataUtil.createTestUserRegistrationDto(testRoleEntity.getId(), testDepartmentEntity.getId());
    }

    @Test
    public void testRegisterUser() throws Exception {
        String userJson = objectMapper.writeValueAsString(testUserRegistrationDto);

        when(userRegistrationMapper.mapFromDto(any())).thenReturn(testUserEntity);
        when(userService.findAllByEmail(testUserEntity.getEmail())).thenReturn(List.of());
        when(roleService.findOneById(testRoleEntity.getId())).thenReturn(Optional.of(testRoleEntity));
        when(departmentService.findOneById(testDepartmentEntity.getId())).thenReturn(Optional.of(testDepartmentEntity));
        when(authenticationService.registerUser(testUserEntity)).thenReturn(testUserEntity);
        when(userMapper.mapToDto(testUserEntity)).
                thenReturn(TestDataUtil.createTestUserDtoA(testRoleEntity.getId(), testDepartmentEntity.getId()));

        mockMvc.perform(
                MockMvcRequestBuilders.post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson)
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
                MockMvcResultMatchers.jsonPath("$.roleId").value(testRoleEntity.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.departmentId").value(testDepartmentEntity.getId())
        );

    }

    @Test
    public void testRegisterUserWhenEmailIsNotUnique() throws Exception {
        String userJson = objectMapper.writeValueAsString(testUserRegistrationDto);

        when(userRegistrationMapper.mapFromDto(any())).thenReturn(testUserEntity);
        when(userService.findAllByEmail(testUserEntity.getEmail())).thenReturn(List.of(testUserEntity));

        mockMvc.perform(
                MockMvcRequestBuilders.post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson)
        ).andExpect(
                MockMvcResultMatchers.status().isConflict()
        );

    }

    @Test
    public void testRegisterUserWhenNoRoleExists() throws Exception {
        String userJson = objectMapper.writeValueAsString(testUserRegistrationDto);

        when(userRegistrationMapper.mapFromDto(any())).thenReturn(testUserEntity);
        when(userService.findAllByEmail(testUserEntity.getEmail())).thenReturn(List.of());
        when(roleService.findOneById(testUserEntity.getRole().getId())).thenReturn(Optional.empty());


        mockMvc.perform(
                MockMvcRequestBuilders.post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson)
        ).andExpect(
                MockMvcResultMatchers.status().isNotFound()
        );

    }

    @Test
    public void testRegisterUserWhenNoDepartmentExists() throws Exception {
        String userJson = objectMapper.writeValueAsString(testUserRegistrationDto);

        when(userRegistrationMapper.mapFromDto(any())).thenReturn(testUserEntity);
        when(userService.findAllByEmail(testUserEntity.getEmail())).thenReturn(List.of());
        when(roleService.findOneById(testUserEntity.getRole().getId())).thenReturn(Optional.of(testRoleEntity));
        when(departmentService.findOneById(testUserEntity.getDepartment().getId())).thenReturn(Optional.empty());


        mockMvc.perform(
                MockMvcRequestBuilders.post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson)
        ).andExpect(
                MockMvcResultMatchers.status().isNotFound()
        );
    }

    @Test
    public void testLoginUser() throws Exception {
        String loginJson = objectMapper.writeValueAsString(TestDataUtil.createTestUserLoginRequestDto());

        when(authenticationService.loginUser(any())).thenReturn(TestDataUtil.createTestUserLoginResponseDto());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        );

    }

    @Test
    public void testLoginUserWhenLoginFails() throws Exception {
        String loginJson = objectMapper.writeValueAsString(TestDataUtil.createTestUserLoginRequestDto());

        when(authenticationService.loginUser(any())).thenReturn(null);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson)
        ).andExpect(
                MockMvcResultMatchers.status().isUnauthorized()
        );

    }

    @Test
    public void testGetLoggedInUser() throws Exception {
        Jwt mockJwt = mock(Jwt.class);
        when(jwtDecoder.decode("token")).thenReturn(mockJwt);
        when(mockJwt.getClaim("sub")).thenReturn("test@test.com");
        when(userService.findOneByEmail("test@test.com")).thenReturn(Optional.of(testUserEntity));
        when(userMapper.mapToDto(testUserEntity)).
                thenReturn(TestDataUtil.createTestUserDtoA(testRoleEntity.getId(), testDepartmentEntity.getId()));

        mockMvc.perform(
                MockMvcRequestBuilders.get("/auth/me")
                        .header("Authorization", "Bearer " + "token")
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.id").isNumber()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.name").value(testUserEntity.getName())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.email").value(testUserEntity.getEmail())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.isApproved").value(testUserEntity.getIsApproved())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.roleId").value(testRoleEntity.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.departmentId").value(testDepartmentEntity.getId())
        );
    }

}
