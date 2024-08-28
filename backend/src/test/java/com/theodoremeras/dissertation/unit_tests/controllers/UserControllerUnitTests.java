package com.theodoremeras.dissertation.unit_tests.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.theodoremeras.dissertation.TestDataUtil;
import com.theodoremeras.dissertation.department.DepartmentEntity;
import com.theodoremeras.dissertation.role.RoleEntity;
import com.theodoremeras.dissertation.user.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false) // circumvent spring security for unit tests
public class UserControllerUnitTests {

    @MockBean
    private UserService userService;

    @MockBean
    private UserMapper userMapper;

    @MockBean
    private JwtDecoder jwtDecoder;

    @MockBean
    private JavaMailSender javaMailSender;

    private final MockMvc mockMvc;

    private final ObjectMapper objectMapper;

    private RoleEntity testRoleEntity;

    private DepartmentEntity testDepartmentEntity;

    private UserEntity testUserEntity;

    private UserDto testUserDto;

    @Autowired
    public UserControllerUnitTests(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @BeforeEach
    public void setUp() {
        // Initialize test objects
        testRoleEntity = TestDataUtil.createTestRoleEntityA();
        testDepartmentEntity = TestDataUtil.createTestDepartmentEntityA();
        testUserEntity = TestDataUtil.createTestUserEntityA(testRoleEntity, testDepartmentEntity);
        testUserDto  = TestDataUtil.createTestUserDtoA(testRoleEntity.getId(), testDepartmentEntity.getId());
    }

    @Test
    public void testGetAllUsers() throws Exception {
        when(userService.findAll()).thenReturn(List.of(testUserEntity));
        when(userMapper.mapToDto(testUserEntity)).thenReturn(testUserDto);


        mockMvc.perform(
                MockMvcRequestBuilders.get("/users")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].id").value(testUserEntity.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].name").value(testUserEntity.getName())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].email").value(testUserEntity.getEmail())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].isApproved").value(testUserEntity.getIsApproved())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].roleId").value(testRoleEntity.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].departmentId").value(testDepartmentEntity.getId())
        );
    }

    @Test
    public void testGetAllUsersByIds() throws Exception {
        when(userService.findAllByIdIn(List.of(testUserEntity.getId()))).thenReturn(List.of(testUserEntity));
        when(userMapper.mapToDto(testUserEntity)).thenReturn(testUserDto);


        mockMvc.perform(
                MockMvcRequestBuilders.get("/users?ids=" + testUserEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].id").value(testUserEntity.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].name").value(testUserEntity.getName())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].email").value(testUserEntity.getEmail())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].isApproved").value(testUserEntity.getIsApproved())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].roleId").value(testRoleEntity.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].departmentId").value(testDepartmentEntity.getId())
        );
    }

    @Test
    public void testGetAllUsersByEmail() throws Exception {
        when(userService.findAllByEmail(testUserEntity.getEmail())).thenReturn(List.of(testUserEntity));
        when(userMapper.mapToDto(testUserEntity)).thenReturn(testUserDto);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/users?email=" + testUserEntity.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].id").value(testUserEntity.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].name").value(testUserEntity.getName())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].email").value(testUserEntity.getEmail())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].isApproved").value(testUserEntity.getIsApproved())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].roleId").value(testRoleEntity.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].departmentId").value(testDepartmentEntity.getId())
        );
    }

    @Test
    public void testGetAllUsersByDepartmentIdAndRoleId() throws Exception {
        when(userService.findAllByDepartmentIdAndRoleId(testDepartmentEntity.getId(), testRoleEntity.getId()))
                .thenReturn(List.of(testUserEntity));
        when(userMapper.mapToDto(testUserEntity)).thenReturn(testUserDto);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/users?departmentId=" + testDepartmentEntity.getId()
                                + "&roleId=" + testRoleEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].id").value(testUserEntity.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].name").value(testUserEntity.getName())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].email").value(testUserEntity.getEmail())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].isApproved").value(testUserEntity.getIsApproved())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].roleId").value(testRoleEntity.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].departmentId").value(testDepartmentEntity.getId())
        );
    }

    @Test
    public void testGetUserById() throws Exception {
        Jwt mockJwt = mock(Jwt.class);

        when(jwtDecoder.decode("token")).thenReturn(mockJwt);
        when(mockJwt.getClaim("userId")).thenReturn(Long.valueOf(testUserEntity.getId()));
        when(mockJwt.getClaim("roles")).thenReturn("Administrator");
        when(userService.findOneById(testUserEntity.getId())).thenReturn(Optional.of(testUserEntity));
        when(userMapper.mapToDto(testUserEntity)).thenReturn(testUserDto);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/users/" + testUserEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer token")
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

    @Test
    public void testGetUserByIdWhenNoUserExists() throws Exception {
        Jwt mockJwt = mock(Jwt.class);

        when(jwtDecoder.decode("token")).thenReturn(mockJwt);
        when(mockJwt.getClaim("userId")).thenReturn(Long.valueOf(testUserEntity.getId()));
        when(mockJwt.getClaim("roles")).thenReturn("Administrator");
        when(userService.findOneById(testUserEntity.getId())).thenReturn(Optional.empty());

        mockMvc.perform(
                MockMvcRequestBuilders.get("/users/" + testUserEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer token")
        ).andExpect(
                MockMvcResultMatchers.status().isNotFound()
        );
    }

    @Test
    public void testGetUserByIdWhenForbidden() throws Exception {
        Jwt mockJwt = mock(Jwt.class);

        when(jwtDecoder.decode("token")).thenReturn(mockJwt);
        when(mockJwt.getClaim("userId")).thenReturn(5L);
        when(mockJwt.getClaim("roles")).thenReturn("Student");
        when(userService.findOneById(testUserEntity.getId())).thenReturn(Optional.of(testUserEntity));

        mockMvc.perform(
                MockMvcRequestBuilders.get("/users/" + testUserEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer token")
        ).andExpect(
                MockMvcResultMatchers.status().isForbidden()
        );
    }

    @Test
    public void testEmailUser() throws Exception {
        EmailDto emailDto = EmailDto.builder()
                .subject("Test Email")
                .body("Test body")
                .build();
        String emailJson = objectMapper.writeValueAsString(emailDto);

        when(userService.findOneById(testUserEntity.getId())).thenReturn(Optional.of(testUserEntity));

        mockMvc.perform(
                MockMvcRequestBuilders.post("/users/" + testUserEntity.getId() + "/mail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(emailJson)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        );

        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    public void testEmailUserWhenNoUserExists() throws Exception {
        EmailDto emailDto = EmailDto.builder()
                .subject("Test Email")
                .body("Test body")
                .build();
        String emailJson = objectMapper.writeValueAsString(emailDto);

        when(userService.findOneById(testUserEntity.getId())).thenReturn(Optional.empty());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/users/" + testUserEntity.getId() + "/mail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(emailJson)
        ).andExpect(
                MockMvcResultMatchers.status().isNotFound()
        );
    }

    @Test
    public void testPartialUpdateUser() throws Exception {
        UserEntity updatedUserEntity = TestDataUtil.createTestUserEntityB(testRoleEntity, testDepartmentEntity);
        updatedUserEntity.setId(testUserEntity.getId());
        UserDto updatedUserDto = TestDataUtil.createTestUserDtoB(testRoleEntity.getId(), testDepartmentEntity.getId());
        updatedUserDto.setId(testUserEntity.getId());
        String userUpdateJson = objectMapper.writeValueAsString(updatedUserDto);

        when(userService.exists(testUserEntity.getId())).thenReturn(true);
        when(userMapper.mapFromDto(any())).thenReturn(updatedUserEntity);
        when(userService.partialUpdate(testUserEntity.getId(), updatedUserEntity)).thenReturn(updatedUserEntity);
        when(userMapper.mapToDto(updatedUserEntity)).thenReturn(updatedUserDto);

        mockMvc.perform(
                MockMvcRequestBuilders.patch("/users/" + testUserEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userUpdateJson)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.id").value(testUserEntity.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.name").value(updatedUserDto.getName())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.email").value(updatedUserDto.getEmail())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.isApproved").value(updatedUserDto.getIsApproved())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.roleId").value(testRoleEntity.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.departmentId").value(testDepartmentEntity.getId())
        );
    }

    @Test
    public void testPartialUpdateUserWhenNoUserExists() throws Exception {
        UserDto updatedUserDto = TestDataUtil.createTestUserDtoB(testRoleEntity.getId(), testDepartmentEntity.getId());
        updatedUserDto.setId(testUserEntity.getId());
        String userUpdateJson = objectMapper.writeValueAsString(updatedUserDto);

        when(userService.exists(testUserEntity.getId())).thenReturn(false);

        mockMvc.perform(
                MockMvcRequestBuilders.patch("/users/" + testUserEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userUpdateJson)
        ).andExpect(
                MockMvcResultMatchers.status().isNotFound()
        );
    }

    @Test
    public void testDeleteUser() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/users/" + testUserEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isNoContent()
        );

        verify(userService, times(1)).delete(testUserEntity.getId());
    }

}
