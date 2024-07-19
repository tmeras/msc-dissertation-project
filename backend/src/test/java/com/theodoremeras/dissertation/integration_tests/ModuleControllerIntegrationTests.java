package com.theodoremeras.dissertation.integration_tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.theodoremeras.dissertation.TestDataUtil;
import com.theodoremeras.dissertation.module.ModuleDto;
import com.theodoremeras.dissertation.module.ModuleEntity;
import com.theodoremeras.dissertation.module.ModuleService;
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
public class ModuleControllerIntegrationTests {

    private ModuleService moduleService;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Autowired
    public ModuleControllerIntegrationTests(ModuleService moduleService, MockMvc mockMvc, ObjectMapper objectMapper) {
        this.moduleService = moduleService;
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @Test
    public void testCreateModule() throws Exception {
        ModuleDto testModuleDto = TestDataUtil.createTestModuleDtoA(null);
        String moduleJson = objectMapper.writeValueAsString(testModuleDto);

        mockMvc.perform(
                MockMvcRequestBuilders.put("/modules/" + testModuleDto.getCode())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(moduleJson)
        ).andExpect(
                MockMvcResultMatchers.status().isCreated()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.code").value(testModuleDto.getCode())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.name").value(testModuleDto.getName())
        );
    }

    @Test
    public void testFullUpdateModule() throws Exception {
        ModuleEntity testModuleEntity = TestDataUtil.createTestModuleEntityA(null);
        ModuleEntity savedModuleEntity = moduleService.save(
                testModuleEntity.getCode(), testModuleEntity
        );

        ModuleDto testModuleDto = TestDataUtil.createTestModuleDtoB(null);
        testModuleDto.setCode(testModuleEntity.getCode());
        String moduleJson = objectMapper.writeValueAsString(testModuleDto);

        mockMvc.perform(
                MockMvcRequestBuilders.put("/modules/" + savedModuleEntity.getCode())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(moduleJson)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.code").value(testModuleEntity.getCode())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.name").value(testModuleDto.getName())
        );
    }

    @Test
    public void testGetAllModules() throws Exception {
        ModuleEntity testModuleEntityA = TestDataUtil.createTestModuleEntityA(null);
        moduleService.save(testModuleEntityA.getCode(), testModuleEntityA);
        ModuleEntity testModuleEntityB = TestDataUtil.createTestModuleEntityB(null);
        moduleService.save(testModuleEntityB.getCode(), testModuleEntityB);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/modules")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].code").value(testModuleEntityA.getCode())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].name").value(testModuleEntityA.getName())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].code").value(testModuleEntityB.getCode())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].name").value(testModuleEntityB.getName())
        );
    }

    @Test
    public void testGetModuleWhenModuleExists() throws Exception {
        ModuleEntity testModuleEntity = TestDataUtil.createTestModuleEntityA(null);
        moduleService.save(testModuleEntity.getCode(), testModuleEntity);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/modules/" + testModuleEntity.getCode())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.code").value(testModuleEntity.getCode())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.name").value(testModuleEntity.getName())
        );
    }

    @Test
    public void testGetModuleWhenNoModuleExists() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/modules/COM2131")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isNotFound()
        );
    }

    @Test
    public void testPartialUpdateModuleWhenModuleExists() throws Exception {
        ModuleEntity testModuleEntity = TestDataUtil.createTestModuleEntityA(null);
        ModuleEntity savedModuleEntity = moduleService.save(testModuleEntity.getCode(), testModuleEntity);

        ModuleDto testModuleDto = TestDataUtil.createTestModuleDtoB(null);
        testModuleDto.setCode(savedModuleEntity.getCode());
        String moduleUpdateJson = objectMapper.writeValueAsString(testModuleDto);

        mockMvc.perform(
                MockMvcRequestBuilders.patch("/modules/" + savedModuleEntity.getCode())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(moduleUpdateJson)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.code").value(savedModuleEntity.getCode())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.name").value(testModuleDto.getName())
        );
    }

    @Test
    public void testPartialUpdateModuleWhenNoModuleExists() throws Exception {
        ModuleDto testModuleDto = TestDataUtil.createTestModuleDtoB(null);
        String moduleUpdateJson = objectMapper.writeValueAsString(testModuleDto);

        mockMvc.perform(
                MockMvcRequestBuilders.patch("/modules/COM2131")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(moduleUpdateJson)
        ).andExpect(
                MockMvcResultMatchers.status().isNotFound()
        );
    }

    @Test
    public void testDeleteModuleWhenModuleExists() throws Exception {
        ModuleEntity testModuleEntity = TestDataUtil.createTestModuleEntityA(null);
        moduleService.save(testModuleEntity.getCode(), testModuleEntity);

        mockMvc.perform(
                MockMvcRequestBuilders.delete("/modules/" + testModuleEntity.getCode())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isNoContent()
        );
    }


    @Test
    public void testDeleteModuleWhenNoModuleExists() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/modules/COM2131")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isNoContent()
        );
    }

}
