package com.epam.webapp;


import lombok.ToString;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.io.Serializable;
import java.util.UUID;

@DynamoDbBean
@ToString
public class Student {

    private UUID id;
    private String name;
    private String email;
    private String mbl;
    @DynamoDbPartitionKey
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMbl() {
        return mbl;
    }

    public void setMbl(String mbl) {
        this.mbl = mbl;
    }
}
