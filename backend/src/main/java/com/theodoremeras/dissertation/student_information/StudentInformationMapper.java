package com.theodoremeras.dissertation.student_information;

import com.theodoremeras.dissertation.user.UserEntity;
import org.springframework.stereotype.Service;

@Service
public class StudentInformationMapper {

    public StudentInformationDto mapToDto(StudentInformationEntity studentInformationEntity) {
        return StudentInformationDto.builder()
                .id(studentInformationEntity.getId())
                .hasHealthIssues(studentInformationEntity.getHasHealthIssues())
                .hasDisability(studentInformationEntity.getHasDisability())
                .hasLsp(studentInformationEntity.getHasLsp())
                .additionalDetails(studentInformationEntity.getAdditionalDetails())
                .studentId(studentInformationEntity.getStudent().getId())
                .build();
    }

    public StudentInformationEntity mapFromDto(StudentInformationDto studentInformationDto) {
        UserEntity student = (studentInformationDto.getStudentId() == null) ? null :
                UserEntity.builder()
                        .id(studentInformationDto.getStudentId())
                        .build();

        return StudentInformationEntity.builder()
                .id(studentInformationDto.getId())
                .hasHealthIssues(studentInformationDto.getHasHealthIssues())
                .hasDisability(studentInformationDto.getHasDisability())
                .hasLsp(studentInformationDto.getHasLsp())
                .additionalDetails(studentInformationDto.getAdditionalDetails())
                .student(student)
                .build();
    }

}
