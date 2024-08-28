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
public class StudentInformationControllerIntegrationTests {

    private final StudentInformationService studentInformationService;

    private final ParentCreationService parentCreationService;

    private final MockMvc mockMvc;

    private final ObjectMapper objectMapper;

    private final JwtEncoder jwtEncoder;

    @Autowired
    public StudentInformationControllerIntegrationTests(
            StudentInformationService studentInformationService, ParentCreationService parentCreationService,
            ObjectMapper objectMapper, MockMvc mockMvc, JwtEncoder jwtEncoder
    ) {
        this.studentInformationService = studentInformationService;
        this.parentCreationService = parentCreationService;
        this.objectMapper = objectMapper;
        this.mockMvc = mockMvc;
        this.jwtEncoder = jwtEncoder;
    }

    @Test
    public void testCreateStudentInformation() throws Exception {
        UserEntity savedStudent = parentCreationService.createUserParentEntity();

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
        UserEntity savedStudent = parentCreationService.createUserParentEntity();

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
                        .header("Authorization", "Bearer " + token)
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
    public void testGetAllStudentInformationWhenForbidden() throws Exception {

        // Build jwt with student role specified, as students are not allowed
        // to fetch all student information
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


        mockMvc.perform(
                MockMvcRequestBuilders.get("/student-information")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
        ).andExpect(
                MockMvcResultMatchers.status().isForbidden()
        );
    }

    @Test
    public void testGetAllStudentInformationByStudentId() throws Exception {
        UserEntity savedStudent = parentCreationService.createUserParentEntity();

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
                        .header("Authorization", "Bearer " + token)
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
    public void testGetAllStudentInformationByStudentIdWhenForbidden() throws Exception {

        // Build jwt with student role specified
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(60))
                .subject("admin@admin.com")
                .claim("roles", "Student")
                .claim("userId", 1)
                .build();
        String token = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        // Request the information of another student, which students are not allowed to do
        mockMvc.perform(
                MockMvcRequestBuilders.get("/student-information?StudentId=2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
        ).andExpect(
                MockMvcResultMatchers.status().isForbidden()
        );
    }

    @Test
    public void testPartialUpdateStudentInformation() throws Exception {
        UserEntity savedStudent = parentCreationService.createUserParentEntity();

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
                        .header("Authorization", "Bearer " + token)
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

        StudentInformationDto testStudentInformationDto =
                TestDataUtil.createTestStudentInformationDtoB(null);
        String studentInformationUpdateJson = objectMapper.writeValueAsString(testStudentInformationDto);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .patch("/student-information/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(studentInformationUpdateJson)
                        .header("Authorization", "Bearer " + token)
        ).andExpect(
                MockMvcResultMatchers.status().isNotFound()
        );
    }

    @Test
    public void testPartialUpdateStudentInformationWhenForbidden() throws Exception {
        UserEntity savedStudent = parentCreationService.createUserParentEntity();

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

        StudentInformationEntity testStudentInformationEntity =
                TestDataUtil.createTestStudentInformationEntityA(savedStudent);
        studentInformationService.save(testStudentInformationEntity);

        StudentInformationDto testStudentInformationDto =
                TestDataUtil.createTestStudentInformationDtoB(null);
        String studentInformationUpdateJson = objectMapper.writeValueAsString(testStudentInformationDto);

        // Request to edit the information of another student, which students are not allowed to do
        mockMvc.perform(
                MockMvcRequestBuilders
                        .patch("/student-information/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(studentInformationUpdateJson)
                        .header("Authorization", "Bearer " + token)
        ).andExpect(
                MockMvcResultMatchers.status().isForbidden()
        );
    }

    @Test
    public void testDeleteStudentInformation() throws Exception {
        UserEntity savedStudent = parentCreationService.createUserParentEntity();

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
