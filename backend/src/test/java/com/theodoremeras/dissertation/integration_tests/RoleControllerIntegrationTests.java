package com.theodoremeras.dissertation.integration_tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.theodoremeras.dissertation.TestDataUtil;
import com.theodoremeras.dissertation.role.RoleEntity;
import com.theodoremeras.dissertation.role.RoleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@WithMockUser(roles = {"Administrator"})
public class RoleControllerIntegrationTests {

    private final RoleService roleService;

    private final ObjectMapper objectMapper;

    private final MockMvc mockMvc;

    @Autowired
    public RoleControllerIntegrationTests(RoleService roleService, ObjectMapper objectMapper, MockMvc mockMvc) {
        this.roleService = roleService;
        this.objectMapper = objectMapper;
        this.mockMvc = mockMvc;
    }

    @Test
    public void testCreateRole() throws Exception {
        RoleEntity testRoleDto = TestDataUtil.createTestRoleEntityA();
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
    public void testCreateRoleWhenNoNameIsSpecified() throws Exception {
        RoleEntity testRoleDto = TestDataUtil.createTestRoleEntityA();
        testRoleDto.setName(null);
        String roleJson = objectMapper.writeValueAsString(testRoleDto);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(roleJson)
        ).andExpect(
                MockMvcResultMatchers.status().isBadRequest()
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
    public void testGetAllRolesByRoleName() throws Exception {
        RoleEntity testRoleEntityA = TestDataUtil.createTestRoleEntityA();
        RoleEntity savedRoleEntityA = roleService.save(testRoleEntityA);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/roles?name=" + savedRoleEntityA.getName())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].id").value(savedRoleEntityA.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].name").value(savedRoleEntityA.getName())
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
