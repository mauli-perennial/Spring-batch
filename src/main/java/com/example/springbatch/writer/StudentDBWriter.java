package com.example.springbatch.writer;

import com.example.springbatch.model.Student;
import com.example.springbatch.repo.StudentRepo;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StudentDBWriter implements ItemWriter<Student> {

    @Autowired
    private StudentRepo employeeRepo;

    @Override
    public void write(List<? extends Student> employees) throws Exception {
        employeeRepo.saveAll(employees);
        System.out.println("inside writer " + employees);
    }
}
