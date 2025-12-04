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
        server.createContext("/addMemberToGroup", this::addMemberToGroup);
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

            User user = um.login(username, password);
            db.addUser(user.getId(),user.getUsername(),user.getPasswordHash());

            JSONObject response = new JSONObject();
            response.put("success", true);
            response.put("userId", user.getId());

            sendJSON(exchange, response);

        } catch (IOException e) {
            sendError(exchange, 500, e.toString());
            return;
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

            User user = um.login(username, password);

            JSONObject response = new JSONObject();
            response.put("success", user!=null);

            if (user != null){
                boolean isValidLogin = db.validateLogin(user.getUsername(), user.getPasswordHash());
                response.put("success", isValidLogin);

                if(isValidLogin){
                    response.put("userId", user.getId());
                }
            }

            sendJSON(exchange, response);

        } catch (IOException e) {
            sendError(exchange, 500, e.toString());
            return;
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
                return;
            }

            else if(!qr.equals(ride.getQrCode())){
                sendError(exchange, 404, "invalid QR code");
                return;
            }

            User user = um.getUser(userId);
            if (user == null) {
                sendError(exchange, 406, "user not found");
                return;
            }

            ride.scanToJoin(user, qr);
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
            return;
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
                return;
            }

            else if(!qr.equals(ride.getQrCode())){
                sendError(exchange, 404, "invalid QR code");
                return;
            }

            User user = um.getUser(userId);
            if (user == null) {
                sendError(exchange, 406, "user not found");
                return;
            }

            ride.scanToLeave(user, qr);
            boolean removed = db.leaveQueue(rideId, userId);
            ride.updateWaitTime();
            double waitTime = ride.getWaitTime();
            db.updateWaitTime(rideId, waitTime);

            JSONObject response = new JSONObject();
            response.put("success", removed);
            sendJSON(exchange, response);

        } catch (IOException e) {
            sendError(exchange, 500, e.toString());
            return;
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
            return;
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
            if (ride == null) {
                sendError(exchange, 403, "ride not found");
                return;
            }

            List<String> names = ride.getQueue().getQueueAsNameList();

            JSONObject response = new JSONObject();
            JSONArray arr = new JSONArray();
            for (String n : names) {
                arr.put(n == null ? JSONObject.NULL : n);
            }

            response.put("queue", arr);

            sendJSON(exchange, response);

        } catch (Exception e) {
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
            return;
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
            System.out.println(ownerId);
            User user = um.getUser(ownerId);
            System.out.println(user.getUsername());
            if(user == null){
                sendError(exchange, 406, "user not found");
                return;
            }

            Group group = new Group(groupName, user, groupId);
            gm.addGroup(group);
            db.addGroup(groupId, groupName, ownerId);

            JSONObject response = new JSONObject();
            response.put("groupId", groupId);

            sendJSON(exchange, response);

        } catch (Exception e) {
            sendError(exchange, 500, e.toString());
            return;
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
            if (group == null) {
                sendError(exchange, 405, "group not found");
                return;
            }

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
            return;
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
            if (group == null) {
                sendError(exchange, 405, "group not found");
                return;
            }

            ride.scanToLeave(group, qr);
            boolean removed = db.leaveQueue(rideId, groupId);

            ride.updateWaitTime();
            db.updateWaitTime(rideId, ride.getWaitTime());

            JSONObject response = new JSONObject();
            response.put("success", removed);

            sendJSON(exchange, response);

        } catch (IOException e) {
            sendError(exchange, 500, e.toString());
            return;
        }
    }

    /**
     * Adds a user to a group in the database.
     * @param exchange expects groupId and userId
     * @throws IOException thrown if JSON fails
     */
    private void addMemberToGroup(HttpExchange exchange) throws IOException {
        try {
            if (!requirePostRequest(exchange)) return;

            JSONObject body = new JSONObject(readBody(exchange));
            String groupId = body.getString("groupId");
            String userId = body.getString("userId");

            Group group = gm.getGroup(groupId);
            if (group == null) {
                sendError(exchange, 405, "group not found");
                return;
            }

            User user = um.getUser(userId);
            if(user == null){
                sendError(exchange, 406, "user not found");
                return;
            }

            group.addMember(user);
            db.addMemberToGroup(groupId, userId);

            JSONObject response = new JSONObject();
            response.put("success", true);
            sendJSON(exchange, response);

        } catch (IOException e) {
            sendError(exchange, 500, e.toString());
            return;
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
            if (group == null) {
                sendError(exchange, 405, "group not found");
                return;
            }

            User user = um.getUser(userId);
            if(user == null){
                sendError(exchange, 406, "user not found");
                return;
            }
            group.removeMember(user);
            db.removeMemberFromGroup(groupId, userId);

            JSONObject response = new JSONObject();
            response.put("success", true);
            sendJSON(exchange, response);

        } catch (IOException e) {
            sendError(exchange, 500, e.toString());
            return;
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
            return;
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
