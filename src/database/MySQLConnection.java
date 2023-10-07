package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.bukkit.plugin.Plugin;

import config.MySQLFile;

public class MySQLConnection {
	
	private final MySQLFile databaseConfig;
    private final Plugin plugin;
    private Connection connection;

    public MySQLConnection(Plugin plugin, MySQLFile databaseConfig) {
        this.plugin = plugin;
        this.databaseConfig = databaseConfig;
    }

    public synchronized Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(getConnectionUrl(), getUsername(), getPassword());
                plugin.getLogger().info("Database connection established.");
            } catch (SQLException e) {
                e.printStackTrace();
                plugin.getLogger().severe("Failed to establish a database connection.");
            }
        } else {
            try {
                // Check if the existing connection is still valid
                if (connection.isClosed() || !connection.isValid(3)) {
                    connection.close();
                    connection = DriverManager.getConnection(getConnectionUrl(), getUsername(), getPassword());
                    plugin.getLogger().info("Re-established a new database connection.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                plugin.getLogger().severe("Failed to re-establish a new database connection.");
            }
        }
        return connection;
    }

    public synchronized void close() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                    plugin.getLogger().info("Database connection closed.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                plugin.getLogger().severe("Failed to close the database connection.");
            } finally {
                connection = null; // Reset the connection to allow reconnection if needed
            }
        }
    }

    private String getConnectionUrl() {
        return "jdbc:mysql://" + databaseConfig.getHost() + ":" + databaseConfig.getPort() + "/" + databaseConfig.getDatabaseName() + "?autoReconnect=true&useUnicode=yes";
    }

    private String getUsername() {
        return databaseConfig.getUsername();
    }

    private String getPassword() {
        return databaseConfig.getPassword();
    }
}