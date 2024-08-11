package com.theodoremeras.dissertation.integration_tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.theodoremeras.dissertation.ParentCreationService;
import com.theodoremeras.dissertation.TestDataUtil;
import com.theodoremeras.dissertation.department.DepartmentEntity;
import com.theodoremeras.dissertation.department.DepartmentService;
import com.theodoremeras.dissertation.ec_application.EcApplicationDto;
import com.theodoremeras.dissertation.ec_application.EcApplicationEntity;
import com.theodoremeras.dissertation.ec_application.EcApplicationService;
import com.theodoremeras.dissertation.role.RoleEntity;
import com.theodoremeras.dissertation.role.RoleService;
import com.theodoremeras.dissertation.user.UserEntity;
import com.theodoremeras.dissertation.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.Instant;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@WithMockUser(roles = {"Administrator"})
public class EcApplicationControllerIntegrationTests {

    private EcApplicationService ecApplicationService;

    private ParentCreationService parentCreationService;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private JwtEncoder jwtEncoder;

    @Autowired
    public EcApplicationControllerIntegrationTests(
            EcApplicationService ecApplicationService, ParentCreationService parentCreationService,
            MockMvc mockMvc, ObjectMapper objectMapper, JwtEncoder jwtEncoder
    ) {
        this.ecApplicationService = ecApplicationService;
        this.parentCreationService = parentCreationService;
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.jwtEncoder = jwtEncoder;
    }

