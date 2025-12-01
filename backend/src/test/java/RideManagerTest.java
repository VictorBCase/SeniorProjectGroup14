import com.example.backend.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.*;
import java.util.*;
public class RideManagerTest {
    private DatabaseManager db;

    @BeforeEach
    void resetDatabase() throws Exception {
        db = new DatabaseManager(
                "jdbc:postgresql://vqueue-db.c9ukqu4ie5ns.us-east-2.rds.amazonaws.com:5432/vqueue",
                "vqueueadmin",
                "virtualqueue"
        );



        Connection connection = DriverManager.getConnection(
                "jdbc:postgresql://vqueue-db.c9ukqu4ie5ns.us-east-2.rds.amazonaws.com:5432/vqueue",
                "vqueueadmin",
                "virtualqueue"
        );

        Statement s = connection.createStatement();

        // Clean all tables before each test
        s.execute("TRUNCATE queue_entries, group_members, groups, rides, users RESTART IDENTITY CASCADE;");

        s.close();
        connection.close();
    }

    @Test
    void testAddRide() {
        RideManager rm;
        UserManager um;
        um = new UserManager(db);
        rm = new RideManager(db, um);

        Ride r = new Ride("R1", "Maverick", new VirtualQueue(), 20, 2, "qr", "/img");

        rm.addRide(r);

        Ride fetched = rm.getRide("R1");

        assertEquals("Maverick", fetched.getRideName());
    }

    @Test
    void testGetRide() {
        RideManager rm;
        UserManager um;
        um = new UserManager(db);
        rm = new RideManager(db, um);

        Ride r = new Ride("R2", "GateKeeper", new VirtualQueue(), 18, 2, "qr", "/img");

        rm.addRide(r);

        assertEquals("GateKeeper", rm.getRide("R2").getRideName());
    }

    @Test
    void testGetRideNotFound() {
        RideManager rm;
        UserManager um;
        um = new UserManager(db);
        rm = new RideManager(db, um);

        assertNull(rm.getRide("NOPE"));
    }

    @Test
    void testValidateQrMatch() {
        RideManager rm;
        UserManager um;
        um = new UserManager(db);
        rm = new RideManager(db, um);

        Ride r = new Ride("R3", "Valravn", new VirtualQueue(), 22, 2, "qr123", "/img");

        rm.addRide(r);

        assertTrue(rm.validateQr("R3", "qr123"));
    }

    @Test
    void testValidateQrNoMatch() {
        RideManager rm;
        UserManager um;
        um = new UserManager(db);
        rm = new RideManager(db, um);

        Ride r = new Ride("R4", "Rougarou", new VirtualQueue(), 20, 2, "qrABC", "/img");

        rm.addRide(r);

        assertFalse(rm.validateQr("R4", "qrWRONG"));
    }

    @Test
    void testValidateQrRideNotFound() {
        RideManager rm;
        UserManager um;
        um = new UserManager(db);
        rm = new RideManager(db, um);

        assertFalse(rm.validateQr("UNKNOWN", "whatever"));
    }

    @Test
    void testLoadRidesFromDatabase() {
        RideManager rm;
        UserManager um;
        um = new UserManager(db);
        rm = new RideManager(db, um);

        db.addRide("R1", "Maverick", 20, 2, "qr", "/img");

        // Reload via constructor
        rm = new RideManager(db, um);

        Ride r = rm.getRide("R1");
        assertNotNull(r);
        assertEquals("Maverick", r.getRideName());
    }

    @Test
    void testLoadQueueFromDatabase() {
        RideManager rm;
        UserManager um;
        um = new UserManager(db);
        rm = new RideManager(db, um);

        // Add ride to DB
        db.addRide("R10", "Millennium Force", 20, 2, "qr", "/img");

        // Create users
        um.createUser("Julio", "1");
        um.createUser("Alex", "1");

        User u1 = um.login("Julio", "1");
        User u2 = um.login("Alex", "1");

        // Insert queue positions directly into DB
        db.joinQueue("R10", u1.getId(), "user", 1);
        db.joinQueue("R10", u2.getId(), "user", 2);

        // Reload RideManager from DB
        rm = new RideManager(db, um);

        Ride ride = rm.getRide("R10");
        assertNotNull(ride);

        // NEW: Validate queue using getQueueAsNameList()
        List<String> names = ride.getQueue().getQueueAsNameList();

        assertEquals(2, names.size());
        assertEquals("Julio", names.get(0));
        assertEquals("Alex", names.get(1));
    }


}
