package com.example.seniorprojectgroup14.plainOldJavaObjects;

public class CreateGroupRequest {
    private String groupName;
    private String ownerId;
    public CreateGroupRequest(String groupName, String ownerId) {
        this.groupName = groupName;
        this.ownerId = ownerId;
    }
}