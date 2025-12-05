// No need to run this again, this populated the database with users and rides
package com.example.backend;

import java.util.UUID;

public class PopulateDatabase {
    public static void main(String[] args) {
        populateDatabase();
    }

    public static void populateDatabase() {
        DatabaseManager db = new DatabaseManager();
        UserManager um = new UserManager(db);
        um.createUser("Julio", "12345");
        um.createUser("Daniel", "3618");
        um.createUser("Victor", "1234");
        um.createUser("Jonah", "98765");
        um.createUser("Aine", "4920");
        um.createUser("Chris", "52395");
        um.createUser("Jen", "5894");
        um.createUser("Linton", "1234");

        User julio = um.login("Julio", "12345");
        User daniel = um.login("Daniel", "3618");
        User victor = um.login("Victor", "1234");
        User jonah = um.login("Jonah", "98765");
        User aine = um.login("Aine", "4920");
        User chris = um.login("Chris", "52395");
        User jen = um.login("Jen", "5894");
        User linton = um.login("Linton", "1234");

        Group cwruGroup14 = new Group("CWRU Group 14", aine);
        Group coolChrisGroup = new Group("Cool Chris Group", chris);
        Group joyousJenGroup = new Group("Joyous Jen Group", jen);

        db.addUser(julio.getId(), julio.getUsername(), julio.getPasswordHash());
        db.addUser(daniel.getId(), daniel.getUsername(), daniel.getPasswordHash());
        db.addUser(victor.getId(), victor.getUsername(), victor.getPasswordHash());
        db.addUser(jonah.getId(), jonah.getUsername(), jonah.getPasswordHash());
        db.addUser(aine.getId(), aine.getUsername(), aine.getPasswordHash());
        db.addUser(chris.getId(), chris.getUsername(), chris.getPasswordHash());
        db.addUser(jen.getId(), jen.getUsername(), jen.getPasswordHash());
        db.addUser(linton.getId(), linton.getUsername(), linton.getPasswordHash());

        db.addGroup(cwruGroup14.getGroupId(), cwruGroup14.getGroupName(), cwruGroup14.getOwner().getId());
        db.addGroup(coolChrisGroup.getGroupId(), coolChrisGroup.getGroupName(), coolChrisGroup.getOwner().getId());
        db.addGroup(joyousJenGroup.getGroupId(), joyousJenGroup.getGroupName(), joyousJenGroup.getOwner().getId());

        db.addMemberToGroup(cwruGroup14.getGroupId(), daniel.getId());
        db.addMemberToGroup(cwruGroup14.getGroupId(), julio.getId());
        db.addMemberToGroup(coolChrisGroup.getGroupId(), jonah.getId());
        db.addMemberToGroup(coolChrisGroup.getGroupId(), victor.getId());

        Ride maverick = new Ride("R1", "Maverick", new VirtualQueue(), 12, 1);
        Ride magnumXL200 = new Ride("R2", "Magnum XL200", new VirtualQueue(), 20, 2);
        Ride steelVengance = new Ride("R3", "Steel Vengance", new VirtualQueue(), 12, 3);
        Ride topThrillTwo = new Ride("R4", "Top Thrill Two", new VirtualQueue(), 10, 1);
        Ride millenniumForce = new Ride("R5", "Millennium Force", new VirtualQueue(), 13, 2);
        maverick.generateQRCode();
        magnumXL200.generateQRCode();
        steelVengance.generateQRCode();
        topThrillTwo.generateQRCode();
        millenniumForce.generateQRCode();

        db.addRide(maverick.getRideId(), maverick.getRideName(), maverick.getHourlyCapacity(), maverick.getLoadTime(), maverick.getQrCode(), maverick.getQrImagePath());
        db.addRide(magnumXL200.getRideId(), magnumXL200.getRideName(), magnumXL200.getHourlyCapacity(), magnumXL200.getLoadTime(), magnumXL200.getQrCode(), magnumXL200.getQrImagePath());
        db.addRide(steelVengance.getRideId(), steelVengance.getRideName(), steelVengance.getHourlyCapacity(), steelVengance.getLoadTime(), steelVengance.getQrCode(), steelVengance.getQrImagePath());
        db.addRide(topThrillTwo.getRideId(), topThrillTwo.getRideName(), topThrillTwo.getHourlyCapacity(), topThrillTwo.getLoadTime(), topThrillTwo.getQrCode(), topThrillTwo.getQrImagePath());
        db.addRide(millenniumForce.getRideId(), millenniumForce.getRideName(), millenniumForce.getHourlyCapacity(), millenniumForce.getLoadTime(), millenniumForce.getQrCode(), millenniumForce.getQrImagePath());

        System.out.println(db.validateLogin("Julio", "12345"));
    }
}
