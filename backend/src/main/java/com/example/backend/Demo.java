package com.example.backend;

import java.util.Scanner;

public class Demo {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.println("Ride Creation Demo");

        // input ride ID
        System.out.print("Enter Ride ID: ");
        String rideId = scanner.nextLine();

        // input ride name
        System.out.print("Enter Ride Name: ");
        String rideName = scanner.nextLine();

        // input capacity
        System.out.print("Enter hourly capacity: ");
        int capacity = Integer.parseInt(scanner.nextLine());

        // input load time
        System.out.print("Enter load time in minutes: ");
        int loadTime = Integer.parseInt(scanner.nextLine());

        // Create ride object
        Ride ride = new Ride(rideId, rideName, new VirtualQueue(), capacity, loadTime);

        DatabaseManager db = new DatabaseManager();
        db.addRide(ride.getRideId(), ride.getRideName(), ride.getHourlyCapacity(), ride.getLoadTime(), ride.getQrCode(), ride.getQrImagePath());
    }
}
