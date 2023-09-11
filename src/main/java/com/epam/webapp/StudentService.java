package com.epam.webapp;


import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class StudentService {

    private final DynamoDbTemplate dynamoDbTemplate;

    @Autowired
    public StudentService(DynamoDbTemplate dynamoDbTemplate) {
        this.dynamoDbTemplate = dynamoDbTemplate;
    }

    public Optional<Student> getStudentById(UUID id) {
        return Optional.ofNullable(dynamoDbTemplate.load(Key.builder().partitionValue(String.valueOf(id)).build(), Student.class));
    }

    public List<Student> getAll() {
        List<Student> students = new ArrayList<>();
        PageIterable<Student> studentPageIterable = dynamoDbTemplate.scanAll(Student.class);
        for(Page<Student> studentPage : studentPageIterable) {
            students.addAll(studentPage.items());
        }
        return students;
    }

    public Student saveStudent(Student student) {
        student.setId(UUID.randomUUID());
        return dynamoDbTemplate.save(student);
    }

    public Student updateStudent(UUID id,Student student) {
        Optional<Student> studentOptional = Optional.ofNullable(dynamoDbTemplate.load(Key.builder().partitionValue(String.valueOf(id)).build(), Student.class));

        if (studentOptional.isEmpty()) {
            System.out.println("No record exists");
        } else {
            Student updatedStudent = studentOptional.get();
            updatedStudent.setId(id);
            dynamoDbTemplate.update(updatedStudent);
        }
        return student;
    }

    public void deleteStudent(UUID id) {
        dynamoDbTemplate.delete(Key.builder().partitionValue(String.valueOf(id)).build(),Student.class);
    }
}
