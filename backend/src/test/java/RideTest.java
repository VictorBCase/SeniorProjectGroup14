import com.example.backend.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class RideTest {

    @Test
    void testRideIntialization(){
        VirtualQueue queue = new VirtualQueue();
        Ride ride = new Ride("R1", "Maverick", queue, 60);

        assertEquals("R1", ride.getRideId());
        assertEquals("Maverick", ride.getRideName());
        assertEquals("https://vqueue.app/ride/R1", ride.getQrCode());
        assertEquals(60, ride.getWaitTime());
        assertNotNull(ride.getQueue());
    }

    @Test
    void testQRCodeFormat(){
        VirtualQueue queue = new VirtualQueue();
        Ride ride = new Ride("R2", "Iron Dragon", queue, 20);

        assertTrue(ride.getQrCode().contains("https://vqueue.app/ride/R2"));
    }

    @Test
    void QRCodeDoesNotCrash(){
        VirtualQueue queue = new VirtualQueue();
        Ride ride = new Ride("R3", "Gemini", queue, 30);

        assertDoesNotThrow(ride::generateQRCode);
    }

    @Test
    void testScanToJoin(){
        VirtualQueue queue = new VirtualQueue();
        Ride ride = new Ride("R4", "Steel Vengeance", queue, 60);
        User user = new User("Julio");
        ride.scanToJoin(user, "https://vqueue.app/ride/R4");

        assertEquals(1, ride.getQueue().size());
    }

    @Test
    void testScanToLeave(){
        VirtualQueue queue = new VirtualQueue();
        Ride ride = new Ride("R4", "Steel Vengeance", queue, 60);
        User user = new User("Julio");
        ride.scanToJoin(user, "https://vqueue.app/ride/R4");
        ride.scanToLeave(user, "https://vqueue.app/ride/R4");

        assertEquals(0, ride.getQueue().size());
    }

    @Test
    void testInvalidQRCode(){
        VirtualQueue queue = new VirtualQueue();
        Ride ride = new Ride("R4", "Steel Vengeance", queue, 60);
        User user = new User("Julio");
        ride.scanToJoin(user, "https://vqueue.app/ride/R2");

        assertEquals(0, ride.getQueue().size());
    }

}
