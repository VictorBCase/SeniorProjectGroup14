package com.example.seniorprojectgroup14.plainOldJavaObjects;

public class ScanToJoinGroupResponse {
    private Boolean success;
    private int position;

    private String ridename;

    public String getRideName() {
        return ridename;
    }
    public Boolean getSuccess() {
        return success;
    }

    public int getPosition() {
        return position;
    }

    public ScanToJoinGroupResponse() {
    }
}
