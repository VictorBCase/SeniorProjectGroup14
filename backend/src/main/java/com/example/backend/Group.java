package com.example.backend;
import java.util.List;
import java.util.UUID;

public class Group {
    private String groupId;
    private String groupName;
    private List<User> members;

    public Group(String groupName, List<User> members) {
        this.groupId = UUID.randomUUID().toString();
        this.groupName = groupName;
        this.members = members;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public List<User> getMembers() {
        return members;
    }

    public String memberNames() {
        StringBuilder sb = new StringBuilder();
        for (User u : members) {
            sb.append(u.getName()).append(", ");
        }
        // Removes trailing commas and spaces if present
        return sb.length() > 1 ? sb.substring(0, sb.length() - 2) : "";
    }
}