    @Test
    public void testCreateEcApplication() throws Exception {
        UserEntity savedUser = parentCreationService.createUserParentEntity();

        EcApplicationDto testEcApplicationDto = TestDataUtil.createTestEcApplicationDtoA(savedUser.getId());
        String applicationJson = objectMapper.writeValueAsString(testEcApplicationDto);

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
                        .value(testEcApplicationDto.getCircumstancesDetails())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.requiresFurtherEvidence")
                        .value(testEcApplicationDto.getRequiresFurtherEvidence())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.studentId")
                        .value(savedUser.getId())
        );
    }

    @Test
    public void testCreateEcApplicationWhenNoStudentIsSpecified() throws Exception {
        EcApplicationDto testEcApplicationDto = TestDataUtil.createTestEcApplicationDtoA(null);
        String applicationJson = objectMapper.writeValueAsString(testEcApplicationDto);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/ec-applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(applicationJson)
        ).andExpect(
                MockMvcResultMatchers.status().isBadRequest()
        );
    }

    @Test
    public void testCreateEcApplicationWhenNoStudentExists() throws Exception {
        EcApplicationDto testEcApplicationDto = TestDataUtil.createTestEcApplicationDtoA(1);
        String applicationJson = objectMapper.writeValueAsString(testEcApplicationDto);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/ec-applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(applicationJson)
        ).andExpect(
                MockMvcResultMatchers.status().isNotFound()
        );
    }

    @Test
    public void testGetAllEcApplications() throws Exception {
        UserEntity savedUser = parentCreationService.createUserParentEntity();

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

        EcApplicationEntity testEcApplicationA = TestDataUtil.createTestEcApplicationEntityA(savedUser);
        EcApplicationEntity savedEcApplicationA = ecApplicationService.save(testEcApplicationA);
        EcApplicationEntity testEcApplicationB = TestDataUtil.createTestEcApplicationEntityB(savedUser);
        EcApplicationEntity savedEcApplicationB = ecApplicationService.save(testEcApplicationB);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/ec-applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].id")
                        .value(savedEcApplicationA.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].circumstancesDetails")
                        .value(savedEcApplicationA.getCircumstancesDetails())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].requiresFurtherEvidence")
                        .value(savedEcApplicationA.getRequiresFurtherEvidence())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].isReferred")
                        .value(savedEcApplicationA.getIsReferred())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].studentId")
                        .value(savedUser.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].id").value(savedEcApplicationB.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].circumstancesDetails")
                        .value(savedEcApplicationB.getCircumstancesDetails())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].requiresFurtherEvidence")
                        .value(savedEcApplicationB.getRequiresFurtherEvidence())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].isReferred")
                        .value(savedEcApplicationB.getIsReferred())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].studentId")
                        .value(savedUser.getId())
        );
    }

    @Test
    public void testGetAllEcApplicationsWhenForbidden() throws Exception {

        // Build jwt with student role specified, as students
        // are not allowed to fetch all EC applications
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
                MockMvcRequestBuilders.get("/ec-applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
        ).andExpect(
                MockMvcResultMatchers.status().isForbidden()
        );
    }

    @Test
    public void testGetAllEcApplicationsByIds() throws Exception {
        UserEntity savedUser = parentCreationService.createUserParentEntity();

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

        EcApplicationEntity testEcApplicationA = TestDataUtil.createTestEcApplicationEntityA(savedUser);
        EcApplicationEntity savedEcApplicationA = ecApplicationService.save(testEcApplicationA);
        EcApplicationEntity testEcApplicationB = TestDataUtil.createTestEcApplicationEntityB(savedUser);
        EcApplicationEntity savedEcApplicationB = ecApplicationService.save(testEcApplicationB);

        mockMvc.perform(
                MockMvcRequestBuilders.get(
                        "/ec-applications?ids=" + savedEcApplicationA.getId() + ", "
                                + savedEcApplicationB.getId()
                ).contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].id")
                        .value(savedEcApplicationA.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].circumstancesDetails")
                        .value(savedEcApplicationA.getCircumstancesDetails())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].isReferred")
                        .value(savedEcApplicationA.getIsReferred())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].requiresFurtherEvidence")
                        .value(savedEcApplicationA.getRequiresFurtherEvidence())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].studentId")
                        .value(savedUser.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].id").value(savedEcApplicationB.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].circumstancesDetails")
                        .value(savedEcApplicationB.getCircumstancesDetails())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].requiresFurtherEvidence")
                        .value(savedEcApplicationB.getRequiresFurtherEvidence())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].isReferred")
                        .value(savedEcApplicationB.getIsReferred())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].studentId")
                        .value(savedUser.getId())
        );
    }

    @Test
    public void testGetAllEcApplicationsByIdsWhenForbidden() throws Exception {

        // Build jwt with student role specified, as students
        // are only allowed to fetch EC applications relevant to them
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
                MockMvcRequestBuilders.get("/ec-applications?ids=1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
        ).andExpect(
                MockMvcResultMatchers.status().isForbidden()
        );
    }

    @Test
    public void testGetAllEcApplicationsByStudentId() throws Exception {
        UserEntity savedUser = parentCreationService.createUserParentEntity();

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

        EcApplicationEntity testEcApplicationA = TestDataUtil.createTestEcApplicationEntityA(savedUser);
        EcApplicationEntity savedEcApplicationA = ecApplicationService.save(testEcApplicationA);
        EcApplicationEntity testEcApplicationB = TestDataUtil.createTestEcApplicationEntityB(savedUser);
        EcApplicationEntity savedEcApplicationB = ecApplicationService.save(testEcApplicationB);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/ec-applications?studentId=" + savedUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].id")
                        .value(savedEcApplicationA.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].circumstancesDetails")
                        .value(savedEcApplicationA.getCircumstancesDetails())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].requiresFurtherEvidence")
                        .value(savedEcApplicationA.getRequiresFurtherEvidence())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].isReferred")
                        .value(savedEcApplicationA.getIsReferred())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].studentId")
                        .value(savedUser.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].id").value(savedEcApplicationB.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].circumstancesDetails")
                        .value(savedEcApplicationB.getCircumstancesDetails())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].requiresFurtherEvidence")
                        .value(savedEcApplicationB.getRequiresFurtherEvidence())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].isReferred")
                        .value(savedEcApplicationB.getIsReferred())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].studentId")
                        .value(savedUser.getId())
        );
    }

    @Test
    public void testGetAllEcApplicationsByStudentIdWhenForbidden() throws Exception {

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


        // Request the EC applications of another student, students are not allowed to do
        mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/ec-applications?studentId=1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
        ).andExpect(
                MockMvcResultMatchers.status().isForbidden()
        );
    }

    @Test
    public void testGetAllEcApplicationsByStudentDepartmentIdAndIsReferred() throws Exception {
        UserEntity savedUser = parentCreationService.createUserParentEntity();

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

        EcApplicationEntity testEcApplicationA = TestDataUtil.createTestEcApplicationEntityA(savedUser);
        EcApplicationEntity savedEcApplicationA = ecApplicationService.save(testEcApplicationA);
        EcApplicationEntity testEcApplicationB = TestDataUtil.createTestEcApplicationEntityB(savedUser);
        EcApplicationEntity savedEcApplicationB = ecApplicationService.save(testEcApplicationB);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/ec-applications?studentDepartmentId=" + savedUser.getDepartment().getId()
                                + "&isReferred=true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].id")
                        .value(savedEcApplicationA.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].circumstancesDetails")
                        .value(savedEcApplicationA.getCircumstancesDetails())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].requiresFurtherEvidence")
                        .value(savedEcApplicationA.getRequiresFurtherEvidence())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].isReferred")
                        .value(savedEcApplicationA.getIsReferred())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].studentId")
                        .value(savedUser.getId())
        );
    }

    @Test
    public void testGetAllEcApplicationsByStudentDepartmentIdAndIsReferredWhenForbidden() throws Exception {

        // Build jwt with student role specified, as students are only allowed to
        // fetch EC applications relevant to them
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
                MockMvcRequestBuilders
                        .get("/ec-applications?studentDepartmentId=1&isReferred=true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
        ).andExpect(
                MockMvcResultMatchers.status().isForbidden()
        );
    }

    @Test
    public void testGetAllEcApplicationsByStudentDepartmentId() throws Exception {
        UserEntity savedUser = parentCreationService.createUserParentEntity();

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

        EcApplicationEntity testEcApplicationA = TestDataUtil.createTestEcApplicationEntityA(savedUser);
        EcApplicationEntity savedEcApplicationA = ecApplicationService.save(testEcApplicationA);
        EcApplicationEntity testEcApplicationB = TestDataUtil.createTestEcApplicationEntityB(savedUser);
        EcApplicationEntity savedEcApplicationB = ecApplicationService.save(testEcApplicationB);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/ec-applications?studentDepartmentId=" + savedUser.getDepartment().getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].id")
                        .value(savedEcApplicationA.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].circumstancesDetails")
                        .value(savedEcApplicationA.getCircumstancesDetails())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].requiresFurtherEvidence")
                        .value(savedEcApplicationA.getRequiresFurtherEvidence())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].isReferred")
                        .value(savedEcApplicationA.getIsReferred())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].studentId")
                        .value(savedUser.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].id").value(savedEcApplicationB.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].circumstancesDetails")
                        .value(savedEcApplicationB.getCircumstancesDetails())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].requiresFurtherEvidence")
                        .value(savedEcApplicationB.getRequiresFurtherEvidence())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].isReferred")
                        .value(savedEcApplicationB.getIsReferred())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[1].studentId")
                        .value(savedUser.getId())
        );
    }

    @Test
    public void testGetAllEcApplicationsByStudentDepartmentIdWhenForbidden() throws Exception {

        // Build jwt with student role specified, as students are only allowed to
        // fetch EC applications relevant to them
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
                MockMvcRequestBuilders
                        .get("/ec-applications?studentDepartmentId=1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
        ).andExpect(
                MockMvcResultMatchers.status().isForbidden()
        );
    }


    @Test
    public void testGetEcApplicationById() throws Exception {
        UserEntity savedUser = parentCreationService.createUserParentEntity();

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

        EcApplicationEntity testEcApplication = TestDataUtil.createTestEcApplicationEntityA(savedUser);
        EcApplicationEntity savedEcApplication = ecApplicationService.save(testEcApplication);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/ec-applications/" + savedEcApplication.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.id")
                        .value(savedEcApplication.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.circumstancesDetails")
                        .value(savedEcApplication.getCircumstancesDetails())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.requiresFurtherEvidence")
                        .value(savedEcApplication.getRequiresFurtherEvidence())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.isReferred")
                        .value(savedEcApplication.getIsReferred())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.studentId")
                        .value(savedUser.getId())
        );
    }

    @Test
    public void testGetEcApplicationByIdWhenNoApplicationExists() throws Exception {

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
                MockMvcRequestBuilders.get("/ec-applications/123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
        ).andExpect(
                MockMvcResultMatchers.status().isNotFound()
        );
    }

    @Test
    public void testGetEcApplicationByIdWhenForbidden() throws Exception {
        UserEntity savedUser = parentCreationService.createUserParentEntity();

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

        EcApplicationEntity testEcApplication = TestDataUtil.createTestEcApplicationEntityA(savedUser);
        EcApplicationEntity savedEcApplication = ecApplicationService.save(testEcApplication);

        // Request the EC application of another student, which students are not allowed to do
        mockMvc.perform(
                MockMvcRequestBuilders.get("/ec-applications/" + savedEcApplication.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
        ).andExpect(
                MockMvcResultMatchers.status().isForbidden()
        );
    }

    @Test
    public void testPartialUpdateEcApplication() throws Exception {
        UserEntity savedUser = parentCreationService.createUserParentEntity();

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

        EcApplicationEntity testEcApplication = TestDataUtil.createTestEcApplicationEntityA(savedUser);
        EcApplicationEntity savedEcApplication = ecApplicationService.save(testEcApplication);

        EcApplicationDto testEcApplicationDto = TestDataUtil.createTestEcApplicationDtoB(null);
        testEcApplicationDto.setCircumstancesDetails(testEcApplication.getCircumstancesDetails());
        String applicationUpdateJson = objectMapper.writeValueAsString(testEcApplicationDto);

        mockMvc.perform(
                MockMvcRequestBuilders.patch("/ec-applications/" + savedEcApplication.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(applicationUpdateJson)
                        .header("Authorization", "Bearer " + token)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.circumstancesDetails")
                        .value(testEcApplication.getCircumstancesDetails())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.requiresFurtherEvidence")
                        .value(testEcApplicationDto.getRequiresFurtherEvidence())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.isReferred")
                        .value(testEcApplicationDto.getIsReferred())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.studentId")
                        .value(savedUser.getId())
        );
    }

    @Test
    public void testPartialUpdateEcApplicationWhenNoApplicationExists() throws Exception {

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

        EcApplicationDto testEcApplicationDto = TestDataUtil.createTestEcApplicationDtoA(null);
        String applicationUpdateJson = objectMapper.writeValueAsString(testEcApplicationDto);

        mockMvc.perform(
                MockMvcRequestBuilders.patch("/ec-applications/123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(applicationUpdateJson)
                        .header("Authorization", "Bearer " + token)
        ).andExpect(
                MockMvcResultMatchers.status().isNotFound()
        );
    }

    @Test
    public void testPartialUpdateEcApplicationWhenForbidden() throws Exception {
        UserEntity savedUser = parentCreationService.createUserParentEntity();

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

        EcApplicationEntity testEcApplication = TestDataUtil.createTestEcApplicationEntityA(savedUser);
        EcApplicationEntity savedEcApplication = ecApplicationService.save(testEcApplication);

        EcApplicationDto testEcApplicationDto = TestDataUtil.createTestEcApplicationDtoB(null);
        testEcApplicationDto.setCircumstancesDetails(testEcApplication.getCircumstancesDetails());
        String applicationUpdateJson = objectMapper.writeValueAsString(testEcApplicationDto);

        // Request to update the EC application made by another student, which students are not allowed to do
        mockMvc.perform(
                MockMvcRequestBuilders.patch("/ec-applications/" + savedEcApplication.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(applicationUpdateJson)
                        .header("Authorization", "Bearer " + token)
        ).andExpect(
                MockMvcResultMatchers.status().isForbidden()
        );
    }

    @Test
    public void testDeleteEcApplication() throws Exception {
        UserEntity savedUser = parentCreationService.createUserParentEntity();

        EcApplicationEntity testEcApplication = TestDataUtil.createTestEcApplicationEntityA(savedUser);
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
