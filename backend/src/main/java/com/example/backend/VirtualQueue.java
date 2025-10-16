package com.example.backend;
import java.util.*;

public class VirtualQueue {
    private Queue<Object> queue = new LinkedList<>(); //Changed to object to account for class user and group
    private int size = 0;

    public int getSize() {
        return size;
    }

    public void joinQueue(User user) {
        queue.add(user);
        size++;
        System.out.println(user.getName() + " joined the queue. Position: " + queue.size());
    }

    public void joinQueue(Group group) {
        queue.add(group);
        size = size + group.getSize();
        System.out.println(group.getGroupName() + " (Group) joined the queue with members: " + group.memberNames());
    }

    //Remove current object in queue (user / group)
    public void serveNext() {
        Object next = queue.poll();
        if (next == null) {
            System.out.println("Queue is empty.");
            return;
        }

        if (next instanceof User) { //User 
            User user = (User) next;
            System.out.println("Serving " + user.getName());
        } else if (next instanceof Group) { //Group
            Group group = (Group) next;
            System.out.println("Serving group: " + group.getGroupName() + " (" + group.memberNames() + ")");
        }
    }

    //Manually leave queue early
    public boolean leaveQueue(String id) {
        Iterator<Object> iterator = queue.iterator();
        while (iterator.hasNext()) {
            Object obj = iterator.next();

            if (obj instanceof User) {
                User user = (User) obj;
                if (user.getId().equals(id)) {
                    iterator.remove();
                    size--;
                    System.out.println(user.getName() + " left the queue voluntarily.");
                    return true;
                }
            } else if (obj instanceof Group) {
                Group group = (Group) obj;
                if (group.getGroupId().equals(id)) {
                    iterator.remove();
                    size = size - group.getSize();
                    System.out.println(group.getGroupName() + " (Group) left the queue voluntarily.");
                    return true;
                }
            }
        }
        System.out.println("No user or group found with that ID.");
        return false;
    }

    //View contents of queue
    public void viewQueue() {
        if (queue.isEmpty()) {
            System.out.println("Queue is currently empty.");
            return;
        }

        System.out.println("\nCurrent V-Queue:");
        int position = 1;
        for (Object obj : queue) {
            if (obj instanceof User) {
                User user = (User) obj;
                System.out.println(position + ". " + user.getName() + " (User ID: " + user.getId() + ")");
            } else if (obj instanceof Group) {
                Group group = (Group) obj;
                System.out.println(position + ". [Group: " + group.getGroupName() + "] Members: " + group.memberNames());
            }
            position++;
        }
    }

}
