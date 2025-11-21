import com.example.backend.*;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

public class APIControllerTest {

    private int port;
    private DatabaseManager db;
    private UserManager userManager;
    private RideManager rideManager;
    private APIController api;

    @BeforeEach
    void setup() throws Exception {
        // Pick a random free port
        try (ServerSocket socket = new ServerSocket(0)) {
            port = socket.getLocalPort();
        }

        db = new DatabaseManager(
                "jdbc:postgresql://localhost:5432/vqueue_test",
                "postgres",
                "vqueue"
        );

        // Clear tables
        try (Connection connection = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/vqueue_test",
                "postgres",
                "vqueue"
        );
             Statement stmt = connection.createStatement()) {
            stmt.execute("TRUNCATE queue_entries, group_members, groups, rides, users RESTART IDENTITY CASCADE;");
        }

        userManager = new UserManager(db);
        rideManager = new RideManager(db, userManager);
        api = new APIController(db, userManager, rideManager, port);
    }

    @AfterEach
    void teardown() {
        // Stop the server to free the port
        api.stop();
    }

    @Test
    void testRegisterAndLogin() throws IOException {
        JSONObject registerBody = new JSONObject();
        registerBody.put("username", "Julio");
        registerBody.put("password", "123");

        JSONObject registerResponse = sendPost("/register", registerBody);
        assertTrue(registerResponse.getBoolean("success"));
        assertNotNull(registerResponse.getString("userId"));

        JSONObject loginBody = new JSONObject();
        loginBody.put("username", "Julio");
        loginBody.put("password", "123");

        JSONObject loginResponse = sendPost("/login", loginBody);
        assertTrue(loginResponse.getBoolean("success"));
        assertNotNull(loginResponse.getString("userId"));
    }

    @Test
    void testScanToJoinAndViewQueue() throws IOException {
        Ride ride = new Ride("R1", "Millennium Force", new VirtualQueue(), 20, 2);
        rideManager.addRide(ride);
        db.addRide("R1", "Millennium Force", 20, 2, ride.getQrCode() , ride.getQrImagePath());

        JSONObject regBody = new JSONObject();
        regBody.put("username", "Alex");
        regBody.put("password", "123");
        sendPost("/register", regBody);

        JSONObject loginBody = new JSONObject();
        loginBody.put("username", "Alex");
        loginBody.put("password", "123");
        JSONObject loginResponse = sendPost("/login", loginBody);
        String userId = loginResponse.getString("userId");

        JSONObject joinBody = new JSONObject();
        joinBody.put("rideId", "R1");
        joinBody.put("entityId", userId);
        joinBody.put("scannedCode", ride.getQrCode());

        JSONObject joinResponse = sendPost("/scanToJoin", joinBody);
        assertTrue(joinResponse.getBoolean("success"));
        assertEquals(1, joinResponse.getInt("position"));

        JSONObject queueBody = new JSONObject();
        queueBody.put("rideId", "R1");
        JSONObject queueResponse = sendPost("/viewQueue", queueBody);
        assertEquals(1, queueResponse.getJSONArray("queue").length());
        assertEquals("Alex", queueResponse.getJSONArray("queue").getString(0));
    }

    private JSONObject sendPost(String path, JSONObject body) throws IOException {
        URL url = new URL("http://localhost:" + port + path);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            os.write(body.toString().getBytes(StandardCharsets.UTF_8));
        }

        InputStream responseStream = connection.getResponseCode() < 400 ?
                connection.getInputStream() : connection.getErrorStream();

        String Response = new String(responseStream.readAllBytes(), StandardCharsets.UTF_8);
        return new JSONObject(Response);
    }

    @Test
    void testScanToJoinGroupAndLeaveGroup() throws IOException {
        // Create users
        JSONObject reg1 = new JSONObject();
        reg1.put("username", "GroupUser1");
        reg1.put("password", "pw1");
        JSONObject userResponse1 = sendPost("/register", reg1);
        String userId1 = userResponse1.getString("userId");

        JSONObject reg2 = new JSONObject();
        reg2.put("username", "GroupUser2");
        reg2.put("password", "pw2");
        JSONObject userResponse2 = sendPost("/register", reg2);
        String userId2 = userResponse2.getString("userId");

        // Create group
        JSONObject groupBody = new JSONObject();
        groupBody.put("groupName", "TestGroup");
        groupBody.put("ownerId", userId1);
        JSONObject createResponse = sendPost("/createGroup", groupBody);
        String groupId = createResponse.getString("groupId");

        // Add member to group
        JSONObject addMember = new JSONObject();
        addMember.put("groupId", groupId);
        addMember.put("userId", userId2);
        JSONObject addResponse = sendPost("/addMemberToGroup", addMember);
        assertTrue(addResponse.getBoolean("success"));

        // Check membership
        JSONObject checkMember = new JSONObject();
        checkMember.put("groupId", groupId);
        checkMember.put("userId", userId2);
        JSONObject checkResponse = sendPost("/isMember", checkMember);
        assertTrue(checkResponse.getBoolean("isMember"));

        // Remove member
        JSONObject removeMember = new JSONObject();
        removeMember.put("groupId", groupId);
        removeMember.put("userId", userId2);
        JSONObject removeResponse = sendPost("/removeMemberFromGroup", removeMember);
        assertTrue(removeResponse.getBoolean("success"));

        checkResponse = sendPost("/isMember", checkMember);
        assertFalse(checkResponse.getBoolean("isMember"));
    }

