package com.example.backend;

import com.example.backend.User;  //User class

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;


import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/*Minimal WebSocket chat server with JSON Protocols:
 *  - auth   
 *  - join
 *  - leave 
 *  - msg   
 */

public class ChatServer extends WebSocketServer {
    private final Gson gson = new Gson();
    private final Map<WebSocket, User> connectionUsers = new ConcurrentHashMap<>();
    private final Map<String, Set<WebSocket>> groupMembers = new ConcurrentHashMap<>();

    public ChatServer(InetSocketAddress address) { //constructor for an address
        super(address);
    }

    public ChatServer(int port) { //constructor for a port number
        super(new InetSocketAddress(port));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("New connection from " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("Connection closed: " + conn.getRemoteSocketAddress() + " reason=" + reason);
        connectionUsers.remove(conn);        // needed to remove from any group sets and user map
        groupMembers.values().forEach(set -> set.remove(conn));
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        try {
            JsonObject json = JsonParser.parseString(message).getAsJsonObject();
            String action = json.has("action") ? json.get("action").getAsString() : "";

            switch (action) {
                case "auth":
                    handleAuth(conn, json);
                    break;
                case "join":
                    handleJoin(conn, json);
                    break;
                case "leave":
                    handleLeave(conn, json);
                    break;
                case "message":
                    handleMessage(conn, json);
                    break;
                default:
                    sendError(conn, "unknown_action", "Unknown action: " + action);
            }
        } catch (Exception e) {
            sendError(conn, "bad_request", "Invalid JSON or server error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.err.println("WebSocket error: " + ex.getMessage());
        ex.printStackTrace();
        if (conn != null) {
            connectionUsers.remove(conn);
            groupMembers.values().forEach(set -> set.remove(conn));
        }
    }

    @Override
    public void onStart() {
        System.out.println("ChatServer started on " + getAddress());
    }

    // --- handlers ---
    // AUTH 
    private void handleAuth(WebSocket conn, JsonObject json) {
        String username = json.has("username") ? json.get("username").getAsString() : null;
        String userId = json.has("userId") ? json.get("userId").getAsString() : null;

        if (username == null || userId == null) {
            sendError(conn, "auth_failed", "username and userId required");
            conn.close();
            return;
        }

        User user = new User(username,password); // REPLACE with a username from the database
    
        connectionUsers.put(conn, user);
        JsonObject resp = new JsonObject();
        resp.addProperty("type", "auth_ok");
        resp.addProperty("message", "Authenticated as " + username);
        conn.send(gson.toJson(resp));
    }
    // JOIN
    private void handleJoin(WebSocket conn, JsonObject json) {
        String groupId = json.has("groupId") ? json.get("groupId").getAsString() : null;
        if (groupId == null) {
            sendError(conn, "join_failed", "groupId required");
            return;
        }
        groupMembers.computeIfAbsent(groupId, k -> Collections.newSetFromMap(new ConcurrentHashMap<>())).add(conn);

        JsonObject resp = new JsonObject();
        resp.addProperty("type", "join_ok");
        resp.addProperty("groupId", groupId);
        conn.send(gson.toJson(resp));
    }
    // LEAVE
    private void handleLeave(WebSocket conn, JsonObject json) {
        String groupId = json.has("groupId") ? json.get("groupId").getAsString() : null;
        if (groupId == null) {
            sendError(conn, "leave_failed", "groupId required");
            return;
        }
        Set<WebSocket> members = groupMembers.get(groupId);
        if (members != null) {
            members.remove(conn);
        }
        JsonObject resp = new JsonObject();
        resp.addProperty("type", "leave_ok");
        resp.addProperty("groupId", groupId);
        conn.send(gson.toJson(resp));
    }
    // MESSAGE
    private void handleMessage(WebSocket conn, JsonObject json) {
        String groupId = json.has("groupId") ? json.get("groupId").getAsString() : null;
        String content = json.has("content") ? json.get("content").getAsString() : null;

        if (groupId == null || content == null) {
            sendError(conn, "message_failed", "groupId and content required");
            return;
        }

        User sender = connectionUsers.get(conn);
        if (sender == null) {
            sendError(conn, "not_authenticated", "Authenticate first");
            return;
        }

        //Message payload
        JsonObject payload = new JsonObject();
        payload.addProperty("type", "message");
        payload.addProperty("groupId", groupId);
        payload.addProperty("senderId", sender.getId());
        payload.addProperty("senderName", sender.getUsername());
        payload.addProperty("content", content);
        payload.addProperty("timestamp", LocalDateTime.now().toString());

        String jsonPayload = gson.toJson(payload);

        //broadcast message payload to group
        Set<WebSocket> members = groupMembers.getOrDefault(groupId, Collections.emptySet());
        for (WebSocket memberConn : members) {
            try {
                memberConn.send(jsonPayload);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void sendError(WebSocket conn, String code, String message) {
        JsonObject err = new JsonObject();
        err.addProperty("type", "error");
        err.addProperty("code", code);
        err.addProperty("message", message);
        conn.send(gson.toJson(err));
    }

   
    public static void main(String[] args) {
        ChatServer server = new ChatServer(new InetSocketAddress("0.0.0.0", 8887));
        server.start();
        System.out.println("Server started on port 8887");
    }

}
