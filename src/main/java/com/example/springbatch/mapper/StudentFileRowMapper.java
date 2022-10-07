package com.example.springbatch.mapper;

import com.example.springbatch.dto.StudentDto;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;

public class StudentFileRowMapper implements FieldSetMapper<StudentDto> {

    @Override
    public StudentDto mapFieldSet(FieldSet fieldSet) {
        StudentDto studentDto = new StudentDto();
        studentDto.setStudentId(fieldSet.readString("studentId"));
        studentDto.setFirstName(fieldSet.readString("firstName"));
        studentDto.setLastName(fieldSet.readString("lastName"));
        studentDto.setEmail(fieldSet.readString("email"));
        try {
            studentDto.setAge(fieldSet.readInt("age"));
        } catch (Exception e) {

        }
        return studentDto;
    }

}
