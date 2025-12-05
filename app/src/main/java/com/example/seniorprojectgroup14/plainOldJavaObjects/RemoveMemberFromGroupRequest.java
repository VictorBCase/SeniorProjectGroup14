package com.example.seniorprojectgroup14.plainOldJavaObjects;

public class RemoveMemberFromGroupRequest {
    private String groupId;
    private String username;
    public RemoveMemberFromGroupRequest(String groupId, String username) {
        this.groupId = groupId;
        this.username = username;
    }
}
