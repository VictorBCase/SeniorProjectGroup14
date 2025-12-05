package com.example.seniorprojectgroup14.plainOldJavaObjects;

public class ScanToLeaveGroupRequest {
    private String rideId;
    private String groupId;
    private String scannedCode;

    public ScanToLeaveGroupRequest(String rideId, String groupId, String scannedCode) {
        this.rideId = rideId;
        this.groupId = groupId;
        this.scannedCode = scannedCode;
    }
}
