package com.example.backend;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Manages all rides used by the API will store rides and can validate QR codes
 */
public class RideManager {

    //stores all rides indexed by rideId
    private Map<String, Ride> rides = new HashMap<>();
    private final DatabaseManager db;

    private final UserManager um;
    /**
     * constuctor that loads rides from the database
     * @param db the database manager
     */
    public RideManager(DatabaseManager db, UserManager um){
        this.db = db;
        this.um = um;
        loadRidesFromDatabase();
    }
    /**
     * adds a new ride to the map
     * @param ride the ride object being stored
     */
    public void addRide(Ride ride){
        rides.put(ride.getRideId(), ride);
    }

    /**
     * finds a ride by its Id
     * @param rideId the rides unique id
     * @return the ride object or null if it is not found
     */
    public Ride getRide(String rideId){
        return rides.get(rideId);
    }

    /**
     * checks if a scanned QR code is the same as the stored QR code
     * @param rideId the unique ride id
     * @param scanned the scanned qr code
     * @return true if the QR matches false otherwise
     */
    public boolean validateQr(String rideId, String scanned){
        Ride r = getRide(rideId);
        if (r == null){
            return false;
        }
        return r.getQrCode().equals(scanned);
    }

    /**
     * will load all rides from database
     */
    private void loadRidesFromDatabase(){
        try(ResultSet rs = db.getAllRide()){
            while (rs.next()){
                String rideId = rs.getString("ride_id");
                String rideName = rs.getString("ride_name");
                int hourlyCapacity = rs.getInt(("hourly_capacity"));
                int loadTime = rs.getInt(("load_time"));
                String qrCode = rs.getString("qr_code");
                String qrImagePath = rs.getString("qr_image_path");

                //create instance of ride and fills out their queue
                Ride ride = new Ride(rideId, rideName, new VirtualQueue(), hourlyCapacity, loadTime, qrCode, qrImagePath);
                rides.put(rideId, ride);
                loadQueuesFromDatabase(rideId);
            }
        } catch(SQLException e){
            throw new RuntimeException("Failed to load rides: " + e.getMessage());
        }
    }

    /**
     * will load all queues from the database and add riders
     * @param rideId the unique ride ID
     */
    private void loadQueuesFromDatabase(String rideId){
        Ride ride = getRide(rideId);
        if (ride == null) return;

        try(ResultSet rs = db.getAllUsersInQueue(rideId)){
            while (rs.next()){
                String userId = rs.getString("entity_id");
                User user = um.getUser(userId);

                if (user == null) {
                    System.err.println("Warning: User " + userId + " not found in UserManager. Skipping.");
                    continue; // skip this entry
                }
                ride.scanToJoin(user, ride.getQrCode());
            }
        } catch(SQLException e){
            throw new RuntimeException("Failed to load rides: " + e.getMessage());
        }
    }
}
