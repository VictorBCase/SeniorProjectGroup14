package com.example.seniorprojectgroup14.plainOldJavaObjects;

public class IsMemberRequest {

    private String groupId;
    private String userId;
    public IsMemberRequest(String groupId, String userId) {
        this.groupId = groupId;
        this.userId = userId;
    }
}
