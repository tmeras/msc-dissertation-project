package com.theodoremeras.dissertation.unit_tests.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.theodoremeras.dissertation.TestDataUtil;
import com.theodoremeras.dissertation.ec_application.*;
import com.theodoremeras.dissertation.user.UserEntity;
import com.theodoremeras.dissertation.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@WebMvcTest(EcApplicationController.class)
@AutoConfigureMockMvc(addFilters = false) // circumvent spring security for unit tests
public class EcApplicationControllerUnitTests {

    @MockBean
    private EcApplicationService ecApplicationService;

    @MockBean
    private UserService userService;

    @MockBean
    private EcApplicationMapper ecApplicationMapper;

    @MockBean
    private JwtDecoder jwtDecoder;

    private final MockMvc mockMvc;

    private final ObjectMapper objectMapper;

    private UserEntity testUserEntity;

    private EcApplicationEntity testEcApplicationEntity;

    private EcApplicationDto testEcApplicationDto;

    @Autowired
    public EcApplicationControllerUnitTests(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @BeforeEach
    public void setUp() {
        // Initialize test objects
        testUserEntity = TestDataUtil
                .createTestUserEntityA(TestDataUtil.createTestRoleEntityA(), TestDataUtil.createTestDepartmentEntityA());
        testEcApplicationEntity = TestDataUtil.createTestEcApplicationEntityA(testUserEntity);
        testEcApplicationDto = TestDataUtil.createTestEcApplicationDtoA(testUserEntity.getId());
    }

    @Test
    public void testCreateEcApplication() throws Exception {
        String ecApplicationJson = objectMapper.writeValueAsString(testEcApplicationDto);

        when(ecApplicationMapper.mapFromDto(any())).thenReturn(testEcApplicationEntity);
        when(userService.findOneById(testEcApplicationEntity.getStudent().getId()))
                .thenReturn(Optional.of(testUserEntity));
        when(ecApplicationService.save(testEcApplicationEntity)).thenReturn(testEcApplicationEntity);
        when(ecApplicationMapper.mapToDto(testEcApplicationEntity)).thenReturn(testEcApplicationDto);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/ec-applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ecApplicationJson)
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
                        .value(testUserEntity.getId())
        );
    }

    @Test
    public void testCreateEcApplicationWhenNoStudentIsSpecified() throws Exception {
        testEcApplicationDto.setStudentId(null);
        String ecApplicationJson = objectMapper.writeValueAsString(testEcApplicationDto);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/ec-applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ecApplicationJson)
        ).andExpect(
                MockMvcResultMatchers.status().isBadRequest()
        );
    }

    @Test
    public void testCreateEcApplicationWhenNoStudentExists() throws Exception {
        String ecApplicationJson = objectMapper.writeValueAsString(testEcApplicationDto);

        when(ecApplicationMapper.mapFromDto(any())).thenReturn(testEcApplicationEntity);
        when(userService.findOneById(testEcApplicationEntity.getStudent().getId()))
                .thenReturn(Optional.empty());

        mockMvc.perform(
                MockMvcRequestBuilders.post("/ec-applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ecApplicationJson)
        ).andExpect(
                MockMvcResultMatchers.status().isNotFound()
        );

    }

    @Test
    public void testGetAllEcApplications() throws Exception {
        Jwt mockJwt = mock(Jwt.class);

        when(jwtDecoder.decode("token")).thenReturn(mockJwt);
        when(mockJwt.getClaim("userId")).thenReturn(Long.valueOf(testUserEntity.getId()));
        when(mockJwt.getClaim("roles")).thenReturn("Administrator");
        when(ecApplicationService.findAll()).thenReturn(List.of(testEcApplicationEntity));
        when(ecApplicationMapper.mapToDto(testEcApplicationEntity)).thenReturn(testEcApplicationDto);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/ec-applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer token")
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].id")
                        .value(testEcApplicationEntity.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].circumstancesDetails")
                        .value(testEcApplicationEntity.getCircumstancesDetails())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].requiresFurtherEvidence")
                        .value(testEcApplicationEntity.getRequiresFurtherEvidence())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].isReferred")
                        .value(testEcApplicationEntity.getIsReferred())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].studentId")
                        .value(testEcApplicationEntity.getId())
        );
    }

    @Test
    public void testGetAllEcApplicationsWhenForbidden() throws Exception {
        Jwt mockJwt = mock(Jwt.class);

        when(jwtDecoder.decode("token")).thenReturn(mockJwt);
        when(mockJwt.getClaim("userId")).thenReturn(Long.valueOf(testUserEntity.getId()));
        when(mockJwt.getClaim("roles")).thenReturn("Student");

        mockMvc.perform(
                MockMvcRequestBuilders.get("/ec-applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer token")
        ).andExpect(
                MockMvcResultMatchers.status().isForbidden()
        );
    }

    @Test
    public void testGetAllEcApplicationsByIds() throws Exception {
        Jwt mockJwt = mock(Jwt.class);

        when(jwtDecoder.decode("token")).thenReturn(mockJwt);
        when(mockJwt.getClaim("userId")).thenReturn(Long.valueOf(testUserEntity.getId()));
        when(mockJwt.getClaim("roles")).thenReturn("Administrator");
        when(ecApplicationService.findAllByIdIn(List.of(testEcApplicationEntity.getId())))
                .thenReturn(List.of(testEcApplicationEntity));
        when(ecApplicationMapper.mapToDto(testEcApplicationEntity)).thenReturn(testEcApplicationDto);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/ec-applications?ids=" + testEcApplicationEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer token")
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].id")
                        .value(testEcApplicationEntity.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].circumstancesDetails")
                        .value(testEcApplicationEntity.getCircumstancesDetails())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].requiresFurtherEvidence")
                        .value(testEcApplicationEntity.getRequiresFurtherEvidence())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].isReferred")
                        .value(testEcApplicationEntity.getIsReferred())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].studentId")
                        .value(testEcApplicationEntity.getId())
        );
    }

    @Test
    public void testGetAllEcApplicationsByIdsWhenForbidden() throws Exception {
        Jwt mockJwt = mock(Jwt.class);

        when(jwtDecoder.decode("token")).thenReturn(mockJwt);
        when(mockJwt.getClaim("userId")).thenReturn(Long.valueOf(testUserEntity.getId()));
        when(mockJwt.getClaim("roles")).thenReturn("Student");

        mockMvc.perform(
                MockMvcRequestBuilders.get("/ec-applications?ids=" + testEcApplicationEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer token")
        ).andExpect(
                MockMvcResultMatchers.status().isForbidden()
        );
    }

    @Test
    public void testGetAllEcApplicationsByStudentId() throws Exception {
        Jwt mockJwt = mock(Jwt.class);

        when(jwtDecoder.decode("token")).thenReturn(mockJwt);
        when(mockJwt.getClaim("userId")).thenReturn(Long.valueOf(testUserEntity.getId()));
        when(mockJwt.getClaim("roles")).thenReturn("Administrator");
        when(ecApplicationService.findAllByStudentId(testUserEntity.getId()))
                .thenReturn(List.of(testEcApplicationEntity));
        when(ecApplicationMapper.mapToDto(testEcApplicationEntity)).thenReturn(testEcApplicationDto);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/ec-applications?studentId=" + testUserEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer token")
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].id")
                        .value(testEcApplicationEntity.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].circumstancesDetails")
                        .value(testEcApplicationEntity.getCircumstancesDetails())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].requiresFurtherEvidence")
                        .value(testEcApplicationEntity.getRequiresFurtherEvidence())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].isReferred")
                        .value(testEcApplicationEntity.getIsReferred())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].studentId")
                        .value(testEcApplicationEntity.getId())
        );
    }

    @Test
    public void testGetAllEcApplicationsByStudentIdWhenForbidden() throws Exception {
        Jwt mockJwt = mock(Jwt.class);

        when(jwtDecoder.decode("token")).thenReturn(mockJwt);
        when(mockJwt.getClaim("userId")).thenReturn(5L);
        when(mockJwt.getClaim("roles")).thenReturn("Student");

        mockMvc.perform(
                MockMvcRequestBuilders.get("/ec-applications?studentId=" + testUserEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer token")
        ).andExpect(
                MockMvcResultMatchers.status().isForbidden()
        );
    }

    @Test
    public void testGetAllEcApplicationsByStudentDepartmentIdAndIsReferred() throws Exception {
        Jwt mockJwt = mock(Jwt.class);

        when(jwtDecoder.decode("token")).thenReturn(mockJwt);
        when(mockJwt.getClaim("userId")).thenReturn(Long.valueOf(testUserEntity.getId()));
        when(mockJwt.getClaim("roles")).thenReturn("Administrator");
        when(ecApplicationService
                .findAllByStudentDepartmentIdAndIsReferred(testUserEntity.getDepartment().getId(), testEcApplicationEntity.getIsReferred()))
                .thenReturn(List.of(testEcApplicationEntity));
        when(ecApplicationMapper.mapToDto(testEcApplicationEntity)).thenReturn(testEcApplicationDto);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/ec-applications?studentDepartmentId=" + testUserEntity.getDepartment().getId()
                                + "&isReferred=" + testEcApplicationEntity.getIsReferred())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer token")
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].id")
                        .value(testEcApplicationEntity.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].circumstancesDetails")
                        .value(testEcApplicationEntity.getCircumstancesDetails())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].requiresFurtherEvidence")
                        .value(testEcApplicationEntity.getRequiresFurtherEvidence())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].isReferred")
                        .value(testEcApplicationEntity.getIsReferred())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].studentId")
                        .value(testEcApplicationEntity.getId())
        );
    }

    @Test
    public void testGetAllEcApplicationsByStudentDepartmentIdAndIsReferredWhenForbidden() throws Exception {
        Jwt mockJwt = mock(Jwt.class);

        when(jwtDecoder.decode("token")).thenReturn(mockJwt);
        when(mockJwt.getClaim("userId")).thenReturn(Long.valueOf(testUserEntity.getId()));
        when(mockJwt.getClaim("roles")).thenReturn("Student");

        mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/ec-applications?studentDepartmentId=" + testUserEntity.getDepartment().getId()
                                + "&isReferred=" + testEcApplicationEntity.getIsReferred())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer token")
        ).andExpect(
                MockMvcResultMatchers.status().isForbidden()
        );
    }

    @Test
    public void testGetAllEcApplicationsByStudentDepartmentId() throws Exception {
        Jwt mockJwt = mock(Jwt.class);

        when(jwtDecoder.decode("token")).thenReturn(mockJwt);
        when(mockJwt.getClaim("userId")).thenReturn(Long.valueOf(testUserEntity.getId()));
        when(mockJwt.getClaim("roles")).thenReturn("Administrator");
        when(ecApplicationService.findAllByStudentDepartmentId(testUserEntity.getDepartment().getId()))
                .thenReturn(List.of(testEcApplicationEntity));
        when(ecApplicationMapper.mapToDto(testEcApplicationEntity)).thenReturn(testEcApplicationDto);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/ec-applications?studentDepartmentId=" + testUserEntity.getDepartment().getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer token")
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].id")
                        .value(testEcApplicationEntity.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].circumstancesDetails")
                        .value(testEcApplicationEntity.getCircumstancesDetails())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].requiresFurtherEvidence")
                        .value(testEcApplicationEntity.getRequiresFurtherEvidence())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].isReferred")
                        .value(testEcApplicationEntity.getIsReferred())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].studentId")
                        .value(testEcApplicationEntity.getId())
        );
    }

    @Test
    public void testGetAllEcApplicationsByStudentDepartmentIdWhenForbidden() throws Exception {
        Jwt mockJwt = mock(Jwt.class);

        when(jwtDecoder.decode("token")).thenReturn(mockJwt);
        when(mockJwt.getClaim("userId")).thenReturn(Long.valueOf(testUserEntity.getId()));
        when(mockJwt.getClaim("roles")).thenReturn("Student");

        mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/ec-applications?studentDepartmentId=" + testUserEntity.getDepartment().getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer token")
        ).andExpect(
                MockMvcResultMatchers.status().isForbidden()
        );
    }

    @Test
    public void testGetEcApplicationById() throws Exception {
        Jwt mockJwt = mock(Jwt.class);

        when(jwtDecoder.decode("token")).thenReturn(mockJwt);
        when(mockJwt.getClaim("userId")).thenReturn(Long.valueOf(testUserEntity.getId()));
        when(mockJwt.getClaim("roles")).thenReturn("Administrator");
        when(ecApplicationService.findOneById(testEcApplicationEntity.getId()))
                .thenReturn(Optional.of(testEcApplicationEntity));
        when(ecApplicationMapper.mapToDto(testEcApplicationEntity)).thenReturn(testEcApplicationDto);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/ec-applications/" + testEcApplicationEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer token" )
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.id")
                        .value(testEcApplicationEntity.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.circumstancesDetails")
                        .value(testEcApplicationEntity.getCircumstancesDetails())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.requiresFurtherEvidence")
                        .value(testEcApplicationEntity.getRequiresFurtherEvidence())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.isReferred")
                        .value(testEcApplicationEntity.getIsReferred())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.studentId")
                        .value(testUserEntity.getId())
        );
    }

    @Test
    public void testGetEcApplicationByIdWhenNoUserExists() throws Exception {
        Jwt mockJwt = mock(Jwt.class);

        when(jwtDecoder.decode("token")).thenReturn(mockJwt);
        when(mockJwt.getClaim("userId")).thenReturn(Long.valueOf(testUserEntity.getId()));
        when(mockJwt.getClaim("roles")).thenReturn("Administrator");
        when(ecApplicationService.findOneById(testEcApplicationEntity.getId())).thenReturn(Optional.empty());

        mockMvc.perform(
                MockMvcRequestBuilders.get("/ec-applications/" + testEcApplicationEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer token" )
        ).andExpect(
                MockMvcResultMatchers.status().isNotFound()
        );
    }

    @Test
    public void testGetEcApplicationByIdWhenForbidden() throws Exception {
        Jwt mockJwt = mock(Jwt.class);

        when(jwtDecoder.decode("token")).thenReturn(mockJwt);
        when(mockJwt.getClaim("userId")).thenReturn(5L);
        when(mockJwt.getClaim("roles")).thenReturn("Student");
        when(ecApplicationService.findOneById(testEcApplicationEntity.getId()))
                .thenReturn(Optional.of(testEcApplicationEntity));

        mockMvc.perform(
                MockMvcRequestBuilders.get("/ec-applications/" + testEcApplicationEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer token" )
        ).andExpect(
                MockMvcResultMatchers.status().isForbidden()
        );
    }

    @Test
    public void testPartialUpdateEcApplication() throws Exception {
        EcApplicationEntity updatedEcApplicationEntity = TestDataUtil.createTestEcApplicationEntityB(testUserEntity);
        updatedEcApplicationEntity.setId(testEcApplicationEntity.getId());
        EcApplicationDto updatedEcApplicationDto = TestDataUtil.createTestEcApplicationDtoB(testUserEntity.getId());
        updatedEcApplicationDto.setId(testEcApplicationEntity.getId());
        String ecApplicationUpdateJson = objectMapper.writeValueAsString(updatedEcApplicationDto);
        Jwt mockJwt = mock(Jwt.class);

        when(jwtDecoder.decode("token")).thenReturn(mockJwt);
        when(mockJwt.getClaim("userId")).thenReturn(Long.valueOf(testUserEntity.getId()));
        when(mockJwt.getClaim("roles")).thenReturn("Administrator");
        when(ecApplicationService.exists(testEcApplicationEntity.getId())).thenReturn(true);
        when(ecApplicationMapper.mapFromDto(any())).thenReturn(updatedEcApplicationEntity);
        when(ecApplicationService.partialUpdate(testEcApplicationEntity.getId(), updatedEcApplicationEntity))
                .thenReturn(updatedEcApplicationEntity);
        when(ecApplicationMapper.mapToDto(updatedEcApplicationEntity)).thenReturn(updatedEcApplicationDto);

         mockMvc.perform(
                MockMvcRequestBuilders.patch("/ec-applications/" + testEcApplicationEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ecApplicationUpdateJson)
                        .header("Authorization", "Bearer token")
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.circumstancesDetails")
                        .value(updatedEcApplicationDto.getCircumstancesDetails())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.requiresFurtherEvidence")
                        .value(updatedEcApplicationDto.getRequiresFurtherEvidence())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.isReferred")
                        .value(updatedEcApplicationDto.getIsReferred())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.studentId")
                        .value(testUserEntity.getId())
        );
    }

    @Test
    public void testPartialUpdateEcApplicationWhenNoApplicationExists() throws Exception {
        EcApplicationDto updatedEcApplicationDto = TestDataUtil.createTestEcApplicationDtoB(testUserEntity.getId());
        updatedEcApplicationDto.setId(testEcApplicationEntity.getId());
        String ecApplicationUpdateJson = objectMapper.writeValueAsString(updatedEcApplicationDto);
        Jwt mockJwt = mock(Jwt.class);

        when(jwtDecoder.decode("token")).thenReturn(mockJwt);
        when(mockJwt.getClaim("userId")).thenReturn(Long.valueOf(testUserEntity.getId()));
        when(mockJwt.getClaim("roles")).thenReturn("Administrator");
        when(ecApplicationService.exists(testEcApplicationEntity.getId())).thenReturn(false);

         mockMvc.perform(
                MockMvcRequestBuilders.patch("/ec-applications/" + testEcApplicationEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ecApplicationUpdateJson)
                        .header("Authorization", "Bearer token")
        ).andExpect(
                MockMvcResultMatchers.status().isNotFound()
        );
    }

    @Test
    public void testPartialUpdateEcApplicationWhenForbidden() throws Exception {
        EcApplicationDto updatedEcApplicationDto = TestDataUtil.createTestEcApplicationDtoB(testUserEntity.getId());
        updatedEcApplicationDto.setId(testEcApplicationEntity.getId());
        String ecApplicationUpdateJson = objectMapper.writeValueAsString(updatedEcApplicationDto);
        Jwt mockJwt = mock(Jwt.class);

        when(jwtDecoder.decode("token")).thenReturn(mockJwt);
        when(mockJwt.getClaim("userId")).thenReturn(5L);
        when(mockJwt.getClaim("roles")).thenReturn("Student");
        when(ecApplicationService.exists(testEcApplicationEntity.getId())).thenReturn(true);
        when(ecApplicationService.findOneById(testEcApplicationEntity.getId()))
                .thenReturn(Optional.of(testEcApplicationEntity));

         mockMvc.perform(
                MockMvcRequestBuilders.patch("/ec-applications/" + testEcApplicationEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ecApplicationUpdateJson)
                        .header("Authorization", "Bearer token")
        ).andExpect(
                MockMvcResultMatchers.status().isForbidden()
        );
    }

    @Test
    public void testDeleteEcApplication() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/ec-applications/" + testEcApplicationEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isNoContent()
        );

        verify(ecApplicationService, times(1)).delete(testEcApplicationEntity.getId());
    }

}
