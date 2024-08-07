package com.theodoremeras.dissertation.integration_tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.theodoremeras.dissertation.ParentCreationService;
import com.theodoremeras.dissertation.TestDataUtil;
import com.theodoremeras.dissertation.department.DepartmentEntity;
import com.theodoremeras.dissertation.department.DepartmentService;
import com.theodoremeras.dissertation.role.RoleEntity;
import com.theodoremeras.dissertation.role.RoleService;
import com.theodoremeras.dissertation.user.UserDto;
import com.theodoremeras.dissertation.user.UserEntity;
import com.theodoremeras.dissertation.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class UserControllerIntegrationTests {

    private UserService userService;

    private ParentCreationService parentCreationService;

    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @Autowired
    public UserControllerIntegrationTests(
            UserService userService, ParentCreationService parentCreationService,
            ObjectMapper objectMapper, MockMvc mockMvc
    ) {
        this.userService = userService;
        this.parentCreationService = parentCreationService;
        this.objectMapper = objectMapper;
        this.mockMvc = mockMvc;
    }

    @Test
    public void testCreateUser() throws Exception {
        RoleEntity savedRoleEntity = parentCreationService.createRoleParentEntity();
        DepartmentEntity savedDepartmentEntity =  parentCreationService.createDepartmentParentEntity();

        UserDto testUserDto = TestDataUtil.createTestUserDtoA(savedRoleEntity.getId(), savedDepartmentEntity.getId());
        String userJson = objectMapper.writeValueAsString(testUserDto);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson)
        ).andExpect(
                MockMvcResultMatchers.status().isCreated()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.id").isNumber()
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
    public void testCreateUserWhenEmailIsNotUnique() throws Exception {
        RoleEntity savedRoleEntity = parentCreationService.createRoleParentEntity();
        DepartmentEntity savedDepartmentEntity =  parentCreationService.createDepartmentParentEntity();

        UserDto testUserDto = TestDataUtil.createTestUserDtoA(savedRoleEntity.getId(), savedDepartmentEntity.getId());
        String userJson = objectMapper.writeValueAsString(testUserDto);

        // Save user with the same email
        userService.save(TestDataUtil.createTestUserEntityA(savedRoleEntity, savedDepartmentEntity));

        mockMvc.perform(
                MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson)
        ).andExpect(
                MockMvcResultMatchers.status().isConflict()
        );
    }

    @Test
    public void testCreateUserWhenNoRoleOrDepartmentIsSpecified() throws Exception {
        UserDto testUserDto = TestDataUtil.createTestUserDtoA(null, null);
        String userJson = objectMapper.writeValueAsString(testUserDto);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson)
        ).andExpect(
                MockMvcResultMatchers.status().isBadRequest()
        );
    }

    @Test
    public void testCreateUserWhenNoDepartmentExists() throws Exception {
        RoleEntity savedRoleEntity =  parentCreationService.createRoleParentEntity();

        UserDto testUserDto = TestDataUtil.createTestUserDtoA(savedRoleEntity.getId(), 1);
        String userJson = objectMapper.writeValueAsString(testUserDto);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson)
        ).andExpect(
                MockMvcResultMatchers.status().isNotFound()
        );
    }

    @Test
    public void testCreateUserWhenNoRoleExists() throws Exception {
        DepartmentEntity savedDepartmentEntity =  parentCreationService.createDepartmentParentEntity();

        UserDto testUserDto = TestDataUtil.createTestUserDtoA(1, savedDepartmentEntity.getId());
        String userJson = objectMapper.writeValueAsString(testUserDto);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson)
        ).andExpect(
                MockMvcResultMatchers.status().isNotFound()
        );
    }

    @Test
    public void testGetAllUsers() throws Exception {
        RoleEntity savedRoleEntity =  parentCreationService.createRoleParentEntity();
        DepartmentEntity savedDepartmentEntity =  parentCreationService.createDepartmentParentEntity();

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
        RoleEntity savedRoleEntity =  parentCreationService.createRoleParentEntity();
        DepartmentEntity savedDepartmentEntity =  parentCreationService.createDepartmentParentEntity();

        UserEntity testUserEntityA = TestDataUtil.createTestUserEntityA(savedRoleEntity, savedDepartmentEntity);
        UserEntity savedUserEntityA = userService.save(testUserEntityA);
        UserEntity testUserEntityB = TestDataUtil.createTestUserEntityB(savedRoleEntity, savedDepartmentEntity);
        UserEntity savedUserEntityB = userService.save(testUserEntityB);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/users?ids=" + savedUserEntityA.getId() + ", "  + savedUserEntityB.getId())
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
        RoleEntity savedRoleEntity =  parentCreationService.createRoleParentEntity();
        DepartmentEntity savedDepartmentEntity =  parentCreationService.createDepartmentParentEntity();

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
        RoleEntity savedRoleEntity =  parentCreationService.createRoleParentEntity();
        DepartmentEntity savedDepartmentEntity =  parentCreationService.createDepartmentParentEntity();

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
        RoleEntity savedRoleEntity =  parentCreationService.createRoleParentEntity();
        DepartmentEntity savedDepartmentEntity =  parentCreationService.createDepartmentParentEntity();

        UserEntity testUserEntity = TestDataUtil.createTestUserEntityA(savedRoleEntity, savedDepartmentEntity);
        UserEntity savedUserEntity = userService.save(testUserEntity);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/users/" + savedUserEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
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
        mockMvc.perform(
                MockMvcRequestBuilders.get("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isNotFound()
        );
    }

    @Test
    public void testPartialUpdateUser() throws Exception {
        RoleEntity savedRoleEntity =  parentCreationService.createRoleParentEntity();
        DepartmentEntity savedDepartmentEntity =  parentCreationService.createDepartmentParentEntity();

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
        RoleEntity savedRoleEntity =  parentCreationService.createRoleParentEntity();
        DepartmentEntity savedDepartmentEntity =  parentCreationService.createDepartmentParentEntity();

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
