package com.example.backend;

import java.util.HashMap;

public class Ride {
    private final String rideId;
    private final String rideName;
    private final String qrCode;

    private String qrImagePath;
    private final VirtualQueue queue;
    private int waitTime;//in minutes


    public Ride(String rideId, String rideName, VirtualQueue queue, int waitTime) {
        this.rideId = rideId;
        this.rideName = rideName;
        this.qrCode = "https://vqueue.app/ride/" + rideId;
        this.queue = new VirtualQueue();
        this.waitTime = waitTime;
    }

    public void generateQRCode(){
        this.qrImagePath = "ride_" + rideId + "_qr.png";
        try {
            QRCodeGenerator.createQR(qrCode, qrImagePath, "UTF-8", new HashMap<>(), 300, 300);
        } catch (Exception e) {
            System.out.println("Failed to generate QR for " + rideName + ": " + e.getMessage());
        }
    }

    public String getRideId() {
        return rideId;
    }

    public String getRideName() {
        return rideName;
    }

    public String getQrCode() {
        return qrCode;
    }

    public String getQrImagePath() {
        return qrImagePath;
    }

    public VirtualQueue getQueue() {
        return queue;
    }

    public int getWaitTime() {
        return waitTime;
    }

    public void viewWaitTime(){
        System.out.println("Wait time for " + rideName + ": " + waitTime + " minutes");
    }
    public void scanToJoin(User user, String scannedCode){
        if (scannedCode.equals(qrCode)){
            queue.joinQueue(user);
        }
        else {
            System.out.println("Invalid QR code. Cannot join " + rideName + ".");
        }

    }

    public void scanToLeave(User user, String scannedCode){
        if (scannedCode.equals(qrCode)){
            queue.leaveQueue(user);
        }
        else {
            System.out.println("Invalid QR code. Cannot join " + rideName + ".");
        }
    }

    //todo function to update wait time

    public void updateWaitTIme(){

    }
}



