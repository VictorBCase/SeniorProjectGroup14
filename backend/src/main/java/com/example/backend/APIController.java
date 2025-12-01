package com.example.backend;
import com.sun.net.httpserver.*;
import org.json.*;
import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Class that combines database and backend together for use on front end
 * simulates server and takes specific post request then executes a function
 */
public class APIController {


    private final DatabaseManager db;
    private final UserManager um;
    private final RideManager rm;
    private final GroupManager gm;
    private HttpServer server;
    /**
     * creates a APIController with given paramters
     * @param db   the database instance
     * @param um   the usermanager
     * @param rm   rides loaded from database
     * @param port the port number server is running on
     * @throws IOException if server fails to load
     */
    public APIController(DatabaseManager db, UserManager um, RideManager rm,GroupManager gm, int port) throws IOException {
        this.db = db;
        this.um = um;
        this.rm = rm;
        this.gm = gm;
        this.server = HttpServer.create(new InetSocketAddress(port), 0);

        //user login
        server.createContext("/register", this::register);
        server.createContext("/login", this::login);

        //single user
        server.createContext("/scanToJoin", this::scanToJoin);
        server.createContext("/scanToLeave", this::scanToLeave);
        server.createContext("/viewWaitTime", this::viewWaitTime);
        server.createContext("/viewQueue", this::viewQueue);
        server.createContext("/userPosition", this::userPosition);

        //groups
        server.createContext("/createGroup", this::createGroup);
        server.createContext("/addMemberToGroup", this::addMemeberToGroup);
        server.createContext("/removeMemberFromGroup", this::removeMemberFromGroup);
        server.createContext("/scanToJoinGroup", this::scanToJoinGroup);
        server.createContext("/scanToLeaveGroup", this::scanToLeaveGroup);
        server.createContext("/isMember", this::isMember);
        server.setExecutor(null);
        server.start();
        System.out.println("VQueue server running on http://localhost:" + port + "/");
    }

