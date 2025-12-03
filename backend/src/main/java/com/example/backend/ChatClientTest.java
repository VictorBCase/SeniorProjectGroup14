package com.example.backend;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.InetSocketAddress;
import java.net.URI;

public class ChatClientTest {
    public static void main(String[] args) throws Exception {
        // database manager
        DatabaseManager db = new DatabaseManager();
        UserManager userManager = new UserManager(db);

        //test account
        userManager.createUser("testuser1", "123");
        User user = userManager.login("testuser1", "123");
        System.out.println("ID = " + user.getId());

        //WebSocket server with user manager
        ChatServer server = new ChatServer(new InetSocketAddress("0.0.0.0", 8887), userManager);
        server.start();
        Thread.sleep(500);  //alowing server to fully start before continuing

        System.out.println("Server started on port 8887");

        URI uri = new URI("ws://localhost:8887");
        Gson gson = new Gson();

        WebSocketClient client = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake handshake) {
                System.out.println("Connected to server");

                //AUTHENTICATE
                JsonObject auth = new JsonObject();
                auth.addProperty("action", "auth");
                auth.addProperty("userId", user.getId());
                send(gson.toJson(auth));

                //JOIN GROUP
                JsonObject join = new JsonObject();
                join.addProperty("action", "join");
                join.addProperty("groupId", "group-123");
                send(gson.toJson(join));

                //SEND A MESSAGE
                JsonObject msg = new JsonObject();
                msg.addProperty("action", "message");
                msg.addProperty("groupId", "group-123");
                msg.addProperty("content", "Hello from testuser1!");
                send(gson.toJson(msg));
            }

            @Override
            public void onMessage(String message) {
                System.out.println("RECV: " + message);
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                System.out.println("Disconnected: " + reason);
            }

            @Override
            public void onError(Exception ex) {
                ex.printStackTrace();
            }
        };

        client.connectBlocking();

        Thread.sleep(30_000); // wait 30 seconds to receive messages
        client.close();
    }
}
