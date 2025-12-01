package com.example.backend;

import java.sql.*;
import java.util.UUID;

/**
 * Class handles interactions for V-queue SQL database
 * will handle connection, managing queries, and operations
 */
public class DatabaseManager {

    // Database connection
    private static final String url = "jdbc:postgresql://localhost:5432/vqueue";
    private static final String user = "postgres";
    private static final String password = "vqueue";

    private Connection connection;
    /**
     * Connect to PostgreSQL database on creation
     */
    DatabaseManager(){
        connect();
    }

    /**
     * Attempts to connect to the vqueue database
     */
    private void connect(){
        try{
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to database");
        } catch (SQLException e) {
            System.err.println("Connection failed " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void closeConnction(){
        try{
            if (connection != null && !connection.isClosed()){
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
    /**
     * Inserts a new user into the data base
     * @param username     the username to register
     * @param passwordHash the hashed password
     */
    public void addUser(String username, String passwordHash){
        String sql = "INSERT INTO users (username, password_hash) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setString(1, username);
            stmt.setString(2, passwordHash);
            stmt.executeUpdate();
            System.out.println("User added: " + username);
        } catch (SQLException e) {
            System.err.println("Error adding User " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * remove a user from the data base
     * @param username     the username to delete
     * @param passwordHash the hashed password
     */
    public void removeUser(String username, String passwordHash){
        String sql = "DELETE FROM users WHERE username = ? AND password_hash = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)){
            stmt.setString(1, username);
            stmt.setString(2, passwordHash);
            int rows = stmt.executeUpdate();
            if (rows > 0){
                System.out.println("User " + username + " removed");
            }
            else {
                System.out.println("No matching user found in group.");
            }
        } catch (SQLException e) {
            System.err.println("removed " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Checks if user exist by matching username and password
     * @param username     username to check
     * @param passwordHash password to verify
     * @return true if user's username and password match false otherwise
     */
    public boolean validateLogin(String username, String passwordHash){
        String sql = "SELECT * FROM users WHERE username = ? and password_hash = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, passwordHash);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.err.println("Login check failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Adds a new group to the group table
     * @param groupId   the unique id of the group
     * @param groupName the name of the group
     * @param ownerId   the owner id
     */
    public void addGroup(String groupId, String groupName, String ownerId){
        String sql = "INSERT INTO groups (group_id ,group_name, owner_id) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, groupId);
            stmt.setString(2, groupName);
            stmt.setObject(3, ownerId);
            stmt.executeUpdate();
            System.out.println("Group created: " + groupName);
        } catch (SQLException e) {
            System.err.println("Error creating group: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Adds a new user to a group in the database
     * @param groupId unique id of the group
     * @param userId unique id of the user
     */
    public void addMemberToGroup(String groupId, String userId){
        String sql = "INSERT INTO group_members (group_id, user_id) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, groupId);
            stmt.setObject(2, userId);
            stmt.executeUpdate();
            System.out.println("User " + userId + " added to group " + groupId);
        } catch (SQLException e) {
            System.err.println("Error adding member to group: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Removes a user from a group in the database
     * @param groupId
     * @param userId
     */
    public void removeMemberFromGrouo(String groupId, String userId){
        String sql = "DELETE FROM group_members WHERE group_id = ? AND user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, groupId);
            stmt.setObject(2, userId);
            int rows = stmt.executeUpdate();
            if (rows > 0){
                System.out.println("User " + userId + " removed from group " + groupId);
            }
            else {
                System.out.println("No matching user found in group.");
            }
        } catch (SQLException e) {
            System.err.println("Error removing member from group: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Checks if a user is part of the group
     * @param groupId unique group id
     * @param userId unique user id
     * @return true if user is found false otherwise
     */
    public boolean isUserInGroup(String groupId, String userId) {
        String sql = "SELECT * FROM group_members WHERE group_id = ? AND user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, groupId);
            stmt.setObject(2, userId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.err.println("Error checking member in group: " + e.getMessage());
            return false;
        }
    }

//    /**
//     * Prints out all the group members in the database
//     * @param groupId the unique groupId
//     */
//    public void viewGroupMembers(String groupId){
//        String sql = """
//                SELECT u.username, u.user_id
//                FROM users u
//                JOIN group_members gm ON gm.user_id = u.user_id
//                WHERE gm.group_id = ?
//                """;
//        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
//            stmt.setObject(1, groupId);
//            ResultSet rs = stmt.executeQuery();
//            System.out.println("Members of group " + groupId + ":");
//            while (rs.next()){
//                System.out.println("- " + rs.getString("username") + " (" + rs.getString("user_id") + ")");
//            }
//        } catch (SQLException e) {
//            System.err.println("Error listing group members: " + e.getMessage());
//            throw new RuntimeException(e);
//        }
//    }

    /**
     * Add a new ride into the database
     * @param rideId         the rides unique number
     * @param rideName       name of the ride
     * @param hourlyCapacity number of riders an hour
     * @param loadTime      rides load time in minutes
     * @param qrCode        qrCode itself
     * @param qrImagePath   path to QR code image
     */
    public void addRide(String rideId, String rideName, int hourlyCapacity, int loadTime,  String qrCode, String qrImagePath){
        String sql = "INSERT INTO rides (ride_id, ride_name, hourly_capacity, load_time, qr_code, qr_image_path) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, rideId);
            stmt.setString(2, rideName);
            stmt.setInt(3, hourlyCapacity);
            stmt.setInt(4, loadTime);
            stmt.setString(5, qrCode);
            stmt.setString(6, qrImagePath);
            stmt.executeUpdate();
            System.out.println("Ride added: " + rideName);
        } catch (SQLException e) {
            System.err.println("Login check failed: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Adds a user or group into the ride queue
     * @param rideId     unique id of the ride
     * @param entityId   unique id of group or user
     * @param entityType if it is a group or user
     * @param position   spot in queue
     */
    public void joinQueue(String rideId, String entityId, String entityType, int position){
        String sql = "INSERT INTO queue_entries (ride_id, entity_id, entity_type, position) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, rideId);
            stmt.setString(2, entityId);
            stmt.setString(3, entityType);
            stmt.setInt(4, position);
            stmt.executeUpdate();
            System.out.println("Entity joined queue for ride: " + rideId);
        } catch (SQLException e) {
            System.err.println("Error joining queue: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Prints out who is currently in queue in the database
     * @param rideId the unique ride id
     */
    public void viewQueue(String rideId){
        String sql = "SELECT entity_id, entity_type, position, joined_at FROM queue_entries WHERE ride_id = ? ORDER BY position ASC";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, rideId);
            ResultSet rs = stmt.executeQuery();
            System.out.println("Queue for ride " + rideId + ":");
            while (rs.next()) {
                String entityType = rs.getString("entity_type");
                String entityId = rs.getString("entity_id");
                int position = rs.getInt("position");
                System.out.println("Position " + position + " → " + entityType + " (" + entityId + ")");
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving queue: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }


    //Temporary main for quick testing
    public static void main(String[] args){
        DatabaseManager db = new DatabaseManager();
        UserManager manager = new UserManager();
        manager.createUser("Julio", "password123");
        User u = manager.login("Julio", "password123");
        if (u != null){
            db.removeUser(u.getUsername(), u.getPasswordHash());
            db.addUser(u.getUsername(), u.getPasswordHash());
        }
        System.out.println(db.validateLogin(u.getUsername(), u.getPasswordHash()));
        db.closeConnction();
    }
}