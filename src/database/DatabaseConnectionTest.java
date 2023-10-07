package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnectionTest {
	
    public static void testDatabaseConnection() {
        String jdbcUrl = "jdbc:mysql://localhost:3306/murder_mystery";
        String username = "root";
        String password = "ORP0095425o777";

        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
            System.out.println("Database connection successful!");
        } catch (SQLException e) {
            System.err.println("Database connection failed:");
            e.printStackTrace();
        }
    }
}