    /**
     * method to end server connection
     */
    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }
    /**
     * registers a new user by username and password
     * creates a user through usermanager and then create database entry
     * @param exchange     the register user exchange expect a username and password
     * @throws IOException thrown if JSON process fails
     */
    private void register(HttpExchange exchange) throws IOException {
        try {
            if(!requirePostRequest(exchange)) return;

            JSONObject body = new JSONObject(readBody(exchange));
            String username = body.optString("username", null);
            String password = body.optString("password", null);

            if (username == null || password == null){
                sendError(exchange, 401, "username and password required");
                return;
            }

            else if (!um.createUser(username, password)){
                sendError(exchange, 402, "username already exists");
                return;
            }

            User u = um.login(username, password);
            db.addUser(u.getId(),u.getUsername(),u.getPasswordHash());

            JSONObject response = new JSONObject();
            response.put("success", true);
            response.put("userId", u.getId());

            sendJSON(exchange, response);

        } catch (IOException e) {
            sendError(exchange, 500, e.toString());
        }
    }

    /**
     * Tries to login the user check if username and password is a valid combination
     * through database and usermanager
     * @param exchange     the login user exchange expect a username and password
     * @throws IOException thrown if JSON process fails
     */
    private void login(HttpExchange exchange) throws IOException {
        try{
            if (!requirePostRequest(exchange)){return;}

            JSONObject body = new JSONObject(readBody(exchange));
            String username = body.optString("username", null);
            String password = body.optString("password", null);

            User u = um.login(username, password);

            JSONObject response = new JSONObject();
            response.put("success", u!=null);

            if (u != null){
                boolean isValidLogin = db.validateLogin(u.getUsername(), u.getPasswordHash());
                response.put("success", isValidLogin);

                if(isValidLogin){
                    response.put("userId", u.getId());
                }
            }

            sendJSON(exchange, response);

        } catch (IOException e) {
            sendError(exchange, 500, e.toString());
        }
    }

    /**
     * attempts to add user to ride queue
     * done through checking valid qr code
     * and then updating virtual queue and database
     * @param exchange     the scanToJoin request expects ride id entity id and scanned qr code
     * @throws IOException thrown if JSON process fails
     */
    private void scanToJoin(HttpExchange exchange) throws IOException {
        try{
            if(!requirePostRequest(exchange)){return;}

            JSONObject body = new JSONObject(readBody(exchange));

            String rideId = body.getString("rideId");
            String userId = body.getString("entityId");
            String qr = body.getString("scannedCode");

            Ride ride = rm.getRide(rideId);
            if(ride == null){
                sendError(exchange, 403, "ride not found");
            }

            else if(!qr.equals(ride.getQrCode())){
                sendError(exchange, 404, "invalid QR code");
            }

            ride.scanToJoin(um.getUser(userId), qr);
            int position = ride.getQueue().getSize();
            System.out.println(position);
            db.joinQueue(rideId, userId, "user", position);
            ride.updateWaitTime();
            double waitTime = ride.getWaitTime();
            db.updateWaitTime(rideId, waitTime);
            JSONObject response = new JSONObject();
            response.put("success", true);
            response.put("position", position);

            sendJSON(exchange, response);
        } catch (IOException e) {
            sendError(exchange, 500, e.toString());
        }
    }

    /**
     * Will remove a user from the ride queue
     * done by confirming correct qr code then removing from database
     * @param exchange     the scan to leave request expect ride id entity id and qr code
     * @throws IOException thrown if JSON process fails
     */
    private void scanToLeave(HttpExchange exchange)throws IOException{
        try {
            if (!requirePostRequest(exchange)){return;}

            JSONObject body = new JSONObject(readBody(exchange));

            String rideId = body.getString("rideId");
            String userId = body.getString("entityId");
            String qr = body.getString("scannedCode");
            Ride ride = rm.getRide(rideId);

            if(ride == null){
                sendError(exchange, 403, "ride not found");
            }

            else if(!qr.equals(ride.getQrCode())){
                sendError(exchange, 404, "invalid QR code");
            }
            ride.scanToLeave(um.getUser(userId), qr);
            boolean removed = db.leaveQueue(rideId, userId);
            ride.updateWaitTime();
            double waitTime = ride.getWaitTime();
            db.updateWaitTime(rideId, waitTime);

            JSONObject response = new JSONObject();
            response.put("success", removed);
            sendJSON(exchange, response);

        } catch (IOException e) {
            sendError(exchange, 500, e.toString());
        }
    }

    /**
     * will get the wait time for a ride
     * @param exchange     the view wait time request expect a ride id
     * @throws IOException thrown if JSON process fails
     */
    private void viewWaitTime(HttpExchange exchange) throws IOException {
        try {
            if (!requirePostRequest(exchange)){return;}

            JSONObject body = new JSONObject(readBody(exchange));

            String rideId = body.getString("rideId");

            double wait = db.getWaitTime(rideId);

            JSONObject response = new JSONObject();
            response.put("waitTime", wait);
            sendJSON(exchange, response);

        } catch (IOException e) {
            sendError(exchange, 500, e.toString());
        }

    }

    /**
     * Retrieves the entire queue for a ride.
     * Uses database to load queue entries sorted by position.
     * @param exchange  the request expects rideId
     * @throws IOException thrown if JSON process fails
     */
    private void viewQueue(HttpExchange exchange) throws IOException {
        try {
            if (!requirePostRequest(exchange)) return;

            JSONObject body = new JSONObject(readBody(exchange));
            String rideId = body.getString("rideId");
            Ride ride = rm.getRide(rideId);

            List<String> names = ride.getQueue().getQueueAsNameList();
            JSONObject response = new JSONObject();
            response.put("queue", new JSONArray(names));

            sendJSON(exchange, response);

        } catch (IOException e) {
            sendError(exchange, 500, e.toString());
        }
    }

    /**
     * Retrieves a user's current position in a ride queue.
     * @param exchange the request expects rideId and entityId
     * @throws IOException thrown if JSON process fails
     */
    private void userPosition(HttpExchange exchange) throws IOException {
        try {
            if (!requirePostRequest(exchange)) return;

            JSONObject body = new JSONObject(readBody(exchange));
            String rideId = body.getString("rideId");
            String userId = body.getString("entityId");

            int pos = db.getUserQueuePosition(rideId, userId);

            JSONObject response = new JSONObject();
            response.put("position", pos);

            sendJSON(exchange, response);

        } catch (IOException e) {
            sendError(exchange, 500, e.toString());
        }
    }

    /**
     * Creates a new group with a given group name and owner.
     * @param exchange the request expects groupName and ownerId
     * @throws IOException thrown if JSON process fails
     */
    private void createGroup(HttpExchange exchange) throws IOException {
        try {
            if (!requirePostRequest(exchange)) return;

            JSONObject body = new JSONObject(readBody(exchange));

            String groupName = body.getString("groupName");
            String ownerId = body.getString("ownerId");

            String groupId = UUID.randomUUID().toString();
            User user = um.getUser(ownerId);
            Group group = new Group(groupName, user, groupId);
            gm.addGroup(group);
            db.addGroup(groupId, groupName, ownerId);

            JSONObject response = new JSONObject();
            response.put("groupId", groupId);

            sendJSON(exchange, response);

        } catch (IOException e) {
            sendError(exchange, 500, e.toString());
        }
    }

    /**
     * Attempts to add a group into a ride queue using QR code.
     * @param exchange expects rideId, groupId, scannedCode
     * @throws IOException thrown if JSON fails
     */
    private void scanToJoinGroup(HttpExchange exchange) throws IOException {
        try {
            if (!requirePostRequest(exchange)) return;

            JSONObject body = new JSONObject(readBody(exchange));

            String rideId = body.getString("rideId");
            String groupId = body.getString("groupId");
            String qr = body.getString("scannedCode");

            Ride ride = rm.getRide(rideId);
            if (ride == null) {
                sendError(exchange, 403, "ride not found");
                return;
            }

            if (!qr.equals(ride.getQrCode())) {
                sendError(exchange, 404, "invalid QR code");
                return;
            }
            Group group = gm.getGroup(groupId);
            ride.scanToJoin(group, qr);
            int position = ride.getQueue().getSize();
            db.joinQueue(rideId, groupId, "group", position);

            ride.updateWaitTime();
            db.updateWaitTime(rideId, ride.getWaitTime());

            JSONObject response = new JSONObject();
            response.put("success", true);
            response.put("position", position);

            sendJSON(exchange, response);

        } catch (IOException e) {
            sendError(exchange, 500, e.toString());
        }
    }

    /**
     * Removes a group from a ride queue after QR confirmation.
     * @param exchange expects rideId, groupId, scannedCode
     * @throws IOException thrown if JSON fails
     */
    private void scanToLeaveGroup(HttpExchange exchange) throws IOException {
        try {
            if (!requirePostRequest(exchange)) return;

            JSONObject body = new JSONObject(readBody(exchange));

            String rideId = body.getString("rideId");
            String groupId = body.getString("groupId");
            String qr = body.getString("scannedCode");

            Ride ride = rm.getRide(rideId);
            if (ride == null) {
                sendError(exchange, 403, "ride not found");
                return;
            }

            if (!qr.equals(ride.getQrCode())) {
                sendError(exchange, 404, "invalid QR code");
                return;
            }
            Group group = gm.getGroup(groupId);

            ride.scanToLeave(group, qr);
            boolean removed = db.leaveQueue(rideId, groupId);

            ride.updateWaitTime();
            db.updateWaitTime(rideId, ride.getWaitTime());

            JSONObject response = new JSONObject();
            response.put("success", removed);

            sendJSON(exchange, response);

        } catch (IOException e) {
            sendError(exchange, 500, e.toString());
        }
    }

    /**
     * Adds a user to a group in the database.
     * @param exchange expects groupId and userId
     * @throws IOException thrown if JSON fails
     */
    private void addMemeberToGroup(HttpExchange exchange) throws IOException {
        try {
            if (!requirePostRequest(exchange)) return;

            JSONObject body = new JSONObject(readBody(exchange));
            String groupId = body.getString("groupId");
            String userId = body.getString("userId");

            Group group = gm.getGroup(groupId);
            User user = um.getUser(userId);

            group.addMember(user);
            db.addMemberToGroup(groupId, userId);

            JSONObject response = new JSONObject();
            response.put("success", true);
            sendJSON(exchange, response);

        } catch (IOException e) {
            sendError(exchange, 500, e.toString());
        }
    }

    /**
     * Removes a user from a group in the database.
     * @param exchange expects groupId and userId
     * @throws IOException thrown if JSON fails
     */
    private void removeMemberFromGroup(HttpExchange exchange) throws IOException {
        try {
            if (!requirePostRequest(exchange)) return;

            JSONObject body = new JSONObject(readBody(exchange));
            String groupId = body.getString("groupId");
            String userId = body.getString("userId");

            Group group = gm.getGroup(groupId);
            User user = um.getUser(userId);
            group.removeMember(user);
            db.removeMemberFromGroup(groupId, userId);

            JSONObject response = new JSONObject();
            response.put("success", true);
            sendJSON(exchange, response);

        } catch (IOException e) {
            sendError(exchange, 500, e.toString());
        }
    }

    /**
     * Checks if a user belongs to a specific group.
     * @param exchange expects groupId and userId
     * @throws IOException thrown if JSON fails
     */
    private void isMember(HttpExchange exchange) throws IOException {
        try {
            if (!requirePostRequest(exchange)) return;

            JSONObject body = new JSONObject(readBody(exchange));
            String groupId = body.getString("groupId");
            String userId = body.getString("userId");

            boolean isMember = db.isUserInGroup(groupId, userId);

            JSONObject response = new JSONObject();
            response.put("isMember", isMember);

            sendJSON(exchange, response);

        } catch (IOException e) {
            sendError(exchange, 500, e.toString());
        }
    }

    //helper methods

    /**
     * Reads the body request from the HttpExchange
     * done with manual buffer to support all android versions
     * @param exchange     the HTTP request exchange
     * @return             the request body as a UTF-8 string
     * @throws IOException I)Excpetion if an I/O error happens
     */
    private String readBody(HttpExchange exchange) throws IOException{
        InputStream is = exchange.getRequestBody();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int nRead;

        //Reads request body chunk by chunk until and written into the buffer
        while((nRead = is.read(data, 0, data.length)) != -1){
            buffer.write(data, 0, nRead);
        }

        //convert bytes to UTF-8 string
        return buffer.toString("UTF-8");
    }

    /**
     * sends a JSON response
     * @param exchange     the exchange that needs response
     * @param json         the JSON body being sent
     * @throws IOException if the writing fails
     */
    private void sendJSON(HttpExchange exchange, JSONObject json) throws IOException{
        byte[] bytes = json.toString().getBytes();

        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, bytes.length);

        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }

    /**
     * helper method to send an error
     * @param exchange     the exchange that has an error
     * @param code         the code for the error we are giving it
     * @param message      the message the user should see
     * @throws IOException if writing fails
     */
    private void sendError(HttpExchange exchange, int code, String message) throws IOException{
        JSONObject json = new JSONObject();
        json.put("error", message);

        byte[] bytes = json.toString().getBytes();

        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(code, bytes.length);

        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }

    /**
     * make sure the exchange is a post request
     * @param exchange     the exchange being checked
     * @return             true if a post request false otherwise
     * @throws IOException if checking the exchange fails
     */
    private boolean requirePostRequest(HttpExchange exchange) throws IOException{
        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")){
            sendError(exchange, 400, "POST required");
            return false;
        }
        return true;
    }
}
