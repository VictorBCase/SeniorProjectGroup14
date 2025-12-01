import com.example.backend.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

public class RideTest {

    @Test
    void testRideIntialization(){
        VirtualQueue queue = new VirtualQueue();
        Ride ride = new Ride("R1", "Maverick", queue, 7, 2);

        assertEquals("R1", ride.getRideId());
        assertEquals("Maverick", ride.getRideName());
        assertEquals("https://vqueue.app/ride/R1", ride.getQrCode());
        assertEquals(7, ride.getHourlyCapacity());
        assertEquals(2, ride.getLoadTime());
        assertNotNull(ride.getQueue());
    }

    @Test
    void testQRCodeFormat(){
        VirtualQueue queue = new VirtualQueue();
        Ride ride = new Ride("R2", "Iron Dragon", queue, 5, 2);

        assertTrue(ride.getQrCode().contains("https://vqueue.app/ride/R2"));
    }

    @Test
    void QRCodeDoesNotCrash(){
        VirtualQueue queue = new VirtualQueue();
        Ride ride = new Ride("R3", "Gemini", queue, 20, 2);

        assertDoesNotThrow(ride::generateQRCode);
    }

    @Test
    void testScanToJoin(){
        DatabaseManager db = new DatabaseManager();
        VirtualQueue queue = new VirtualQueue();
        UserManager manager = new UserManager(db);
        Ride ride = new Ride("R4", "Steel Vengeance", queue, 10, 2);
        manager.createUser("Julio", "1");
        User user = manager.login("Julio", "1");
        ride.scanToJoin(user, "https://vqueue.app/ride/R4");

        assertEquals(1, ride.getQueue().getSize());
    }

    @Test
    void testScanToJoinGroup(){
        DatabaseManager db = new DatabaseManager();
        VirtualQueue queue = new VirtualQueue();
        UserManager manager = new UserManager(db);
        manager.createUser("Julio", "1");
        User user = manager.login("Julio", "1");
        manager.createUser("Jonah", "1");
        User user2 = manager.login("Jonah", "1");
        manager.createUser("Victor", "1");
        User user3 = manager.login("Victor", "1");
        manager.createUser("Daniel", "1");
        User user4 = manager.login("Daniel", "1");
        List<User> groupMembers = Arrays.asList(user, user2, user3, user4);

        Ride ride = new Ride("R4", "Steel Vengeance", queue, 10, 2);
        Group group = new Group("Stroop", groupMembers);
        ride.scanToJoin(group, "https://vqueue.app/ride/R4");

        assertEquals(4, ride.getQueue().getSize());
    }

    @Test
    void testScanToLeave(){
        DatabaseManager db = new DatabaseManager();
        VirtualQueue queue = new VirtualQueue();
        Ride ride = new Ride("R4", "Steel Vengeance", queue, 10, 2);
        UserManager manager = new UserManager(db);
        manager.createUser("Julio", "1");
        User user = manager.login("Julio", "1");
        ride.scanToJoin(user, "https://vqueue.app/ride/R4");
        ride.scanToLeave(user, "https://vqueue.app/ride/R4");

        assertEquals(0, ride.getQueue().getSize());
    }

    @Test
    void testScanToLeaveGroup(){
        DatabaseManager db = new DatabaseManager();
        VirtualQueue queue = new VirtualQueue();
        UserManager manager = new UserManager(db);
        manager.createUser("Julio", "1");
        User user = manager.login("Julio", "1");
        manager.createUser("Jonah", "1");
        User user2 = manager.login("Jonah", "1");
        manager.createUser("Victor", "1");
        User user3 = manager.login("Victor", "1");
        manager.createUser("Daniel", "1");
        User user4 = manager.login("Daniel", "1");
        List<User> groupMembers = Arrays.asList(user, user2, user3, user4);

        Ride ride = new Ride("R4", "Steel Vengeance", queue, 10, 2);
        Group group = new Group("Stroop", groupMembers);
        ride.scanToJoin(group, "https://vqueue.app/ride/R4");
        ride.scanToLeave(group, "https://vqueue.app/ride/R4");

        assertEquals(0, ride.getQueue().getSize());
    }

    @Test
    void testInvalidQRCode(){
        DatabaseManager db = new DatabaseManager();
        VirtualQueue queue = new VirtualQueue();
        Ride ride = new Ride("R4", "Steel Vengeance", queue, 10, 2);
        UserManager manager = new UserManager(db);
        manager.createUser("Julio", "1");
        User user = manager.login("Julio", "1");
        ride.scanToJoin(user, "https://vqueue.app/ride/R2");

        assertEquals(0, ride.getQueue().getSize());
    }

    @Test
    void testGetWaitTime(){
        DatabaseManager db = new DatabaseManager();
        VirtualQueue queue = new VirtualQueue();
        UserManager manager = new UserManager(db);
        manager.createUser("Julio", "1");
        User user = manager.login("Julio", "1");
        manager.createUser("Jonah", "1");
        User user2 = manager.login("Jonah", "1");
        manager.createUser("Victor", "1");
        User user3 = manager.login("Victor", "1");
        manager.createUser("Daniel", "1");
        User user4 = manager.login("Daniel", "1");
        manager.createUser("Linton", "1");
        User user5 = manager.login("Linton", "1");
        manager.createUser("Vivian", "1");
        User user6 = manager.login("Vivian", "1");
        manager.createUser("Paige", "1");
        User user7 = manager.login("Paige", "1");
        manager.createUser("Milo", "1");
        User user8 = manager.login("Milo", "1");
        manager.createUser("Tom", "1");
        User user9 = manager.login("Tom", "1");
        manager.createUser("Tyler", "1");
        User user10 = manager.login("Tyler", "1");
        List<User> groupMembers = Arrays.asList(user, user2, user3, user4, user5, user6, user7, user8, user9, user10);

        Ride ride = new Ride("R4", "Steel Vengeance", queue, 10, 1);
        Group group = new Group("Stroop", groupMembers);
        ride.scanToJoin(group, "https://vqueue.app/ride/R4");

        assertEquals(60, ride.getWaitTime());
    }

}
