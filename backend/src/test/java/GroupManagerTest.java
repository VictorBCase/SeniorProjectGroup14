import com.example.backend.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.*;
public class GroupManagerTest {
    private DatabaseManager db;

    @BeforeEach
    void resetDatabase() throws Exception {

        db = new DatabaseManager(
                "jdbc:postgresql://vqueue-db.c9ukqu4ie5ns.us-east-2.rds.amazonaws.com:5432/vqueue",
                "vqueueadmin",
                "virtualqueue"
        );

        Connection connection = DriverManager.getConnection(
                "jdbc:postgresql://vqueue-db.c9ukqu4ie5ns.us-east-2.rds.amazonaws.com:5432/vqueue",
                "vqueueadmin",
                "virtualqueue"
        );

        Statement s = connection.createStatement();

        // Clean all tables
        s.execute("TRUNCATE queue_entries, group_members, groups, rides, users RESTART IDENTITY CASCADE;");

        s.close();
        connection.close();
    }

    @Test
    void testAddGroup() {
        UserManager um = new UserManager(db);
        GroupManager gm = new GroupManager(db, um);
        um.createUser("Julio", "1");
        User owner = um.login("Julio", "1");
        Group g = new Group("TestGroup", owner);

        gm.addGroup(g);

        Group fetched = gm.getGroup(g.getGroupId());
        assertNotNull(fetched);
        assertEquals("TestGroup", fetched.getGroupName());
    }

    @Test
    void testGetGroupNotFound() {
        UserManager um = new UserManager(db);
        GroupManager gm = new GroupManager(db, um);

        assertNull(gm.getGroup("NOPE"));
    }

    @Test
    void testLoadGroupsFromDatabase() {
        UserManager um = new UserManager(db);

        // Create owner
        um.createUser("OwnerUser", "1");
        User owner = um.login("OwnerUser", "1");

        db.addUser(owner.getId(), owner.getUsername(), owner.getPasswordHash());
        // Insert group directly into DB
        db.addGroup("G1", "RollerGang", owner.getId());

        // Reload GroupManager from DB
        GroupManager gm = new GroupManager(db, um);

        Group g = gm.getGroup("G1");

        assertNotNull(g);
        assertEquals("RollerGang", g.getGroupName());
        assertEquals(owner.getUsername(), g.getOwner().getUsername());
    }

    @Test
    void testLoadGroupMembersFromDatabase() {
        UserManager um = new UserManager(db);

        // Create users
        um.createUser("Julio", "1");
        um.createUser("Alex", "1");
        User u1 = um.login("Julio", "1");
        User u2 = um.login("Alex", "1");

        // Create group + owner
        um.createUser("Victor", "1");
        User owner = um.login("Victor", "1");


        // Insert into DB
        db.addUser(owner.getId(), owner.getUsername(), owner.getPasswordHash());
        db.addUser(u1.getId(), u1.getUsername(), u1.getPasswordHash());
        db.addUser(u2.getId(), u2.getUsername(), u2.getPasswordHash());
        db.addGroup("G10", "TestGroup", owner.getId());

        // Insert members
        db.addMemberToGroup("G10", u1.getId());
        db.addMemberToGroup("G10", u2.getId());

        // Reload manager
        GroupManager gm = new GroupManager(db, um);

        Group g = gm.getGroup("G10");

        assertNotNull(g);
        assertEquals(3, g.getMembers().size()); // owner + 2 members

        assertTrue(
                g.getMembers().stream().anyMatch(u -> u.getUsername().equals("Julio"))
        );
        assertTrue(
                g.getMembers().stream().anyMatch(u -> u.getUsername().equals("Alex"))
        );
    }
}

