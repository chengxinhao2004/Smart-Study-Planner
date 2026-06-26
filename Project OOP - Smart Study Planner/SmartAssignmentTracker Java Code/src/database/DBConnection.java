package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;

public class DBConnection {
    // XAMPP Default Credentials
    private static final String URL = "jdbc:mysql://localhost:3307/assignment_tracker_db";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // XAMPP root password is blank by default

    // Static method to grab the connection from anywhere in  project
    public static Connection getConnection() {
        Connection connection = null;
        try {
            // Load the MySQL Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Establish the bridge
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println(">>> Database Connection Successful!");
            
        } catch (ClassNotFoundException e) {
            System.out.println("Error: MySQL JDBC Driver not found. Did you add the JAR to your Build Path?");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Error: Could not connect to XAMPP. Is MySQL running in the XAMPP Control Panel?");
            e.printStackTrace();
        }
        return connection;
    }
    
    public static void logAction(int userId, String action) {
        String sql = "INSERT INTO audit_logs (user_id, action) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, action);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace(); // Fails silently so it doesn't crash UI
        }
    }
}
