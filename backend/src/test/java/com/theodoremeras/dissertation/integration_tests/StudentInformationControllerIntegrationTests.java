package com.theodoremeras.dissertation.integration_tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.theodoremeras.dissertation.ParentCreationService;
import com.theodoremeras.dissertation.TestDataUtil;
import com.theodoremeras.dissertation.student_information.StudentInformationDto;
import com.theodoremeras.dissertation.student_information.StudentInformationEntity;
import com.theodoremeras.dissertation.student_information.StudentInformationService;
import com.theodoremeras.dissertation.user.UserEntity;
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
@WithMockUser(roles={"Administrator"})
public class StudentInformationControllerIntegrationTests {

    private StudentInformationService studentInformationService;

    private ParentCreationService parentCreationService;

    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @Autowired
    public StudentInformationControllerIntegrationTests(
            StudentInformationService studentInformationService, ParentCreationService parentCreationService,
            ObjectMapper objectMapper, MockMvc mockMvc
    ) {
        this.studentInformationService = studentInformationService;
        this.parentCreationService = parentCreationService;
        this.objectMapper = objectMapper;
        this.mockMvc = mockMvc;
    }

    @Test
    public void testCreateStudentInformation() throws Exception {
        UserEntity savedStudent =  parentCreationService.createUserParentEntity();

        StudentInformationDto testStudentInformationDto =
                TestDataUtil.createTestStudentInformationDtoA(savedStudent.getId());
        String studentInformationJson = objectMapper.writeValueAsString(testStudentInformationDto);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/student-information")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(studentInformationJson)
        ).andExpect(
                MockMvcResultMatchers.status().isCreated()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.id").isNumber()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.hasHealthIssues")
                        .value(testStudentInformationDto.getHasHealthIssues())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.hasDisability")
                        .value(testStudentInformationDto.getHasDisability())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.hasLsp")
                        .value(testStudentInformationDto.getHasLsp())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.additionalDetails")
                        .value(testStudentInformationDto.getAdditionalDetails())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.studentId")
                        .value(savedStudent.getId())
        );
    }

    @Test
    public void testCreateStudentInformationWhenNoStudentIsSpecified() throws Exception {
        StudentInformationDto testStudentInformationDto =
                TestDataUtil.createTestStudentInformationDtoA(null);
        String studentInformationJson = objectMapper.writeValueAsString(testStudentInformationDto);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/student-information")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(studentInformationJson)
        ).andExpect(
                MockMvcResultMatchers.status().isBadRequest()
        );
    }

    @Test
    public void testCreateStudentInformationWhenNoStudentExists() throws Exception {
        StudentInformationDto testStudentInformationDto =
                TestDataUtil.createTestStudentInformationDtoA(1);
        String studentInformationJson = objectMapper.writeValueAsString(testStudentInformationDto);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/student-information")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(studentInformationJson)
        ).andExpect(
                MockMvcResultMatchers.status().isNotFound()
        );
    }

    @Test
    public void testGetAllStudentInformation() throws Exception {
        UserEntity savedStudent =  parentCreationService.createUserParentEntity();

        StudentInformationEntity testStudentInformationEntityA =
                TestDataUtil.createTestStudentInformationEntityA(savedStudent);
        StudentInformationEntity savedStudentInformationEntityA =
                studentInformationService.save(testStudentInformationEntityA);
        StudentInformationEntity testStudentInformationEntityB =
                TestDataUtil.createTestStudentInformationEntityB(savedStudent);
        StudentInformationEntity savedStudentInformationEntityB =
                studentInformationService.save(testStudentInformationEntityB);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/student-information")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].id")
                        .value(savedStudentInformationEntityA.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].hasHealthIssues")
                        .value(savedStudentInformationEntityA.getHasHealthIssues())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].hasDisability")
                        .value(savedStudentInformationEntityA.getHasDisability())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].hasLsp")
                        .value(savedStudentInformationEntityA.getHasLsp())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].additionalDetails")
                        .value(savedStudentInformationEntityA.getAdditionalDetails())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].studentId")
                        .value(savedStudent.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].id")
                        .value(savedStudentInformationEntityB.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].hasHealthIssues")
                        .value(savedStudentInformationEntityB.getHasHealthIssues())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].hasDisability")
                        .value(savedStudentInformationEntityB.getHasDisability())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].hasLsp")
                        .value(savedStudentInformationEntityB.getHasLsp())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].additionalDetails")
                        .value(savedStudentInformationEntityB.getAdditionalDetails())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].studentId")
                        .value(savedStudent.getId())
        );
    }

  @Test
    public void testGetAllStudentInformationByStudentId() throws Exception {
        UserEntity savedStudent =  parentCreationService.createUserParentEntity();

        StudentInformationEntity testStudentInformationEntityA =
                TestDataUtil.createTestStudentInformationEntityA(savedStudent);
        StudentInformationEntity savedStudentInformationEntityA =
                studentInformationService.save(testStudentInformationEntityA);
        StudentInformationEntity testStudentInformationEntityB =
                TestDataUtil.createTestStudentInformationEntityB(savedStudent);
        StudentInformationEntity savedStudentInformationEntityB =
                studentInformationService.save(testStudentInformationEntityB);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/student-information?StudentId=" + savedStudent.getId())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].id")
                        .value(savedStudentInformationEntityA.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].hasHealthIssues")
                        .value(savedStudentInformationEntityA.getHasHealthIssues())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].hasDisability")
                        .value(savedStudentInformationEntityA.getHasDisability())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].hasLsp")
                        .value(savedStudentInformationEntityA.getHasLsp())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].additionalDetails")
                        .value(savedStudentInformationEntityA.getAdditionalDetails())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].studentId")
                        .value(savedStudent.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].id")
                        .value(savedStudentInformationEntityB.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].hasHealthIssues")
                        .value(savedStudentInformationEntityB.getHasHealthIssues())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].hasDisability")
                        .value(savedStudentInformationEntityB.getHasDisability())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].hasLsp")
                        .value(savedStudentInformationEntityB.getHasLsp())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].additionalDetails")
                        .value(savedStudentInformationEntityB.getAdditionalDetails())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].studentId")
                        .value(savedStudent.getId())
        );
    }

    @Test
    public void testPartialUpdateStudentInformation() throws Exception {
        UserEntity savedStudent =  parentCreationService.createUserParentEntity();

        StudentInformationEntity testStudentInformationEntity =
                TestDataUtil.createTestStudentInformationEntityA(savedStudent);
        StudentInformationEntity savedStudentInformationEntity =
                studentInformationService.save(testStudentInformationEntity);

        StudentInformationDto testStudentInformationDto =
                TestDataUtil.createTestStudentInformationDtoB(null);
        String studentInformationUpdateJson = objectMapper.writeValueAsString(testStudentInformationDto);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .patch("/student-information/" + savedStudentInformationEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(studentInformationUpdateJson)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.id")
                        .value(savedStudentInformationEntity.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.hasHealthIssues")
                        .value(testStudentInformationDto.getHasHealthIssues())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.hasDisability")
                        .value(testStudentInformationDto.getHasDisability())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.hasLsp")
                        .value(testStudentInformationDto.getHasLsp())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.additionalDetails")
                        .value(testStudentInformationDto.getAdditionalDetails())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.studentId")
                        .value(savedStudent.getId())
        );
    }

    @Test
    public void testPartialUpdateStudentInformationWhenNoStudentInformationExists() throws Exception {
        StudentInformationDto testStudentInformationDto =
                TestDataUtil.createTestStudentInformationDtoB(null);
        String studentInformationUpdateJson = objectMapper.writeValueAsString(testStudentInformationDto);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .patch("/student-information/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(studentInformationUpdateJson)
        ).andExpect(
                MockMvcResultMatchers.status().isNotFound()
        );
    }

    @Test
    public void testDeleteStudentInformation() throws Exception {
        UserEntity savedStudent =  parentCreationService.createUserParentEntity();

        StudentInformationEntity testStudentInformationEntity =
                TestDataUtil.createTestStudentInformationEntityA(savedStudent);
        StudentInformationEntity savedStudentInformationEntity =
                studentInformationService.save(testStudentInformationEntity);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .delete("/student-information/" + savedStudentInformationEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isNoContent()
        );
    }


    @Test
    public void testDeleteStudentInformationWhenNoStudentInformationExists() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/student-information/1")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isNoContent()
        );
    }

}
