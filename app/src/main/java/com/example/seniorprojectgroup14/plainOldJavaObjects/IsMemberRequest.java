package com.example.seniorprojectgroup14.plainOldJavaObjects;

public class IsMemberRequest {

    private String groupName;
    private String userId;
    public IsMemberRequest(String groupName, String userId) {
        this.groupName = groupName;
        this.userId = userId;
    }
}
