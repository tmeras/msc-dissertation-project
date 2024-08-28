package com.theodoremeras.dissertation.unit_tests.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.theodoremeras.dissertation.TestDataUtil;
import com.theodoremeras.dissertation.student_information.*;
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

import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@WebMvcTest(StudentInformationController.class)
@AutoConfigureMockMvc(addFilters = false) // circumvent spring security for unit tests
public class StudentInformationControllerUnitTests {

    @MockBean
    private StudentInformationService studentInformationService;

    @MockBean
    private UserService userService;

    @MockBean
    private StudentInformationMapper studentInformationMapper;

    @MockBean
    private JwtDecoder jwtDecoder;

    private final MockMvc mockMvc;

    private final ObjectMapper objectMapper;

    private UserEntity testUserEntity;

    private StudentInformationEntity testStudentInformationEntity;

    private StudentInformationDto testStudentInformationDto;

    @Autowired
    public StudentInformationControllerUnitTests(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @BeforeEach
    public void setUp() {
        // Initialize test objects
        testUserEntity = TestDataUtil
                .createTestUserEntityA(TestDataUtil.createTestRoleEntityA(), TestDataUtil.createTestDepartmentEntityA());
        testStudentInformationEntity = TestDataUtil.createTestStudentInformationEntityA(testUserEntity);
        testStudentInformationDto = TestDataUtil.createTestStudentInformationDtoA(testUserEntity.getId());
    }

    @Test
    public void testCreateStudentInformation() throws Exception {
        String studentInformationJson = objectMapper.writeValueAsString(testStudentInformationDto);

        when(studentInformationMapper.mapFromDto(any())).thenReturn(testStudentInformationEntity);
        when(userService.findOneById(testUserEntity.getId())).thenReturn(Optional.of(testUserEntity));
        when(studentInformationService.save(testStudentInformationEntity)).thenReturn(testStudentInformationEntity);
        when(studentInformationMapper.mapToDto(testStudentInformationEntity)).thenReturn(testStudentInformationDto);

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
                        .value(testUserEntity.getId())
        );
    }

