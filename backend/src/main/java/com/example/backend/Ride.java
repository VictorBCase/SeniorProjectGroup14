package com.example.backend;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * Represents an amusement park ride that supports a virtual queue system.
 * Each ride has a unique QR code that allows users or groups to join or leave its queue by scanning.
 * This class also manages estimated wait times based on queue size,
 * ride capacity, and ride loading time.
 */
public class Ride {
    private final String rideId;
    private final String rideName;
    private final String qrCode;
    private String qrImagePath;
    private final VirtualQueue queue;
    private double waitTime;//in minutes
    private final int hourlyCapacity;
    private final int loadTime;

    /**
     * Constructs a Ride with the given parameters.
     *
     * @param rideId         Unique identifier for the ride
     * @param rideName       Name of the ride
     * @param queue          VirtualQueue object managing users in line
     * @param hourlyCapacity Number of people the ride can handle per hour
     * @param loadTime       Average load cycle time in minutes
     */
    public Ride(String rideId, String rideName, VirtualQueue queue, int hourlyCapacity, int loadTime) {
        this.rideId = rideId;
        this.rideName = rideName;
        this.qrCode = "https://vqueue.app/ride/" + rideId;
        this.queue = new VirtualQueue();
        this.hourlyCapacity = hourlyCapacity;
        this.loadTime = loadTime;
        generateQRCode();
    }

    /**
     * Reconstructs a Ride from database
     *
     * @param rideId         Unique identifier for the ride
     * @param rideName       Name of the ride
     * @param queue          VirtualQueue object managing users in line
     * @param hourlyCapacity Number of people the ride can handle per hour
     * @param loadTime       Average load cycle time in minutes
     * @param qrCode         the encoded qr code
     * @param qrImagePath    the path to the qr code
     */
    public Ride(String rideId, String rideName, VirtualQueue queue, int hourlyCapacity, int loadTime, String qrCode, String qrImagePath) {
        this.rideId = rideId;
        this.rideName = rideName;
        this.queue = new VirtualQueue();
        this.hourlyCapacity = hourlyCapacity;
        this.loadTime = loadTime;
        this.qrCode = qrCode;
        this.qrImagePath = qrImagePath;
    }


    /**
     * Generates a QR code image for the ride, saved as a PNG file.
     * The QR code encodes the ride's unique join URL.
     */
    public void generateQRCode() {
        this.qrImagePath = "ride_" + rideId + "_qr.png";
        try {
            QRCodeGenerator.createQR(qrCode, qrImagePath, "UTF-8", new HashMap<>(), 300, 300);
        } catch (Exception e) {
            System.out.println("Failed to generate QR for " + rideName + ": " + e.getMessage());
        }
    }

    /**
     * @return the unique ride ID
     */
    public String getRideId() {
        return rideId;
    }

    /**
     * @return the name of the ride
     */
    public String getRideName() {
        return rideName;
    }

    /**
     * @return the URL encoded in the ride's QR code
     */
    public String getQrCode() {
        return qrCode;
    }

    /**
     * @return the file path of the QR code image
     */
    public String getQrImagePath() {
        return qrImagePath;
    }

    /**
     * @return the VirtualQueue for this ride
     */
    public VirtualQueue getQueue() {
        return queue;
    }

    /**
     * @return current estimated wait time in minutes
     */
    public double getWaitTime() {
        return waitTime;
    }

    /**
     * @return the maximum ride capacity per hour
     */
    public int getHourlyCapacity() {
        return hourlyCapacity;
    }

    /**
     * @return the time in minutes it takes for one loading cycle
     */
    public int getLoadTime() {
        return loadTime;
    }

    /**
     * Displays the estimated wait time for this ride.
     */
    public void viewWaitTime() {
        System.out.println("Wait time for " + rideName + ": " + waitTime + " minutes");
    }

    /**
     * Allows a single user to join the ride's queue by scanning its QR code.
     *
     * @param user        the user attempting to join
     * @param scannedCode the scanned QR code string
     */
    public void scanToJoin(User user, String scannedCode) {
        if (scannedCode.equals(qrCode)) {
            queue.joinQueue(user);
            //updateWaitTime();
        } else {
            System.out.println("Invalid QR code. Cannot join " + rideName + ".");
        }

    }

    /**
     * Allows a group of users to join the ride's queue by scanning its QR code.
     *
     * @param group       the group attempting to join
     * @param scannedCode the scanned QR code string
     */
    public void scanToJoin(Group group, String scannedCode) {
        if (scannedCode.equals(qrCode)) {
            queue.joinQueue(group);
            //updateWaitTime();
        } else {
            System.out.println("Invalid QR code. Cannot join " + rideName + ".");
        }

    }

    /**
     * Allows a user to leave the ride's queue by scanning the QR code.
     *
     * @param user        the user attempting to leave
     * @param scannedCode the scanned QR code string
     */
    public void scanToLeave(User user, String scannedCode) {
        if (scannedCode.equals(qrCode)) {
            queue.leaveQueue(user.getId());
            // updateWaitTime();
        } else {
            System.out.println("Invalid QR code. Cannot join " + rideName + ".");
        }
    }

    /**
     * Allows a group to leave the ride's queue by scanning the QR code.
     *
     * @param group       the group attempting to leave
     * @param scannedCode the scanned QR code string
     */
    public void scanToLeave(Group group, String scannedCode) {
        if (scannedCode.equals(qrCode)) {
            queue.leaveQueue(group.getGroupId());
            // updateWaitTime();
        } else {
            System.out.println("Invalid QR code. Cannot join " + rideName + ".");
        }
    }

    /**
     * Updates the ride's estimated wait time based on the queue size,
     * hourly capacity, and load time.
     * Formula used: waitTime = (queueSize / (hourlyCapacity / 60)) * loadTime
     */
    public void updateWaitTime() {
        if (queue.getSize() == 0) {
            waitTime = 0;
        } else {
            waitTime = ((double) queue.getSize() / ((double) hourlyCapacity / 60)) * loadTime;
        }
    }
}



