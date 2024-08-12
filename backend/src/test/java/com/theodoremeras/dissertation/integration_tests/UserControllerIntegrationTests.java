package com.theodoremeras.dissertation.integration_tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.theodoremeras.dissertation.ParentCreationService;
import com.theodoremeras.dissertation.TestDataUtil;
import com.theodoremeras.dissertation.department.DepartmentEntity;
import com.theodoremeras.dissertation.role.RoleEntity;
import com.theodoremeras.dissertation.user.EmailDto;
import com.theodoremeras.dissertation.user.UserDto;
import com.theodoremeras.dissertation.user.UserEntity;
import com.theodoremeras.dissertation.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.Instant;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@WithMockUser(roles = {"Administrator"})
public class UserControllerIntegrationTests {

    private final UserService userService;

    private final ParentCreationService parentCreationService;

    private final MockMvc mockMvc;

    private final ObjectMapper objectMapper;

    private final JwtEncoder jwtEncoder;

    @Autowired
    public UserControllerIntegrationTests(
            UserService userService, ParentCreationService parentCreationService,
            ObjectMapper objectMapper, MockMvc mockMvc, JwtEncoder jwtEncoder
    ) {
        this.userService = userService;
        this.parentCreationService = parentCreationService;
        this.objectMapper = objectMapper;
        this.mockMvc = mockMvc;
        this.jwtEncoder = jwtEncoder;
    }

