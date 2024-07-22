package com.theodoremeras.dissertation.integration_tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.theodoremeras.dissertation.TestDataUtil;
import com.theodoremeras.dissertation.role.RoleDto;
import com.theodoremeras.dissertation.role.RoleEntity;
import com.theodoremeras.dissertation.role.RoleService;
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
public class RoleControllerIntegrationTests {

    private RoleService roleService;

    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @Autowired
    public RoleControllerIntegrationTests(RoleService roleService, ObjectMapper objectMapper, MockMvc mockMvc) {
        this.roleService = roleService;
        this.objectMapper = objectMapper;
        this.mockMvc = mockMvc;
    }

    @Test
    public void testCreateRole() throws Exception {
        RoleEntity testRoleDto  = TestDataUtil.createTestRoleEntityA();
        String roleJson = objectMapper.writeValueAsString(testRoleDto);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(roleJson)
        ).andExpect(
                MockMvcResultMatchers.status().isCreated()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.id").isNumber()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.name").value(testRoleDto.getName())
        );
    }

    @Test
    public void testGetAllRoles() throws Exception {
        RoleEntity testRoleEntityA = TestDataUtil.createTestRoleEntityA();
        RoleEntity savedRoleEntityA = roleService.save(testRoleEntityA);
        RoleEntity testRoleEntityB = TestDataUtil.createTestRoleEntityB();
        RoleEntity savedRoleEntityB = roleService.save(testRoleEntityB);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/roles")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].id").value(savedRoleEntityA.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].name").value(savedRoleEntityA.getName())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].id").value(savedRoleEntityB.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].name").value(savedRoleEntityB.getName())
        );
    }

    @Test
    public void testGetRole() throws Exception {
        RoleEntity testRoleEntity = TestDataUtil.createTestRoleEntityA();
        RoleEntity savedRoleEntity = roleService.save(testRoleEntity);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/roles/" + savedRoleEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.id").value(savedRoleEntity.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.name").value(savedRoleEntity.getName())
        );
    }

    @Test
    public void testGetRoleWhenNoRoleExists() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/roles/1")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isNotFound()
        );
    }

    @Test
    public void testPartialUpdateRole() throws Exception {
        RoleEntity testRoleEntity = TestDataUtil.createTestRoleEntityA();
        RoleEntity savedRoleEntity = roleService.save(testRoleEntity);

        RoleDto testRoleDto = TestDataUtil.createTestRoleDtoB();
        String roleJson = objectMapper.writeValueAsString(testRoleDto);

        mockMvc.perform(
                MockMvcRequestBuilders.patch("/roles/" + savedRoleEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(roleJson)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.id").value(savedRoleEntity.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.name").value(testRoleDto.getName())
        );
    }

    @Test
    public void testPartialUpdateRoleWhenNoRoleExists() throws Exception {
        RoleDto testRoleDto = TestDataUtil.createTestRoleDtoB();
        String roleJson = objectMapper.writeValueAsString(testRoleDto);

        mockMvc.perform(
                MockMvcRequestBuilders.patch("/roles/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(roleJson)
        ).andExpect(
                MockMvcResultMatchers.status().isNotFound()
        );
    }

    @Test
    public void testDeleteRole() throws Exception {
        RoleEntity testRoleEntity = TestDataUtil.createTestRoleEntityA();
        RoleEntity savedRoleEntity = roleService.save(testRoleEntity);

        mockMvc.perform(
                MockMvcRequestBuilders.delete("/roles/" + savedRoleEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isNoContent()
        );
    }

    @Test
    public void testDeleteRoleWhenNoRoleExists() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/roles/1")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isNoContent()
        );
    }

}
