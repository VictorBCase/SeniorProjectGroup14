package com.example.backend;

public class RunServer {
    public static void main(String[] args) throws Exception {

        DatabaseManager db = new DatabaseManager();
        UserManager um = new UserManager(db);
        RideManager rm = new RideManager(db, um);
        GroupManager gm = new GroupManager(db, um);

        new APIController(db, um, rm, gm, 8080);
        System.out.println("Backend API running at http://localhost:8080/");
    }
}