    @Test
    public void testGetAllUsers() throws Exception {
        RoleEntity savedRoleEntity = parentCreationService.createRoleParentEntity();
        DepartmentEntity savedDepartmentEntity = parentCreationService.createDepartmentParentEntity();

        UserEntity testUserEntityA = TestDataUtil.createTestUserEntityA(savedRoleEntity, savedDepartmentEntity);
        UserEntity savedUserEntityA = userService.save(testUserEntityA);
        UserEntity testUserEntityB = TestDataUtil.createTestUserEntityB(savedRoleEntity, savedDepartmentEntity);
        UserEntity savedUserEntityB = userService.save(testUserEntityB);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/users")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].id").value(savedUserEntityA.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].name").value(savedUserEntityA.getName())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].email").value(savedUserEntityA.getEmail())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].isApproved").value(savedUserEntityA.getIsApproved())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].roleId").value(savedRoleEntity.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].departmentId").value(savedDepartmentEntity.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].id").value(savedUserEntityB.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].name").value(savedUserEntityB.getName())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].email").value(savedUserEntityB.getEmail())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].isApproved").value(savedUserEntityB.getIsApproved())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].roleId").value(savedRoleEntity.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].departmentId").value(savedDepartmentEntity.getId())
        );
    }

    @Test
    public void testGetAllUsersByIds() throws Exception {
        RoleEntity savedRoleEntity = parentCreationService.createRoleParentEntity();
        DepartmentEntity savedDepartmentEntity = parentCreationService.createDepartmentParentEntity();

        UserEntity testUserEntityA = TestDataUtil.createTestUserEntityA(savedRoleEntity, savedDepartmentEntity);
        UserEntity savedUserEntityA = userService.save(testUserEntityA);
        UserEntity testUserEntityB = TestDataUtil.createTestUserEntityB(savedRoleEntity, savedDepartmentEntity);
        UserEntity savedUserEntityB = userService.save(testUserEntityB);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/users?ids=" + savedUserEntityA.getId() + ", " + savedUserEntityB.getId())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].id").value(savedUserEntityA.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].name").value(savedUserEntityA.getName())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].email").value(savedUserEntityA.getEmail())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].isApproved").value(savedUserEntityA.getIsApproved())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].roleId").value(savedRoleEntity.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].departmentId").value(savedDepartmentEntity.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].id").value(savedUserEntityB.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].name").value(savedUserEntityB.getName())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].email").value(savedUserEntityB.getEmail())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].isApproved").value(savedUserEntityB.getIsApproved())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].roleId").value(savedRoleEntity.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].departmentId").value(savedDepartmentEntity.getId())
        );
    }

    @Test
    public void testGetAllUsersByEmail() throws Exception {
        RoleEntity savedRoleEntity = parentCreationService.createRoleParentEntity();
        DepartmentEntity savedDepartmentEntity = parentCreationService.createDepartmentParentEntity();

        UserEntity testUserEntityA = TestDataUtil.createTestUserEntityA(savedRoleEntity, savedDepartmentEntity);
        UserEntity savedUserEntityA = userService.save(testUserEntityA);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/users?email=" + savedUserEntityA.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].id").value(savedUserEntityA.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].name").value(savedUserEntityA.getName())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].email").value(savedUserEntityA.getEmail())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].isApproved").value(savedUserEntityA.getIsApproved())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].roleId").value(savedRoleEntity.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].departmentId").value(savedDepartmentEntity.getId())
        );
    }

    @Test
    public void testGetAllUsersByDepartmentIdAndRoleId() throws Exception {
        RoleEntity savedRoleEntity = parentCreationService.createRoleParentEntity();
        DepartmentEntity savedDepartmentEntity = parentCreationService.createDepartmentParentEntity();

        UserEntity testUserEntityA = TestDataUtil.createTestUserEntityA(savedRoleEntity, savedDepartmentEntity);
        UserEntity savedUserEntityA = userService.save(testUserEntityA);
        UserEntity testUserEntityB = TestDataUtil.createTestUserEntityB(savedRoleEntity, savedDepartmentEntity);
        UserEntity savedUserEntityB = userService.save(testUserEntityB);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/users?departmentId=" + savedDepartmentEntity.getId()
                                + "&roleId=" + savedRoleEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].id").value(savedUserEntityA.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].name").value(savedUserEntityA.getName())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].email").value(savedUserEntityA.getEmail())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].isApproved").value(savedUserEntityA.getIsApproved())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].roleId").value(savedRoleEntity.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].departmentId").value(savedDepartmentEntity.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].id").value(savedUserEntityB.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].name").value(savedUserEntityB.getName())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].email").value(savedUserEntityB.getEmail())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].isApproved").value(savedUserEntityB.getIsApproved())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].roleId").value(savedRoleEntity.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].departmentId").value(savedDepartmentEntity.getId())
        );
    }


    @Test
    public void testGetUserById() throws Exception {
        RoleEntity savedRoleEntity = parentCreationService.createRoleParentEntity();
        DepartmentEntity savedDepartmentEntity = parentCreationService.createDepartmentParentEntity();

        // Build jwt with admin role specified
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(60))
                .subject("admin@admin.com")
                .claim("roles", "Administrator")
                .claim("userId", 5)
                .build();
        String token = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        UserEntity testUserEntity = TestDataUtil.createTestUserEntityA(savedRoleEntity, savedDepartmentEntity);
        UserEntity savedUserEntity = userService.save(testUserEntity);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/users/" + savedUserEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.id").isNumber()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.name").value(savedUserEntity.getName())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.email").value(savedUserEntity.getEmail())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.isApproved").value(savedUserEntity.getIsApproved())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.roleId").value(savedRoleEntity.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.departmentId").value(savedDepartmentEntity.getId())
        );
    }

    @Test
    public void testGetUserByIdWhenNoUserExists() throws Exception {

        // Build jwt with admin role specified
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(60))
                .subject("admin@admin.com")
                .claim("roles", "Administrator")
                .claim("userId", 5)
                .build();
        String token = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        mockMvc.perform(
                MockMvcRequestBuilders.get("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
        ).andExpect(
                MockMvcResultMatchers.status().isNotFound()
        );
    }

    @Test
    public void testGetUserByIdWhenForbidden() throws Exception {
        RoleEntity savedRoleEntity = parentCreationService.createRoleParentEntity();
        DepartmentEntity savedDepartmentEntity = parentCreationService.createDepartmentParentEntity();

        // Build jwt with student role specified
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(60))
                .subject("admin@admin.com")
                .claim("roles", "Student")
                .claim("userId", 5)
                .build();
        String token = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        UserEntity testUserEntity = TestDataUtil.createTestUserEntityA(savedRoleEntity, savedDepartmentEntity);
        UserEntity savedUserEntity = userService.save(testUserEntity);

        // Request the details of another student, which students are not allowed to do
        mockMvc.perform(
                MockMvcRequestBuilders.get("/users/" + savedUserEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
        ).andExpect(
                MockMvcResultMatchers.status().isForbidden()
        );
    }

    @Test
    public void testEmailUser() throws Exception {
        RoleEntity savedRoleEntity = parentCreationService.createRoleParentEntity();
        DepartmentEntity savedDepartmentEntity = parentCreationService.createDepartmentParentEntity();

        UserEntity testUserEntity = TestDataUtil.createTestUserEntityA(savedRoleEntity, savedDepartmentEntity);
        UserEntity savedUserEntity = userService.save(testUserEntity);

        EmailDto emailDto = EmailDto.builder()
                .subject("Test Email")
                .body("Test body")
                .build();
        String emailJson = objectMapper.writeValueAsString(emailDto);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/users/" + savedUserEntity.getId() + "/mail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(emailJson)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        );
    }

    @Test
    public void testEmailUserWhenNoUserExists() throws Exception {

        EmailDto emailDto = EmailDto.builder()
                .subject("Test Email")
                .body("Test body")
                .build();
        String emailJson = objectMapper.writeValueAsString(emailDto);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/users/1/mail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(emailJson)
        ).andExpect(
                MockMvcResultMatchers.status().isNotFound()
        );
    }

    @Test
    public void testPartialUpdateUser() throws Exception {
        RoleEntity savedRoleEntity = parentCreationService.createRoleParentEntity();
        DepartmentEntity savedDepartmentEntity = parentCreationService.createDepartmentParentEntity();

        UserEntity testUserEntity = TestDataUtil.createTestUserEntityA(savedRoleEntity, savedDepartmentEntity);
        UserEntity savedUserEntity = userService.save(testUserEntity);

        UserDto testUserDto = TestDataUtil.createTestUserDtoB(null, null);
        String userUpdateJson = objectMapper.writeValueAsString(testUserDto);

        mockMvc.perform(
                MockMvcRequestBuilders.patch("/users/" + savedUserEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userUpdateJson)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.id").value(savedUserEntity.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.name").value(testUserDto.getName())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.email").value(testUserDto.getEmail())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.isApproved").value(testUserDto.getIsApproved())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.roleId").value(savedRoleEntity.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.departmentId").value(savedDepartmentEntity.getId())
        );
    }

    @Test
    public void testPartialUpdateUserWhenNoUserExists() throws Exception {
        UserDto testUserDto = TestDataUtil.createTestUserDtoB(null, null);
        String userUpdateJson = objectMapper.writeValueAsString(testUserDto);

        mockMvc.perform(
                MockMvcRequestBuilders.patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userUpdateJson)
        ).andExpect(
                MockMvcResultMatchers.status().isNotFound()
        );
    }

    @Test
    public void testDeleteUser() throws Exception {
        RoleEntity savedRoleEntity = parentCreationService.createRoleParentEntity();
        DepartmentEntity savedDepartmentEntity = parentCreationService.createDepartmentParentEntity();

        UserEntity testUserEntity = TestDataUtil.createTestUserEntityA(savedRoleEntity, savedDepartmentEntity);
        UserEntity savedUserEntity = userService.save(testUserEntity);

        mockMvc.perform(
                MockMvcRequestBuilders.delete("/users/" + savedUserEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isNoContent()
        );
    }

    @Test
    public void testDeleteUserWhenNoUserExists() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isNoContent()
        );
    }

}
