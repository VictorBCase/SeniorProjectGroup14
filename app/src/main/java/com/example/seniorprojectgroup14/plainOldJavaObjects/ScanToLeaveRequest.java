package com.example.seniorprojectgroup14.plainOldJavaObjects;

public class ScanToLeaveRequest {
    private String rideId;
    private String entityId;
    private String scannedCode;

    public ScanToLeaveRequest(String rideId, String entityId, String scannedCode){
        this.rideId = rideId;
        this.entityId = entityId;
        this.scannedCode = scannedCode;
    }
}
