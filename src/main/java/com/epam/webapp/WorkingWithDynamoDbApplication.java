package com.epam.webapp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WorkingWithDynamoDbApplication {

    public static void main(String[] args) {
        SpringApplication.run(WorkingWithDynamoDbApplication.class, args);
    }

    @SqsListener(value = "student-queue")
    public void consumer(String student) throws JsonProcessingException {
        System.out.println("consumer side");
        Student student1 = new ObjectMapper().readValue(student,Student.class);
        System.out.println(student1);
    }

}
