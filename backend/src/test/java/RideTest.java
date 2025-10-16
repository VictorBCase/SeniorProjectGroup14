import com.example.backend.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

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
        VirtualQueue queue = new VirtualQueue();
        Ride ride = new Ride("R4", "Steel Vengeance", queue, 10, 2);
        User user = new User("Julio");
        ride.scanToJoin(user, "https://vqueue.app/ride/R4");

        assertEquals(1, ride.getQueue().getSize());
    }

    @Test
    void testScanToJoinGroup(){
        VirtualQueue queue = new VirtualQueue();
        List<User> groupMembers = Arrays.asList(
                new User("Julio"),
                new User("Jonah"),
                new User("Victor"),
                new User("Daniel")
        );

        Ride ride = new Ride("R4", "Steel Vengeance", queue, 10, 2);
        Group group = new Group("Stroop", groupMembers);
        ride.scanToJoin(group, "https://vqueue.app/ride/R4");

        assertEquals(4, ride.getQueue().getSize());
    }

    @Test
    void testScanToLeave(){
        VirtualQueue queue = new VirtualQueue();
        Ride ride = new Ride("R4", "Steel Vengeance", queue, 10, 2);
        User user = new User("Julio");
        ride.scanToJoin(user, "https://vqueue.app/ride/R4");
        ride.scanToLeave(user, "https://vqueue.app/ride/R4");

        assertEquals(0, ride.getQueue().getSize());
    }

    @Test
    void testScanToLeaveGroup(){
        VirtualQueue queue = new VirtualQueue();
        List<User> groupMembers = Arrays.asList(
                new User("Julio"),
                new User("Jonah"),
                new User("Victor"),
                new User("Daniel")
        );

        Ride ride = new Ride("R4", "Steel Vengeance", queue, 10, 2);
        Group group = new Group("Stroop", groupMembers);
        ride.scanToJoin(group, "https://vqueue.app/ride/R4");
        ride.scanToLeave(group, "https://vqueue.app/ride/R4");

        assertEquals(0, ride.getQueue().getSize());
    }

    @Test
    void testInvalidQRCode(){
        VirtualQueue queue = new VirtualQueue();
        Ride ride = new Ride("R4", "Steel Vengeance", queue, 10, 2);
        User user = new User("Julio");
        ride.scanToJoin(user, "https://vqueue.app/ride/R2");

        assertEquals(0, ride.getQueue().getSize());
    }

    @Test
    void testGetWaitTime(){
        VirtualQueue queue = new VirtualQueue();
        List<User> groupMembers = Arrays.asList(
                new User("Julio"),
                new User("Jonah"),
                new User("Victor"),
                new User("Daniel"),
                new User("Linton"),
                new User("Vivian"),
                new User("Tyler"),
                new User("Paige"),
                new User("Milo"),
                new User("Tom")
        );

        Ride ride = new Ride("R4", "Steel Vengeance", queue, 10, 1);
        Group group = new Group("Stroop", groupMembers);
        ride.scanToJoin(group, "https://vqueue.app/ride/R4");

        assertEquals(60, ride.getWaitTime());
    }

}
