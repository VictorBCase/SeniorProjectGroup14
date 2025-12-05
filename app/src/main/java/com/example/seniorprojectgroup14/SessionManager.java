package com.example.seniorprojectgroup14;
public class SessionManager {

    private static SessionManager instance = null;
    private String userId;
    private String username;

    private SessionManager() {
    }


    public static SessionManager getInstance() {
        if (instance == null) {
            // Lazy initialization
            instance = new SessionManager();
        }
        return instance;
    }

    public void setSession(String userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public void logout() {
        this.userId = null;
        this.username = null;
    }
}