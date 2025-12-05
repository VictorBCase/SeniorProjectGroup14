package com.example.seniorprojectgroup14.plainOldJavaObjects;

public class AddMemberToGroupRequest {
    private String groupId;
    private String username;

    public AddMemberToGroupRequest(String groupId, String username) {
        this.groupId = groupId;
        this.username = username;
    }
}