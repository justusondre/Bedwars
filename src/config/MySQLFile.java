package config;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import bedwars.Main;

public class MySQLFile {

	private final File file;
    private FileConfiguration config;

    public MySQLFile(JavaPlugin plugin) {
        this.file = new File("plugins/" + Main.getInstance().getName(), "sql.yml");
        this.config = YamlConfiguration.loadConfiguration(file);

        // Only set default values if the file doesn't exist yet
        if (!file.exists()) {
            setDefaultValues();
            saveConfig();
        }
    }

    private void setDefaultValues() {
        config.set("database.host", "host");
        config.set("database.port", 3306);
        config.set("database.name", "database");
        config.set("database.username", "username");
        config.set("database.password", "password");
    }

    public String getHost() {
        return config.getString("database.host");
    }

    public int getPort() {
        return config.getInt("database.port");
    }

    public String getDatabaseName() {
        return config.getString("database.name");
    }

    public String getUsername() {
        return config.getString("database.username");
    }

    public String getPassword() {
        return config.getString("database.password");
    }

    // Helper method to save the configuration to "sql.yml"
    public void saveConfig() {
        try {
            config.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}