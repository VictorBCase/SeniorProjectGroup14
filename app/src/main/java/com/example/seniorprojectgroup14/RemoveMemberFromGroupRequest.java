package com.example.seniorprojectgroup14;

public class RemoveMemberFromGroupRequest {
    private String groupId;
    private String userId;
    public RemoveMemberFromGroupRequest(String groupId, String userId) {
        this.groupId = groupId;
        this.userId = userId;
    }
}
