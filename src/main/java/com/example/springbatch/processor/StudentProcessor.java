package com.example.springbatch.processor;

import com.example.springbatch.dto.StudentDto;
import com.example.springbatch.model.Student;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class StudentProcessor implements ItemProcessor<StudentDto, Student> {

    @Override
    public Student process(StudentDto studentDto) throws Exception {
        Student student = new Student();
        student.setStudentId(studentDto.getStudentId()+new Random().nextInt(10000000));
        student.setFirstName(studentDto.getFirstName());
        student.setLastName(studentDto.getLastName());
        student.setEmail(studentDto.getEmail());
        student.setAge(studentDto.getAge());
        System.out.println("inside processor " + student.toString());
        return student;
    }
}
