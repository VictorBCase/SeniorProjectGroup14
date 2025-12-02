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

    public ChatServer(InetSocketAddress address) {
        super(address);
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

}
