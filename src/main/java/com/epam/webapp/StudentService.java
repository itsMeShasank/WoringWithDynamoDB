package com.epam.webapp;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import io.awspring.cloud.sqs.operations.SqsTemplate;
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
    private final SqsTemplate sqsTemplate;

    @Autowired
    public StudentService(DynamoDbTemplate dynamoDbTemplate, SqsTemplate sqsTemplate) {
        this.dynamoDbTemplate = dynamoDbTemplate;
        this.sqsTemplate = sqsTemplate;
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
        sendToQueue(student);
        return dynamoDbTemplate.save(student);
    }

    private void sendToQueue(Student student) {
        sqsTemplate.send(to -> {
            try {
                to.queue("student-queue").payload(new ObjectMapper().writeValueAsString(student));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
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
