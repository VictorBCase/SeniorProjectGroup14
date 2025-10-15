package com.example.backend;
import java.util.*;

public class QueueTests {
    public static void main(String[] args) {
      VirtualQueue vq = new VirtualQueue();

        User TestUser = new User("TestUser");
        User TestUser2 = new User("TestUser2");
        vq.joinQueue(TestUser);
        vq.joinQueue(TestUser2);

        List<User> groupMembers = Arrays.asList(
            new User("User1"),
            new User("User2"),
            new User("USer3")
        );
        Group testGroup = new Group("Group1", groupMembers);
        vq.joinQueue(testGroup);

        vq.viewQueue();

        vq.leaveQueue(testGroup.getGroupId());
        vq.viewQueue();

        vq.serveNext();
        vq.viewQueue();
    }
}