    @Test
    public void testCreateStudentInformationWhenNoStudentIsSpecified() throws Exception {
        testStudentInformationDto.setStudentId(null);
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
        String studentInformationJson = objectMapper.writeValueAsString(testStudentInformationDto);

        when(studentInformationMapper.mapFromDto(any())).thenReturn(testStudentInformationEntity);
        when(userService.findOneById(testUserEntity.getId())).thenReturn(Optional.empty());

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
        Jwt mockJwt = mock(Jwt.class);

        when(jwtDecoder.decode("token")).thenReturn(mockJwt);
        when(mockJwt.getClaim("userId")).thenReturn(Long.valueOf(testUserEntity.getId()));
        when(mockJwt.getClaim("roles")).thenReturn("Administrator");
        when(studentInformationService.findAll()).thenReturn(List.of(testStudentInformationEntity));
        when(studentInformationMapper.mapToDto(testStudentInformationEntity)).thenReturn(testStudentInformationDto);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/student-information")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer token")
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].id")
                        .value(testStudentInformationEntity.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].hasHealthIssues")
                        .value(testStudentInformationEntity.getHasHealthIssues())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].hasDisability")
                        .value(testStudentInformationEntity.getHasDisability())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].hasLsp")
                        .value(testStudentInformationEntity.getHasLsp())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].additionalDetails")
                        .value(testStudentInformationEntity.getAdditionalDetails())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].studentId")
                        .value(testUserEntity.getId())
        );
    }

    @Test
    public void testGetAllStudentInformationWhenForbidden() throws Exception {
        Jwt mockJwt = mock(Jwt.class);

        when(jwtDecoder.decode("token")).thenReturn(mockJwt);
        when(mockJwt.getClaim("userId")).thenReturn(Long.valueOf(testUserEntity.getId()));
        when(mockJwt.getClaim("roles")).thenReturn("Student");

        mockMvc.perform(
                MockMvcRequestBuilders.get("/student-information")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer token")
        ).andExpect(
                MockMvcResultMatchers.status().isForbidden()
        );
    }

    @Test
    public void testGetAllStudentInformationByStudentId() throws Exception {
        Jwt mockJwt = mock(Jwt.class);

        when(jwtDecoder.decode("token")).thenReturn(mockJwt);
        when(mockJwt.getClaim("userId")).thenReturn(Long.valueOf(testUserEntity.getId()));
        when(mockJwt.getClaim("roles")).thenReturn("Administrator");
        when(studentInformationService.findOneByStudentId(testUserEntity.getId()))
                .thenReturn(Optional.of(testStudentInformationEntity));
        when(studentInformationMapper.mapToDto(testStudentInformationEntity)).thenReturn(testStudentInformationDto);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/student-information?studentId=" + testUserEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer token")
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].id")
                        .value(testStudentInformationEntity.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].hasHealthIssues")
                        .value(testStudentInformationEntity.getHasHealthIssues())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].hasDisability")
                        .value(testStudentInformationEntity.getHasDisability())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].hasLsp")
                        .value(testStudentInformationEntity.getHasLsp())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].additionalDetails")
                        .value(testStudentInformationEntity.getAdditionalDetails())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].studentId")
                        .value(testUserEntity.getId())
        );
    }

    @Test
    public void testGetAllStudentInformationByStudentIdWhenForbidden() throws Exception {
        Jwt mockJwt = mock(Jwt.class);

        when(jwtDecoder.decode("token")).thenReturn(mockJwt);
        when(mockJwt.getClaim("userId")).thenReturn(5L);
        when(mockJwt.getClaim("roles")).thenReturn("Student");

        mockMvc.perform(
                MockMvcRequestBuilders.get("/student-information?studentId=" + testUserEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer token")
        ).andExpect(
                MockMvcResultMatchers.status().isForbidden()
        );
    }

    @Test
    public void testPartialUpdateStudentInformation() throws Exception {
        StudentInformationEntity updatedStudentInformationEntity =
                TestDataUtil.createTestStudentInformationEntityB(testUserEntity);
        updatedStudentInformationEntity.setId(testStudentInformationEntity.getId());
        StudentInformationDto updatedStudentInformationDto =
                TestDataUtil.createTestStudentInformationDtoB(testUserEntity.getId());
        updatedStudentInformationDto.setId(testStudentInformationEntity.getId());
        String studentInformationUpdateJson = objectMapper.writeValueAsString(updatedStudentInformationDto);
        Jwt mockJwt = mock(Jwt.class);

        when(jwtDecoder.decode("token")).thenReturn(mockJwt);
        when(mockJwt.getClaim("userId")).thenReturn(Long.valueOf(testUserEntity.getId()));
        when(mockJwt.getClaim("roles")).thenReturn("Administrator");
        when(studentInformationService.exists(testStudentInformationEntity.getId())).thenReturn(true);
        when(studentInformationService.findOneById(testStudentInformationEntity.getId()))
                .thenReturn(Optional.of(testStudentInformationEntity));
        when(studentInformationMapper.mapFromDto(any())).thenReturn(updatedStudentInformationEntity);
        when(studentInformationService.partialUpdate(testStudentInformationEntity.getId(), updatedStudentInformationEntity))
                .thenReturn(updatedStudentInformationEntity);
        when(studentInformationMapper.mapToDto(updatedStudentInformationEntity)).thenReturn(updatedStudentInformationDto);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .patch("/student-information/" + testUserEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(studentInformationUpdateJson)
                        .header("Authorization", "Bearer token")
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.id")
                        .value(testStudentInformationEntity.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.hasHealthIssues")
                        .value(updatedStudentInformationDto.getHasHealthIssues())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.hasDisability")
                        .value(updatedStudentInformationDto.getHasDisability())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.hasLsp")
                        .value(updatedStudentInformationDto.getHasLsp())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.additionalDetails")
                        .value(updatedStudentInformationDto.getAdditionalDetails())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.studentId")
                        .value(testUserEntity.getId())
        );

    }

    @Test
    public void testPartialUpdateStudentInformationWhenNoStudentInformationExists() throws Exception {
        StudentInformationDto updatedStudentInformationDto =
                TestDataUtil.createTestStudentInformationDtoB(testUserEntity.getId());
        updatedStudentInformationDto.setId(testStudentInformationEntity.getId());
        String studentInformationUpdateJson = objectMapper.writeValueAsString(updatedStudentInformationDto);
        Jwt mockJwt = mock(Jwt.class);

        when(jwtDecoder.decode("token")).thenReturn(mockJwt);
        when(mockJwt.getClaim("userId")).thenReturn(Long.valueOf(testUserEntity.getId()));
        when(mockJwt.getClaim("roles")).thenReturn("Administrator");
        when(studentInformationService.exists(testStudentInformationEntity.getId())).thenReturn(false);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .patch("/student-information/" + testUserEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(studentInformationUpdateJson)
                        .header("Authorization", "Bearer token")
        ).andExpect(
                MockMvcResultMatchers.status().isNotFound()
        );
    }

    @Test
    public void testPartialUpdateStudentInformationWhenForbidden() throws Exception {
        StudentInformationDto updatedStudentInformationDto =
                TestDataUtil.createTestStudentInformationDtoB(testUserEntity.getId());
        updatedStudentInformationDto.setId(testStudentInformationEntity.getId());
        String studentInformationUpdateJson = objectMapper.writeValueAsString(updatedStudentInformationDto);
        Jwt mockJwt = mock(Jwt.class);

        when(jwtDecoder.decode("token")).thenReturn(mockJwt);
        when(mockJwt.getClaim("userId")).thenReturn(5L);
        when(mockJwt.getClaim("roles")).thenReturn("Student");
        when(studentInformationService.exists(testStudentInformationEntity.getId())).thenReturn(true);
        when(studentInformationService.findOneById(testStudentInformationEntity.getId()))
                .thenReturn(Optional.of(testStudentInformationEntity));

        mockMvc.perform(
                MockMvcRequestBuilders
                        .patch("/student-information/" + testUserEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(studentInformationUpdateJson)
                        .header("Authorization", "Bearer token")
        ).andExpect(
                MockMvcResultMatchers.status().isForbidden()
        );
    }

    @Test
    public void testDeleteStudentInformation() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders
                        .delete("/student-information/" + testStudentInformationEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isNoContent()
        );

        verify(studentInformationService, times(1)).delete(testStudentInformationEntity.getId());
    }

}
