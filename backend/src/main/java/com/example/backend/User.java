package com.example.backend;

import java.util.UUID;

public class User {
    private String id;
    private String username;
    private String passwordHash;

    //com.example.backend.User Constructor
    //Note, adding ID to constructor for unique identifcation
    public User(String name, String passwordHash) {
        this.id = UUID.randomUUID().toString(); //UUID converted to string; can change if we don't want UUID
        this.username = name;
        this.passwordHash = passwordHash;
    }

    public String getId() { return id; }

    public String getUsername() {return username;}

    public String getPasswordHash() {return passwordHash;}


}