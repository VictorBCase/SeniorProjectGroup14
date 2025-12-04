package com.example.backend;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class UserManager {

    private Map<String, User> usersById = new HashMap<>();
    private Map<String, User> users = new HashMap<>();
    private final DatabaseManager db;

    public UserManager(DatabaseManager db){

        this.db = db;
        loadUsersFromDatabase();
    }
    public boolean createUser(String username, String password) {
        if (users.containsKey(username)) {
            System.out.println("Username already exists.");
            return false;
        }
        String hashedPassword = hashPassword(password);
        User newUser = new User(username, hashedPassword);
        users.put(username, newUser);
        usersById.put(newUser.getId(), newUser);
        System.out.println("User registered: " + username);
        return true;
    }

    //Login
    public User login(String username, String password) {
        User user = users.get(username);
        if (user == null) {
            System.out.println("Error: User not found.");
            return null;
        }

        String hashedInput = hashPassword(password);
        if (!user.getPasswordHash().equals(hashedInput)) {
            System.out.println("Error: Incorrect password.");
            return null;
        }

        System.out.println("Logged in as: " + username);
        return user;
    }

    /**
     * finds a user by its Id
     * @param userId the users unique id
     * @return the user object or null if it is not found
     */
    public User getUser(String userId){return usersById.get(userId);}

    //Password hashing using SHA-256
    //Note: Security was a nonfunctional requirement but I'll leave it with just the hash.
    // Don't really see the point in adding a salt as well just for a student project
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b)); 
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found.", e);
        }
    }

    /**
     * helper method to load users from database
     */
    private void loadUsersFromDatabase(){
        try(ResultSet rs = db.getAllUsers()){
            while (rs.next()){
                String userId = rs.getString("user_id");
                String userName = rs.getString("username");
                String passwordHash = rs.getString(("password_hash"));
                //create user
                User user = new User(userId, userName, passwordHash);
                users.put(userName, user);
                usersById.put(user.getId(), user);
                System.out.println(userId);
            }
        } catch(SQLException e){
            throw new RuntimeException("Failed to load users: " + e.getMessage());
        }
    }
}
