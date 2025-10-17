package com.example.backend;
import java.util.*;

public class QueueTests {
    public static void main(String[] args) {
      VirtualQueue queue = new VirtualQueue();

        //Creating manual users
        User john = new User("John");
        User adam = new User("Adam");
        User chris = new User("Chris");
        User wendy = new User("Wendy");

        //Group creation where John is owner
        Group group = new Group("LotsOfRides", john);
        group.addMember(adam);
        group.addMember(chris);

        // John's group joins the queue
        queue.joinQueue(group);

        // Another individual joins, separate from the group
        queue.joinQueue(wendy);

        //Queue Methods
        queue.viewQueue();
        queue.leaveQueue(group.getGroupId());
        queue.viewQueue();
    }
}
