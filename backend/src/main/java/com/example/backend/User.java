package com.example.backend;

import java.util.UUID;

public class User {
    private String id;
    private String name;

    //com.example.backend.User Constructor
    //Note, adding ID to constructor for unique identifcation
    public User(String name) {
        this.id = UUID.randomUUID().toString(); //UUID converted to string; can change if we don't want UUID
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}