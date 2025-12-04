package com.example.seniorprojectgroup14;

public class IsMemberRequest {

    private String groupId;
    private String userId;
    public IsMemberRequest(String groupid, String userId) {
        this.groupId = groupId;
        this.userId = userId;
    }
}
