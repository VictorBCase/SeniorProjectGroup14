package com.example.backend;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Group {
    private String groupId;
    private String groupName;
    private List<User> members;
    private User owner;
    private int size = 0;

    // Constructor for group creation with an owner
    public Group(String groupName, User owner) {
         this.groupId = UUID.randomUUID().toString();
        this.groupName = groupName;
        this.owner = owner;
        this.members = new ArrayList<>();
        this.members.add(owner); // Owner is added to group on creation
        this.size = 1;
    }

    // Constructor for group if members are already provided
    public Group(String groupName, List<User> members) {
        this.groupId = UUID.randomUUID().toString();
        this.groupName = groupName;
        this.members = new ArrayList<>(members);
        this.size = members.size();
    }

    //Getter Methods
    public String getGroupId() { return groupId; }
    public String getGroupName() { return groupName; }
    public User getOwner() { return owner; }
    public List<User> getMembers() { return members; }
    public int getSize() { return size; }

    //Display all members
    public String memberNames() {
        StringBuilder sb = new StringBuilder();
        for (User u : members) {
            sb.append(u.getName()).append(", ");
        }
        // Removes trailing commas and spaces if present
        return sb.length() > 1 ? sb.substring(0, sb.length() - 2) : "";
    }

    // Add a new member to a group
    public void addMember(User user) {
        if (!members.contains(user)) {
            members.add(user);
            size++;
            System.out.println(user.getName() + " added to group " + groupName);
        } else {
            System.out.println(user.getName() + " is already in the group " + groupName);
        }
    }

    // Remove a member from an existing group
    public void removeMember(User user) {
        if (members.remove(user)) {
            size--;
            System.out.println(user.getName() + " removed from group " + groupName);
        } else {
            System.out.println(user.getName() + " not found in group " + groupName);
        }
    }



}
