package com.example.backend;

import java.util.LinkedList;
import java.util.Queue;

public class VirtualQueue {
    private Queue<User> queue = new LinkedList<>();

    //Check current queue size
    public int size() {
        return queue.size();
    }


    //Add user to queue
    public void joinQueue(User user) {
        queue.add(user);
        System.out.println(user.getName() + " joined the queue. Position: " + queue.size());
    }

    //Remove next user from queue
    public User serveNext() {
        User next = queue.poll();
        if (next != null) {
            System.out.println("It is now " + next.getName() + "'s turn for the ride. ");
        } 
        else {
            System.out.println("Queue is empty.");
        }
        return next;
    }

    public boolean leaveQueue(User user) {
        String userId = user.getId();
        for(User people : queue){
            if(people.getId().equals(userId)){
                queue.remove(people);
                System.out.println(("You have succesfully left the queue"));
                return true;
            }
        }
        System.out.println("You are not in a queue currently");
        return false;
    }


    //View all users in the VQueue
    public void viewQueue() {
        if (queue.isEmpty()) {
            System.out.println("The queue is empty.");
            return;
        }
        
        System.out.println("Current queue:");
        int position = 1;
        for (User user : queue) {
            System.out.println(position + ". " + user.getName() + " (ID: " + user.getId() + ")");
            position++;
        }
    }
}



