package com.example.seniorprojectgroup14;

public class CreateGroupRequest {
    private String groupName;
    private String ownerId;
    public CreateGroupRequest(String groupName, String ownerId) {
        this.groupName = groupName;
        this.ownerId = ownerId;
    }
}