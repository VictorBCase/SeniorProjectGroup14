package com.example.seniorprojectgroup14;

public class ScanToJoinRequest {

    private String rideId;
    private String entityId;
    private String qr;

    public ScanToJoinRequest(String rideId, String entityId, String qr){
        this.rideId = rideId;
        this.entityId = entityId;
        this.qr = qr;
    }
}
