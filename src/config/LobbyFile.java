package config;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import bedwars.Main;

@SuppressWarnings("serial")
public class LobbyFile extends File {
	
	public File file = new File("plugins/" + Main.getInstance().getName(), "lobby.yml");
	public FileConfiguration config = YamlConfiguration.loadConfiguration(file);
	
	public LobbyFile() {
		super("", "lobby");
		writeDefaults();
	}

	public void writeDefaults() {
		config.options().copyDefaults(true);
		saveConfig();

	}
	
	public void setLocation(Location loc) {
	    String locationString = String.format(
	        "%s, %.3f, %.3f, %.3f, %.3f, %.3f",
	        loc.getWorld().getName(),
	        loc.getX(),
	        loc.getY(),
	        loc.getZ(),
	        loc.getYaw(),
	        loc.getPitch()
	    );
	    config.set("lobbyLocation", locationString);
	    saveConfig();
	}
	
	public String getWorldName() {
	    try {
	        String lobbyLocationString = config.getString("mainLobbyLocation");
	        String[] parts = lobbyLocationString.split(", ");
	        
	        if (parts.length >= 1) {
	            return parts[0];
	        }
	    } catch (Exception ex) {
	        // Handle the exception if needed
	    }
	    
	    return ""; // Return an empty string if not found or an error occurred
	}
	
	public Location getLocation() {
	    Location loc;

	    try {
	        String lobbyLocationString = config.getString("mainLobbyLocation");
	        String[] parts = lobbyLocationString.split(", ");

	        if (parts.length >= 6) {
	            World w = Bukkit.getWorld(parts[0]);
	            double x = Double.parseDouble(parts[1]);
	            double y = Double.parseDouble(parts[2]);
	            double z = Double.parseDouble(parts[3]);
	            float yaw = Float.parseFloat(parts[4]);
	            float pitch = Float.parseFloat(parts[5]);

	            loc = new Location(w, x, y, z, yaw, pitch);
	        } else {
	            loc = new Location(Bukkit.getWorlds().get(0), 0, 0, 0);
	        }
	    } catch (Exception ex) {
	        loc = new Location(Bukkit.getWorlds().get(0), 0, 0, 0);
	    }

	    return loc;
	}
	
	public World createLobbyWorld(String worldName) {
	      WorldCreator worldCreator = new WorldCreator(worldName);
	      World newWorld = Bukkit.createWorld(worldCreator);
	      return newWorld;
	  }
	
	public void saveConfig() {
		
		try {
			config.save(file);
		} catch (IOException e) {
			
		}
	}
}