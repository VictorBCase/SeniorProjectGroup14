package com.example.backend;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import java.net.URI;

public class ChatClientTest {
/*     public static void main(String[] args) throws Exception {
        ChatServer server = new ChatServer(8887);
        server.start();
        System.out.println("Server started on port 8887");
    
        URI uri = new URI("ws://localhost:8887");
        Gson gson = new Gson();
       
        WebSocketClient client = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake handshake) {
                System.out.println("Connected to server");

                //Part 1: Test authenticating user
                JsonObject auth = new JsonObject();
                auth.addProperty("action", "auth");
                auth.addProperty("username", "testuser");
                auth.addProperty("userId", "u1"); 
                send(gson.toJson(auth));

                //Part 2: Test user joining a group
                JsonObject join = new JsonObject();
                join.addProperty("action", "join");
                join.addProperty("groupId", "group-123");
                send(gson.toJson(join));

                //Part 3: Test sending the chat message
                JsonObject msg = new JsonObject();
                msg.addProperty("action", "message");
                msg.addProperty("groupId", "group-123");
                msg.addProperty("content", "Hello from client!");
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

        Thread.sleep(30_000);
        client.close();
    } */
}
