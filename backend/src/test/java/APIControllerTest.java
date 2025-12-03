import com.example.backend.*;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class APIControllerTest {

    private int port;
    private DatabaseManager db;
    private UserManager um;
    private RideManager rm;
    private APIController api;
    private GroupManager gm;
    @BeforeEach
    void setup() throws Exception {
        // Pick a random free port
        try (ServerSocket socket = new ServerSocket(0)) {
            port = socket.getLocalPort();
        }

        db = new DatabaseManager();

        // Clear tables
        try (Connection connection = DriverManager.getConnection(
                "jdbc:postgresql://vqueue-db.c9ukqu4ie5ns.us-east-2.rds.amazonaws.com:5432/vqueue",
                "vqueueadmin",
                "virtualqueue"
        );
             Statement stmt = connection.createStatement()) {
            System.out.println("DB = " + connection.getMetaData().getURL());
            stmt.execute("TRUNCATE queue_entries, group_members, groups, rides, users RESTART IDENTITY CASCADE;");
        }

        um = new UserManager(db);
        rm = new RideManager(db, um);
        gm = new GroupManager(db, um);
        api = new APIController(db, um, rm, gm, port);
    }

    @AfterEach
    void teardown() {
        // Stop the server to free the port
        api.stop();
    }

    @Test
    void testRegisterAndLogin() throws IOException {

        // build register body
        JSONObject registerBody = new JSONObject();
        registerBody.put("username", "Julio");
        registerBody.put("password", "123");

        // send register request
        JSONObject registerResponse = sendPost("/register", registerBody);
        assertTrue(registerResponse.getBoolean("success"));
        assertNotNull(registerResponse.getString("userId"));

        // build login body
        JSONObject loginBody = new JSONObject();
        loginBody.put("username", "Julio");
        loginBody.put("password", "123");

        // send login request
        JSONObject loginResponse = sendPost("/login", loginBody);
        assertTrue(loginResponse.getBoolean("success"));
        assertNotNull(loginResponse.getString("userId"));
    }

    @Test
    void testDuplicateRegisterFails() throws IOException {

        // build register body
        JSONObject body = new JSONObject();
        body.put("username", "Julio");
        body.put("password", "123");

        // send register request
        sendPost("/register", body);

        // send duplicate register request
        JSONObject duplicate = sendPost("/register", body);
        assertTrue(duplicate.has("error"));
    }

    @Test
    void testLoginInvalidCredentials() throws IOException {

        // build login body with no user created
        JSONObject loginBody = new JSONObject();
        loginBody.put("username", "GhostUser");
        loginBody.put("password", "wrong");

        // send login request
        JSONObject response = sendPost("/login", loginBody);
        assertFalse(response.getBoolean("success"));
    }

    @Test
    void testScanToJoinAndViewQueue() throws IOException {

        // create ride
        Ride ride = new Ride("R1", "GateKeeper", new VirtualQueue(), 20, 2);
        rm.addRide(ride);
        db.addRide("R1", "GateKeeper", 20, 2, ride.getQrCode(), ride.getQrImagePath());

        // build register body
        JSONObject regBody = new JSONObject();
        regBody.put("username", "Alex");
        regBody.put("password", "123");
        sendPost("/register", regBody);

        // build login body
        JSONObject loginBody = new JSONObject();
        loginBody.put("username", "Alex");
        loginBody.put("password", "123");

        // send login request
        JSONObject loginResponse = sendPost("/login", loginBody);
        String userId = loginResponse.getString("userId");

        // build join body
        JSONObject joinBody = new JSONObject();
        joinBody.put("rideId", "R1");
        joinBody.put("entityId", userId);
        joinBody.put("scannedCode", ride.getQrCode());

        // send join request
        JSONObject joinResponse = sendPost("/scanToJoin", joinBody);
        assertTrue(joinResponse.getBoolean("success"));
        assertEquals(1, joinResponse.getInt("position"));

        // build queue body
        JSONObject queueBody = new JSONObject();
        queueBody.put("rideId", "R1");

        // send view queue request
        JSONObject queueResponse = sendPost("/viewQueue", queueBody);
        assertEquals(1, queueResponse.getJSONArray("queue").length());
        assertEquals("Alex", queueResponse.getJSONArray("queue").getString(0));
    }

    @Test
    void testScanToLeave() throws IOException {

        // create ride
        Ride ride = new Ride("R2", "Corkscrew", new VirtualQueue(), 15, 2);
        rm.addRide(ride);
        db.addRide("R2", "Corkscrew", 15, 2, ride.getQrCode(), ride.getQrImagePath());

        // register user
        JSONObject reg = new JSONObject();
        reg.put("username", "Oliver");
        reg.put("password", "pw");
        sendPost("/register", reg);

        // login user
        JSONObject login = new JSONObject();
        login.put("username", "Oliver");
        login.put("password", "pw");
        JSONObject loginRes = sendPost("/login", login);
        String userId = loginRes.getString("userId");

        // join queue
        JSONObject join = new JSONObject();
        join.put("rideId", "R2");
        join.put("entityId", userId);
        join.put("scannedCode", ride.getQrCode());
        sendPost("/scanToJoin", join);

        // build leave body
        JSONObject leave = new JSONObject();
        leave.put("rideId", "R2");
        leave.put("entityId", userId);
        leave.put("scannedCode", ride.getQrCode());

        // send leave request
        JSONObject leaveRes = sendPost("/scanToLeave", leave);
        assertTrue(leaveRes.getBoolean("success"));
    }

    @Test
    void testScanToJoinGroupAndLeaveGroup() throws IOException {

        // build first user registration
        JSONObject reg1 = new JSONObject();
        reg1.put("username", "Carson");
        reg1.put("password", "pw1");
        JSONObject userResponse1 = sendPost("/register", reg1);
        String userId1 = userResponse1.getString("userId");

        // build second user registration
        JSONObject reg2 = new JSONObject();
        reg2.put("username", "Liam");
        reg2.put("password", "pw2");
        JSONObject userResponse2 = sendPost("/register", reg2);
        String userId2 = userResponse2.getString("userId");

        // build group creation body
        JSONObject groupBody = new JSONObject();
        groupBody.put("groupName", "RaptorCrew");
        groupBody.put("ownerId", userId1);

        // send group creation request
        JSONObject createResponse = sendPost("/createGroup", groupBody);
        String groupId = createResponse.getString("groupId");

        // build add member body
        JSONObject addMember = new JSONObject();
        addMember.put("groupId", groupId);
        addMember.put("userId", userId2);

        // send add member request
        JSONObject addResponse = sendPost("/addMemberToGroup", addMember);
        assertTrue(addResponse.getBoolean("success"));

        // build membership check body
        JSONObject checkMember = new JSONObject();
        checkMember.put("groupId", groupId);
        checkMember.put("userId", userId2);

        // send membership check request
        JSONObject checkResponse = sendPost("/isMember", checkMember);
        assertTrue(checkResponse.getBoolean("isMember"));

        // build remove member body
        JSONObject removeMember = new JSONObject();
        removeMember.put("groupId", groupId);
        removeMember.put("userId", userId2);

        // send remove member request
        JSONObject removeResponse = sendPost("/removeMemberFromGroup", removeMember);
        assertTrue(removeResponse.getBoolean("success"));

        // send membership recheck request
        checkResponse = sendPost("/isMember", checkMember);
        assertFalse(checkResponse.getBoolean("isMember"));
    }

    @Test
    void testScanToLeaveGroup() throws IOException {

        // register owner
        JSONObject regOwner = new JSONObject();
        regOwner.put("username", "Eli");
        regOwner.put("password", "pw");
        JSONObject ownerRes = sendPost("/register", regOwner);
        String ownerId = ownerRes.getString("userId");

        // register member
        JSONObject regMem = new JSONObject();
        regMem.put("username", "Kai");
        regMem.put("password", "pw");
        JSONObject memRes = sendPost("/register", regMem);
        String memId = memRes.getString("userId");

        // create group
        JSONObject groupBody = new JSONObject();
        groupBody.put("groupName", "BlueStreakBoys");
        groupBody.put("ownerId", ownerId);
        JSONObject groupRes = sendPost("/createGroup", groupBody);
        String groupId = groupRes.getString("groupId");

        // add member
        JSONObject add = new JSONObject();
        add.put("groupId", groupId);
        add.put("userId", memId);
        sendPost("/addMemberToGroup", add);

        // create ride
        Ride ride = new Ride("R3", "Blue Streak", new VirtualQueue(), 12, 2);
        rm.addRide(ride);
        db.addRide("R3", "Blue Streak", 12, 2, ride.getQrCode(), ride.getQrImagePath());

        // join group queue
        JSONObject join = new JSONObject();
        join.put("rideId", "R3");
        join.put("groupId", groupId);
        join.put("scannedCode", ride.getQrCode());
        sendPost("/scanToJoinGroup", join);

        // leave group queue
        JSONObject leave = new JSONObject();
        leave.put("rideId", "R3");
        leave.put("groupId", groupId);
        leave.put("scannedCode", ride.getQrCode());
        JSONObject leaveRes = sendPost("/scanToLeaveGroup", leave);

        assertTrue(leaveRes.getBoolean("success"));
    }

    @Test
    void testMultipleUsersQueueAndPositions() throws IOException {

        // create ride
        Ride ride = new Ride("R6", "Valravn", new VirtualQueue(), 10, 2);
        rm.addRide(ride);
        db.addRide("R6", "Valravn", 10, 2, ride.getQrCode(), ride.getQrImagePath());

        // user list
        String[] usernames = {"Mason", "Ethan", "Noah"};
        String[] userIds = new String[3];

        // loop register + login + join
        for (int i = 0; i < usernames.length; i++) {

            // register body
            JSONObject reg = new JSONObject();
            reg.put("username", usernames[i]);
            reg.put("password", "pw");
            JSONObject regResponse = sendPost("/register", reg);
            userIds[i] = regResponse.getString("userId");

            // login body
            JSONObject login = new JSONObject();
            login.put("username", usernames[i]);
            login.put("password", "pw");
            sendPost("/login", login);

            // join body
            JSONObject join = new JSONObject();
            join.put("rideId", "R6");
            join.put("entityId", userIds[i]);
            join.put("scannedCode", ride.getQrCode());

            // send join request
            JSONObject joinResponse = sendPost("/scanToJoin", join);
            assertTrue(joinResponse.getBoolean("success"));

            // build queue body
            JSONObject queueBody = new JSONObject();
            queueBody.put("rideId", "R6");

            // send queue request
            sendPost("/viewQueue", queueBody);
        }

        // final queue check
        JSONObject finalQueueBody = new JSONObject();
        finalQueueBody.put("rideId", "R6");
        JSONObject finalQueueResponse = sendPost("/viewQueue", finalQueueBody);
        assertEquals(3, finalQueueResponse.getJSONArray("queue").length());

        for (int i = 0; i < usernames.length; i++) {
            assertEquals(usernames[i], finalQueueResponse.getJSONArray("queue").getString(i));
        }

        // check each user’s position
        for (int i = 0; i < userIds.length; i++) {
            JSONObject posBody = new JSONObject();
            posBody.put("rideId", "R6");
            posBody.put("entityId", userIds[i]);

            JSONObject posResponse = sendPost("/userPosition", posBody);
            assertEquals(i + 1, posResponse.getInt("position"));
        }
    }

    @Test
    void testInvalidQRJoinFails() throws IOException {

        // create ride
        Ride ride = new Ride("R4", "Rougarou", new VirtualQueue(), 20, 2);
        rm.addRide(ride);
        db.addRide("R4", "Rougarou", 20, 2, ride.getQrCode(), ride.getQrImagePath());

        // register + login
        JSONObject reg = new JSONObject();
        reg.put("username", "Isaac");
        reg.put("password", "pw");
        sendPost("/register", reg);

        JSONObject login = new JSONObject();
        login.put("username", "Isaac");
        login.put("password", "pw");
        JSONObject loginRes = sendPost("/login", login);
        String userId = loginRes.getString("userId");

        // build invalid join body
        JSONObject join = new JSONObject();
        join.put("rideId", "R4");
        join.put("entityId", userId);
        join.put("scannedCode", "WRONG-CODE");

        // send join request
        JSONObject response = sendPost("/scanToJoin", join);
        assertTrue(response.has("error"));
    }

    @Test
    void testViewWaitTime() throws IOException {

        // create ride
        Ride ride = new Ride("R5", "Wild Mouse", new VirtualQueue(), 15, 1);
        rm.addRide(ride);
        db.addRide("R5", "Wild Mouse", 15, 1, ride.getQrCode(), ride.getQrImagePath());

        // build body
        JSONObject body = new JSONObject();
        body.put("rideId", "R5");

        // send wait request
        JSONObject response = sendPost("/viewWaitTime", body);
        assertTrue(response.has("waitTime"));
    }

    @Test
    void testViewQueueInvalidRide() throws IOException {

        // build body
        JSONObject body = new JSONObject();
        body.put("rideId", "BAD_RIDE");

        // send request
        JSONObject response = sendPost("/viewQueue", body);
        assertTrue(response.has("error"));
    }

    @Test
    void testUserPositionNotInQueue() throws IOException {

        // create ride
        Ride ride = new Ride("R7", "Gemini", new VirtualQueue(), 25, 2);
        rm.addRide(ride);
        db.addRide("R7", "Gemini", 25, 2, ride.getQrCode(), ride.getQrImagePath());

        // register + login
        JSONObject reg = new JSONObject();
        reg.put("username", "Mark");
        reg.put("password", "pw");
        JSONObject regRes = sendPost("/register", reg);
        String userId = regRes.getString("userId");

        // build position request
        JSONObject posBody = new JSONObject();
        posBody.put("rideId", "R7");
        posBody.put("entityId", userId);

        // send request
        JSONObject posRes = sendPost("/userPosition", posBody);
        assertEquals(-1, posRes.getInt("position"));
    }

    @Test
    void testCreateGroupMissingOwnerFails() throws IOException {

        // build incomplete body
        JSONObject body = new JSONObject();
        body.put("groupName", "the best group");

        // send request
        JSONObject res = sendPost("/createGroup", body);
        assertTrue(res.has("error"));
    }

    @Test
    void testScanToJoinGroupInvalidRide() throws IOException {

        // register user
        JSONObject reg = new JSONObject();
        reg.put("username", "Max");
        reg.put("password", "pw");
        JSONObject regRes = sendPost("/register", reg);
        String userId = regRes.getString("userId");

        // create group
        JSONObject body = new JSONObject();
        body.put("groupName", "InvalidRideGroup");
        body.put("ownerId", userId);
        JSONObject groupRes = sendPost("/createGroup", body);
        String groupId = groupRes.getString("groupId");

        // invalid ride attempt
        JSONObject join = new JSONObject();
        join.put("rideId", "BAD_RIDE");
        join.put("groupId", groupId);
        join.put("scannedCode", "ANYTHING");

        JSONObject response = sendPost("/scanToJoinGroup", join);
        assertTrue(response.has("error"));
    }

    //helper to mimic sending post request//
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

}

