package com.example.seniorprojectgroup14;

public class ScanToLeaveGroupRequest {
    private String rideId;
    private String groupId;
    private String qr;

    public ScanToLeaveGroupRequest(String rideId, String groupId, String qr) {
        this.rideId = rideId;
        this.groupId = groupId;
        this.qr = qr;
    }
}
