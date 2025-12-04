package com.example.seniorprojectgroup14;

public class AddMemberToGroupRequest {
    private String groupId;
    private String userId;

    public AddMemberToGroupRequest(String groupId, String userId) {
        this.groupId = groupId;
        this.userId = userId;
    }
}