//    @Test
//    void testMultipleUsersQueueAndPositions() throws IOException {
//        Ride ride = new Ride("R6", "Lightning Rod", new VirtualQueue(), 10, 2);
//        rideManager.addRide(ride);
//        db.addRide("R6", "Lightning Rod", 10, 2, ride.getQrCode(), ride.getQrImagePath());
//
//        String[] usernames = {"UserA", "UserB", "UserC"};
//        String[] userIds = new String[3];
//
//        for (int i = 0; i < usernames.length; i++) {
//
//            JSONObject reg = new JSONObject();
//            reg.put("username", usernames[i]);
//            reg.put("password", "pw");
//            JSONObject regResponse = sendPost("/register", reg);
//            userIds[i] = regResponse.getString("userId");
//            System.out.println("Registered: " + usernames[i]);
//
//            // Login user
//            JSONObject login = new JSONObject();
//            login.put("username", usernames[i]);
//            login.put("password", "pw");
//            sendPost("/login", login);
//            System.out.println("Logged in: " + usernames[i]);
//
//            // Join queue
//            JSONObject join = new JSONObject();
//            join.put("rideId", "R6");
//            join.put("entityId", userIds[i]);
//            join.put("scannedCode", ride.getQrCode());
//            JSONObject joinResponse = sendPost("/scanToJoin", join);
//            assertTrue(joinResponse.getBoolean("success"), "Join failed for " + usernames[i]);
//            System.out.println(usernames[i] + " joined at position " + joinResponse.getInt("position"));
//
//
//            JSONObject queueBody = new JSONObject();
//            queueBody.put("rideId", "R6");
//            JSONObject queueResponse = sendPost("/viewQueue", queueBody);
//            System.out.println("Current queue: " + queueResponse.getJSONArray("queue").toString());
//        }
//
//
//        JSONObject finalQueueBody = new JSONObject();
//        finalQueueBody.put("rideId", "R6");
//        JSONObject finalQueueResponse = sendPost("/viewQueue", finalQueueBody);
//        assertEquals(3, finalQueueResponse.getJSONArray("queue").length(), "Queue length mismatch");
//        for (int i = 0; i < usernames.length; i++) {
//            assertEquals(usernames[i], finalQueueResponse.getJSONArray("queue").getString(i),
//                    "Queue order mismatch at position " + i);
//        }
//
//
//        for (int i = 0; i < userIds.length; i++) {
//            JSONObject posBody = new JSONObject();
//            posBody.put("rideId", "R6");
//            posBody.put("entityId", userIds[i]);
//            JSONObject posResponse = sendPost("/userPosition", posBody);
//            assertEquals(i + 1, posResponse.getInt("position"),
//                    "Position mismatch for " + usernames[i]);
//        }
//    }**/

}

