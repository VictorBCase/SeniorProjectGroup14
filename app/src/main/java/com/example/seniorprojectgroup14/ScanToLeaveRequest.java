package com.example.seniorprojectgroup14;

public class ScanToLeaveRequest {
    private String rideId;
    private String entityId;
    private String qr;

    public ScanToLeaveRequest(String rideId, String entityId, String qr){
        this.rideId = rideId;
        this.entityId = entityId;
        this.qr = qr;
    }
}
