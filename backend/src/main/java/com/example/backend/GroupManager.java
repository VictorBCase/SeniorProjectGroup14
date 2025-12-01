package com.example.backend;
import java.util.*;
import java.sql.*;

/**
 * Manages all groups used by the API will store rides and can validate QR codes
 */
public class GroupManager {

    //stores all groups indexed by groupId
    private final Map<String, Group> groups = new HashMap<>();

    private final DatabaseManager db;

    private final UserManager um;


    /**
     * loads all groups from database
     *
     * @param db the database
     * @param um the usermanager
     */
    public GroupManager(DatabaseManager db, UserManager um) {
        this.db = db;
        this.um = um;
        loadGroupsFromDatabase();
    }

    /**
     * gets the group from specific id
     *
     * @param groupId the unique group id
     * @return the group instance
     */
    public Group getGroup(String groupId) {
        return groups.get(groupId);
    }

    /**
     * adds a group to the list
     *
     * @param group the group instance
     */
    public void addGroup(Group group) {
        groups.put(group.getGroupId(), group);
    }

    /**
     * Will load all groups from database
     */
    private void loadGroupsFromDatabase() {
        try (ResultSet rs = db.getAllGroups()) {

            while (rs.next()) {
                String groupId = rs.getString("group_id");
                String groupName = rs.getString("group_name");
                String ownerId = rs.getString("owner_id");

                User owner = um.getUser(ownerId);

                if (owner == null) {
                    System.err.println("⚠ Warning: Owner " + ownerId + " not found for group " + groupId);
                    continue;
                }


                Group group = new Group(groupName, owner, groupId);

                groups.put(groupId, group);

                loadGroupMembers(groupId);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to load groups: " + e.getMessage());
        }
    }

    /**
     * will load all group members from database
     *
     * @param groupId the groups unique ID
     */
    private void loadGroupMembers(String groupId) {
        Group group = groups.get(groupId);
        try (ResultSet rs = db.getAllGroupMembers(groupId)){
            while (rs.next()) {

                String userId = rs.getString("user_id");
                User user = um.getUser(userId);

                if (user == null) {
                    System.err.println("⚠ Warning: User " + userId + " not found in UserManager. Skipping.");
                    continue;
                }

                // Add member in memory (no DB writes)
                group.addMember(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load group members: " + e.getMessage());
        }
    }
}
