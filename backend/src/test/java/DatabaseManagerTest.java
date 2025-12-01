import com.example.backend.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;

import java.sql.*;
public class DatabaseManagerTest {
    private  DatabaseManager db;

    @BeforeEach
    void resetDatabase() throws Exception {
        db = new DatabaseManager(
                "jdbc:postgresql://localhost:5432/vqueue_test",
                "postgres",
                "vqueue"
        );

        Connection connection = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/vqueue_test",
                "postgres",
                "vqueue"
        );

        Statement s = connection.createStatement();

        // Clean all tables before each test
        s.execute("TRUNCATE queue_entries, group_members, groups, rides, users RESTART IDENTITY CASCADE;");

        s.close();
        connection.close();
    }

    //user test
    @Test
    void testAddUser() throws Exception {
        db.addUser("U1", "Julio", "123");

        try (ResultSet rs = db.getAllUsers()) {
            assertTrue(rs.next());
            assertEquals("U1", rs.getString("user_id"));
            assertEquals("Julio", rs.getString("username"));
            assertEquals("123", rs.getString("password_hash"));
        }
    }

    @Test
    void testValidateLoginSuccess() {
        db.addUser("U1", "Julio", "pwd123");
        assertTrue(db.validateLogin("Julio", "pwd123"));
    }

    @Test
    void testValidateLoginFail() {
        db.addUser("U1", "Julio", "pwd123");
        assertFalse(db.validateLogin("Julio", "wrongpass"));
    }

    @Test
    void testRemoveUser() throws Exception {
        db.addUser("U1", "Julio", "123");
        db.removeUser("Julio", "123");

        try (ResultSet rs = db.getAllUsers()) {
            assertFalse(rs.next());
        }
    }

    //group test
    @Test
    void testAddGroup() throws Exception {
        db.addUser("U1", "Julio", "1");
        db.addGroup("G1", "GroupOne", "U1");

        Connection conn = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/vqueue_test",
                "postgres",
                "vqueue"
        );

        PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM groups WHERE group_id = 'G1'"
        );
        ResultSet rs = stmt.executeQuery();

        assertTrue(rs.next());
        assertEquals("GroupOne", rs.getString("group_name"));
        assertEquals("U1", rs.getString("owner_id"));

        rs.close();
        stmt.close();
        conn.close();
    }

    @Test
    void testAddMemberToGroup() {
        db.addUser("U1", "Julio", "1");
        db.addUser("U2", "Jonah", "1");

        db.addGroup("G1", "GroupOne", "U1");

        db.addMemberToGroup("G1", "U1");
        db.addMemberToGroup("G1", "U2");

        assertTrue(db.isUserInGroup("G1", "U1"));
        assertTrue(db.isUserInGroup("G1", "U2"));
    }

    @Test
    void testRemoveMemberFromGroup() {
        db.addUser("U1", "Julio", "1");
        db.addGroup("G1", "GroupOne", "U1");

        db.addMemberToGroup("G1", "U1");
        assertTrue(db.isUserInGroup("G1", "U1"));

        db.removeMemberFromGroup("G1", "U1");
        assertFalse(db.isUserInGroup("G1", "U1"));
    }

    //ride test
    @Test
    void testAddRide() throws Exception {
        db.addRide("R1", "Maverick", 20, 2, "code", "/img");

        ResultSet rs = db.getAllRide();
        assertTrue(rs.next());
        assertEquals("R1", rs.getString("ride_id"));
        assertEquals("Maverick", rs.getString("ride_name"));
        assertEquals(20, rs.getInt("hourly_capacity"));
        assertEquals(2, rs.getInt("load_time"));
        rs.close();
    }

    @Test
    void testUpdateWaitTime() {
        db.addRide("R1", "Maverick", 20, 2, "qr", "/img");

        db.updateWaitTime("R1", 40.0);

        assertEquals(40.0, db.getWaitTime("R1"));
    }

    //queue test
    @Test
    void testJoinQueue() throws Exception {
        db.addRide("R1", "Maverick", 20, 2, "qr", "/img");
        db.addUser("U1", "Julio", "1");

        db.joinQueue("R1", "U1", "user", 1);
        assertEquals(1, db.getUserQueuePosition("R1", "U1"));
    }

    @Test
    void testLeaveQueue() {
        db.addRide("R1", "Ride", 20, 2, "qr", "/img");
        db.addUser("U1", "Julio", "1");

        db.joinQueue("R1", "U1", "user", 1);
        assertEquals(1, db.getUserQueuePosition("R1", "U1"));

        db.leaveQueue("R1", "U1");
        assertEquals(-1, db.getUserQueuePosition("R1", "U1"));
    }

}

