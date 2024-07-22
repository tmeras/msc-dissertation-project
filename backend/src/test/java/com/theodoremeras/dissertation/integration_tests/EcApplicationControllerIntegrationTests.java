package com.theodoremeras.dissertation.integration_tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.theodoremeras.dissertation.TestDataUtil;
import com.theodoremeras.dissertation.ec_application.EcApplicationDto;
import com.theodoremeras.dissertation.ec_application.EcApplicationEntity;
import com.theodoremeras.dissertation.ec_application.EcApplicationService;
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

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Autowired
    public EcApplicationControllerIntegrationTests(
            EcApplicationService ecApplicationService, MockMvc mockMvc, ObjectMapper objectMapper
    ) {
        this.ecApplicationService = ecApplicationService;
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @Test
    public void testCreateEcApplication() throws Exception {
        EcApplicationEntity testEcApplication = TestDataUtil.createTestEcApplicationEntityA();
        String applicationJson = objectMapper.writeValueAsString(testEcApplication);

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
                        .value(testEcApplication.getCircumstancesDetails())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.additionalDetails")
                        .value(testEcApplication.getAdditionalDetails())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.isReferred")
                        .value(testEcApplication.getIsReferred())
        );
    }

    @Test
    public void testGetAllEcApplications() throws Exception {
        EcApplicationEntity testEcApplicationA = TestDataUtil.createTestEcApplicationEntityA();
        EcApplicationEntity savedEcApplicationA = ecApplicationService.save(testEcApplicationA);
        EcApplicationEntity testEcApplicationB = TestDataUtil.createTestEcApplicationEntityB();
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
    public void testGetEcApplication() throws Exception {
        EcApplicationEntity testEcApplication = TestDataUtil.createTestEcApplicationEntityA();
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
    public void testGetEcApplicationWhenNoApplicationExists() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/ec-applications/123")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isNotFound()
        );
    }

    @Test
    public void testPartialUpdateEcApplication() throws Exception {
        EcApplicationEntity testEcApplication = TestDataUtil.createTestEcApplicationEntityA();
        EcApplicationEntity savedEcApplication = ecApplicationService.save(testEcApplication);

        EcApplicationDto testEcApplicationDto = TestDataUtil.createTestEcApplicationDtoB();
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
        EcApplicationDto testEcApplicationDto = TestDataUtil.createTestEcApplicationDtoA();
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
        EcApplicationEntity testEcApplication = TestDataUtil.createTestEcApplicationEntityA();
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
