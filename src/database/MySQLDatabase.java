package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class MySQLDatabase {

	private final MySQLConnection mysqlConnection;

    public MySQLDatabase(MySQLConnection mysqlConnection) {
        this.mysqlConnection = mysqlConnection;
        createPlayerDataTable();
    }
    
    public void setUserData(UUID uuid, String playerName, int innocentWins, int murdererWins, int deaths, int kills) {
        String sql = "INSERT INTO player_data (uuid, player_name, innocent_wins, murderer_wins, deaths, kills) " +
                     "VALUES (?, ?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE player_name = VALUES(player_name), " +
                     "innocent_wins = VALUES(innocent_wins), murderer_wins = VALUES(murderer_wins), " +
                     "deaths = VALUES(deaths), kills = VALUES(kills)";

        try (Connection connection = mysqlConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, uuid.toString());
            statement.setString(2, playerName);
            statement.setInt(3, innocentWins);
            statement.setInt(4, murdererWins);
            statement.setInt(5, deaths);
            statement.setInt(6, kills);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void createPlayerDataTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS player_data ("
                + "uuid VARCHAR(36) PRIMARY KEY,"
                + "player_name VARCHAR(255),"
                + "innocent_wins INT,"
                + "murderer_wins INT,"
                + "deaths INT,"
                + "kills INT"
                + ")";

        try (Connection connection = mysqlConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(createTableSQL)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            // Handle the exception and log the error message
            e.printStackTrace();
            // You can also add more detailed logging here
            // For example: getLogger().severe("Failed to create player_data table: " + e.getMessage());
        }
    }

    public void addKills(UUID uuid, int killsToAdd) {
        try (Connection connection = mysqlConnection.getConnection()) {
            int currentKills = getKills(uuid);
            int newKills = currentKills + killsToAdd;
            setKills(uuid, newKills);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addInnocentWins(UUID uuid, int winsToAdd) {
        try (Connection connection = mysqlConnection.getConnection()) {
            int currentWins = getInnocentWins(uuid);
            int newWins = currentWins + winsToAdd;
            setInnocentWins(uuid, newWins);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addMurdererWins(UUID uuid, int winsToAdd) {
        try (Connection connection = mysqlConnection.getConnection()) {
            int currentWins = getMurdererWins(uuid);
            int newWins = currentWins + winsToAdd;
            setMurdererWins(uuid, newWins);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addDeaths(UUID uuid, int deathsToAdd) {
        try (Connection connection = mysqlConnection.getConnection()) {
            int currentDeaths = getDeaths(uuid);
            int newDeaths = currentDeaths + deathsToAdd;
            setDeaths(uuid, newDeaths);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getKills(UUID uuid) {
        try (Connection connection = mysqlConnection.getConnection()) {
            String sql = "SELECT kills FROM player_data WHERE uuid = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, uuid.toString());
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt("kills");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getInnocentWins(UUID uuid) {
        try (Connection connection = mysqlConnection.getConnection()) {
            String sql = "SELECT innocent_wins FROM player_data WHERE uuid = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, uuid.toString());
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt("innocent_wins");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getMurdererWins(UUID uuid) {
        try (Connection connection = mysqlConnection.getConnection()) {
            String sql = "SELECT murderer_wins FROM player_data WHERE uuid = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, uuid.toString());
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt("murderer_wins");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getDeaths(UUID uuid) {
        try (Connection connection = mysqlConnection.getConnection()) {
            String sql = "SELECT deaths FROM player_data WHERE uuid = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, uuid.toString());
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt("deaths");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void setKills(UUID uuid, int kills) {
        try (Connection connection = mysqlConnection.getConnection()) {
            String sql = "UPDATE player_data SET kills = ? WHERE uuid = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, kills);
                statement.setString(2, uuid.toString());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setInnocentWins(UUID uuid, int wins) {
        try (Connection connection = mysqlConnection.getConnection()) {
            String sql = "UPDATE player_data SET innocent_wins = ? WHERE uuid = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, wins);
                statement.setString(2, uuid.toString());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setMurdererWins(UUID uuid, int wins) {
        try (Connection connection = mysqlConnection.getConnection()) {
            String sql = "UPDATE player_data SET murderer_wins = ? WHERE uuid = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, wins);
                statement.setString(2, uuid.toString());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setDeaths(UUID uuid, int deaths) {
        try (Connection connection = mysqlConnection.getConnection()) {
            String sql = "UPDATE player_data SET deaths = ? WHERE uuid = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, deaths);
                statement.setString(2, uuid.toString());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public UUID getPlayerUUID(String playerName) {
        String sql = "SELECT uuid FROM player_data WHERE player_name = ?";
        try (Connection connection = mysqlConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, playerName);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return UUID.fromString(resultSet.getString("uuid"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; // Player UUID not found
    }

    public UserData getPlayerData(UUID uuid) {
        String sql = "SELECT * FROM player_data WHERE uuid = ?";

        try (Connection connection = mysqlConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, uuid.toString());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String playerName = resultSet.getString("player_name");
                    int innocentWins = resultSet.getInt("innocent_wins");
                    int murdererWins = resultSet.getInt("murderer_wins");
                    int deaths = resultSet.getInt("deaths");
                    int kills = resultSet.getInt("kills");
                    return new UserData(playerName, innocentWins, murdererWins, deaths, kills);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; // Player data not found
    }
}