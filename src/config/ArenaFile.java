package config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import bedwars.Main;
import bedwars.Mode;
import object.TeamColor;

@SuppressWarnings("serial")
public class ArenaFile extends File {
	
  public File file = new File("plugins/" + Main.getInstance().getName(), "arena.yml");
  
  public FileConfiguration config = (FileConfiguration)YamlConfiguration.loadConfiguration(this.file);
  
  public ArenaFile() {
    super("", "arena");
    writeDefaults();
  }
  
  public void writeDefaults() {
    this.config.options().copyDefaults(true);
    saveConfig();
  }
  
  public void createArena(String arenaId) {
	    String arenaPath = "instance." + arenaId.toLowerCase();
	    config.set(arenaPath + ".mapName", "default");
	    config.set(arenaPath + ".mode", "SOLO");
	    config.set(arenaPath + ".minimumPlayers", 4);
	    config.set(arenaPath + ".maximumPlayers", 8);
	    setDefaultLocation(config, arenaPath + ".lobbyLocation");
	    setDefaultLocation(config, arenaPath + ".endLocation");
	    config.set(arenaPath + ".teams", new ArrayList<>());
	    //config.set(arenaPath + ".ironSpawnPoints", new ArrayList<>());
	    //config.set(arenaPath + ".goldSpawnPoints", new ArrayList<>());
	    config.set(arenaPath + ".diamondSpawnPoints", new ArrayList<>());
	    config.set(arenaPath + ".emeraldSpawnPoints", new ArrayList<>());

	    
	    saveConfig();
	}

	private void setDefaultLocation(FileConfiguration config, String path) {
	    String locationString = String.format("world, %.3f, %.3f, %.3f, %.3f, %.3f", 0.0, 0.0, 0.0, 0.0, 0.0);
	    config.set(path, locationString);
	}
  
	public Location getArena(String arenaName) {
	    Location loc = null;
	    try {
	        String lobbyLocationString = config.getString("instance." + arenaName + ".lobbyLocation");
	        String[] parts = lobbyLocationString.split(", ");

	        if (parts.length >= 6) {
	            World w = Bukkit.getWorld(parts[0]);
	            double x = Double.parseDouble(parts[1]);
	            double y = Double.parseDouble(parts[2]);
	            double z = Double.parseDouble(parts[3]);
	            float yaw = Float.parseFloat(parts[4]);
	            float pitch = Float.parseFloat(parts[5]);

	            loc = new Location(w, x, y, z, yaw, pitch);
	        }
	    } catch (Exception ex) {
	        loc = null;
	    }
	    return loc;
	}
  
  public void setArenaLobby(String arenaName, Location loc) {
	    String locationString = String.format(
	        "%s, %.3f, %.3f, %.3f, %.3f, %.3f",
	        loc.getWorld().getName(),
	        loc.getX(),
	        loc.getY(),
	        loc.getZ(),
	        loc.getYaw(),
	        loc.getPitch()
	    );

	    config.set("instance." + arenaName + ".lobbyLocation", locationString);
	    saveConfig();
	}

  public Location getArenaLobby(String arenaName) {
      Location loc;
      try {
          String lobbyLocationString = config.getString("instance." + arenaName + ".lobbyLocation");
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
  
	public void setIronSpawn(String arenaName, int number, Location loc) {
	    String locationString = String.format(
	        "%s, %.3f, %.3f, %.3f, %.3f, %.3f",
	        loc.getWorld().getName(),
	        loc.getX(),
	        loc.getY(),
	        loc.getZ(),
	        loc.getYaw(),
	        loc.getPitch()
	    );

	    config.set("instance." + arenaName + ".ironSpawnPoints." + number, locationString);
	    saveConfig();
	}
	
	public void setGoldSpawn(String arenaName, int number, Location loc) {
	    String locationString = String.format(
	        "%s, %.3f, %.3f, %.3f, %.3f, %.3f",
	        loc.getWorld().getName(),
	        loc.getX(),
	        loc.getY(),
	        loc.getZ(),
	        loc.getYaw(),
	        loc.getPitch()
	    );

	    config.set("instance." + arenaName + ".goldSpawnPoints." + number, locationString);
	    saveConfig();
	}
	
	public void setDiamondSpawn(String arenaName, int number, Location loc) {
	    String locationString = String.format(
	        "%s, %.3f, %.3f, %.3f, %.3f, %.3f",
	        loc.getWorld().getName(),
	        loc.getX(),
	        loc.getY(),
	        loc.getZ(),
	        loc.getYaw(),
	        loc.getPitch()
	    );

	    config.set("instance." + arenaName + ".diamondSpawnPoints." + number, locationString);
	    saveConfig();
	}
	
	public void setEmeraldSpawn(String arenaName, int number, Location loc) {
	    String locationString = String.format(
	        "%s, %.3f, %.3f, %.3f, %.3f, %.3f",
	        loc.getWorld().getName(),
	        loc.getX(),
	        loc.getY(),
	        loc.getZ(),
	        loc.getYaw(),
	        loc.getPitch()
	    );

	    config.set("instance." + arenaName + ".emeraldSpawnPoints." + number, locationString);
	    saveConfig();
	}

	public Location getIronSpawn(String arenaName, int number) {
	    Location loc;
	    try {
	        String spawnLocationString = config.getString("instance." + arenaName + ".ironSpawnPoints." + number);
	        String[] parts = spawnLocationString.split(", ");

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

	public Location getGoldSpawn(String arenaName, int number) {
	    Location loc;
	    try {
	        String spawnLocationString = config.getString("instance." + arenaName + ".goldSpawnPoints." + number);
	        String[] parts = spawnLocationString.split(", ");

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
	
	public Location getDiamondSpawn(String arenaName, int number) {
	    Location loc;
	    try {
	        String spawnLocationString = config.getString("instance." + arenaName + ".diamondSpawnPoints." + number);
	        String[] parts = spawnLocationString.split(", ");

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
	
	public Location getEmeraldSpawn(String arenaName, int number) {
	    Location loc;
	    try {
	        String spawnLocationString = config.getString("instance." + arenaName + ".emeraldSpawnPoints." + number);
	        String[] parts = spawnLocationString.split(", ");

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
	
	public List<Location> getAllDiamondSpawns(String arenaName) {
        List<Location> diamondSpawns = new ArrayList<>();

        for (int number = 1; config.contains("instance." + arenaName + ".diamondSpawnPoints." + number); number++) {
            Location loc = getDiamondSpawn(arenaName, number);
            diamondSpawns.add(loc);
        }

        return diamondSpawns;
    }

    // Method to get all emerald spawn locations for a given arena
    public List<Location> getAllEmeraldSpawns(String arenaName) {
        List<Location> emeraldSpawns = new ArrayList<>();

        for (int number = 1; config.contains("instance." + arenaName + ".emeraldSpawnPoints." + number); number++) {
            Location loc = getEmeraldSpawn(arenaName, number);
            emeraldSpawns.add(loc);
        }

        return emeraldSpawns;
    }
	
	public void setMode(String arenaName, Mode selectedMode) {
	    config.set("instance." + arenaName + ".mode", selectedMode.name()); // Save the mode as an enum constant
	    saveConfig();
	}

	public Mode getMode(String arenaName) {
	    String modeName = config.getString("instance." + arenaName + ".mode");

	    if (modeName != null) {
	        // Convert the mode name to uppercase to match the enum value
	        modeName = modeName.toUpperCase();

	        try {
	            // Attempt to parse the mode name as an enum value
	            return Mode.valueOf(modeName);
	        } catch (IllegalArgumentException e) {
	            // Handle the case where the mode name is invalid
	            return null;
	        }
	    }

	    return null; // Return null if the mode is not found or is invalid
	}
	
	public void setBedLocation(String arenaName, TeamColor teamColor, Location location) {
	    String path = "instance." + arenaName + ".teams." + teamColor.name().toLowerCase() + ".bedLocation";
	    String locationString = locationToString(location);
	    config.set(path, locationString);
	    saveConfig();
	}
	
	public Location getBedLocation(String arenaName, TeamColor teamColor) {
	    String path = "instance." + arenaName + ".teams." + teamColor.name().toLowerCase() + ".bedLocation";
	    String locationString = config.getString(path);

	    if (locationString != null) {
	        return stringToLocation(locationString);
	    }

	    return null;
	}
	
	public List<TeamColor> getTeamColorsInArena(String arenaName) {
	    List<TeamColor> teamColors = new ArrayList<>();

	    for (TeamColor teamColor : TeamColor.values()) {
	        if (isTeamListedInArena(arenaName, teamColor.name())) {
	            teamColors.add(teamColor);
	        }
	    }

	    return teamColors;
	}
	
	public void setTeamSpawn(String arenaName, TeamColor teamColor, Location location) {
	    String path = "instance." + arenaName + ".teams." + teamColor.name().toLowerCase(); // Convert ChatColor to lowercase string
	    String locationString = locationToString(location);
	    config.set(path + ".teamSpawnPoint", locationString);
	    saveConfig();
	}
	
	public Location getTeamSpawn(String arenaName, TeamColor teamColor) {
	    String path = "instance." + arenaName + ".teams." + teamColor.getDisplayName().toLowerCase() + ".teamSpawnPoint"; // Use teamColor.name() directly
	    String spawnLocationString = config.getString(path);

	    return stringToLocation(spawnLocationString);
	}
	
	public void setBaseGenerator(String arenaName, TeamColor teamColor, Location location) {
	    String path = "instance." + arenaName + ".teams." + teamColor.name().toLowerCase(); // Convert ChatColor to lowercase string
	    String locationString = locationToString(location);
	    config.set(path + ".baseGeneratorLocation", locationString);
	    saveConfig();
	}
	
	public Location getBaseGenerator(String arenaName, TeamColor teamColor) {
	    String path = "instance." + arenaName + ".teams." + teamColor.getDisplayName().toLowerCase() + ".baseGeneratorLocation"; // Use teamColor.name() directly
	    String spawnLocationString = config.getString(path);

	    return stringToLocation(spawnLocationString);
	}

	private String locationToString(Location location) {
	    return String.format(
	        "%s, %.3f, %.3f, %.3f, %.3f, %.3f",
	        location.getWorld().getName(),
	        location.getX(),
	        location.getY(),
	        location.getZ(),
	        location.getYaw(),
	        location.getPitch()
	    );
	}

	private Location stringToLocation(String locationString) {
	    try {
	        String[] parts = locationString.split(", ");

	        if (parts.length >= 6) {
	            World w = Bukkit.getWorld(parts[0]);
	            double x = Double.parseDouble(parts[1]);
	            double y = Double.parseDouble(parts[2]);
	            double z = Double.parseDouble(parts[3]);
	            float yaw = Float.parseFloat(parts[4]);
	            float pitch = Float.parseFloat(parts[5]);

	            return new Location(w, x, y, z, yaw, pitch);
	        }
	    } catch (Exception ex) {
	        // Handle any exceptions or invalid location strings here
	    }
	    return new Location(Bukkit.getWorlds().get(0), 0, 0, 0);
	}
    
    public void addTeam(String arenaName, String teamColor) {
        String teamPath = "instance." + arenaName.toLowerCase() + ".teams";
        ConfigurationSection teamSection = config.getConfigurationSection(teamPath);

        if (teamSection == null) {
            teamSection = config.createSection(teamPath);
        }

        teamSection.createSection(teamColor.toLowerCase());
        saveConfig();
    }
    
    public boolean isTeamListedInArena(String arenaName, String teamColor) {
        String teamPath = "instance." + arenaName.toLowerCase() + ".teams." + teamColor.toLowerCase();
        return config.contains(teamPath);
    }
    
    public TeamColor getBedTeamColorAtLocation(String arenaName, Location location) {
        int radius = 5; // You can adjust the radius as needed

        for (TeamColor teamColor : TeamColor.values()) {
            Location bedLocation = getBedLocation(arenaName, teamColor);

            if (bedLocation != null && isBedNearLocation(bedLocation, location, radius)) {
                return teamColor;
            }
        }

        return null; // No matching team color found at the location
    }

    private boolean isBedNearLocation(Location bedLocation, Location targetLocation, int radius) {
        if (!bedLocation.getWorld().equals(targetLocation.getWorld())) {
            return false; // Beds are not in the same world
        }

        int x1 = bedLocation.getBlockX();
        int y1 = bedLocation.getBlockY();
        int z1 = bedLocation.getBlockZ();
        
        int x2 = targetLocation.getBlockX();
        int y2 = targetLocation.getBlockY();
        int z2 = targetLocation.getBlockZ();

        return Math.abs(x1 - x2) <= radius && Math.abs(y1 - y2) <= radius && Math.abs(z1 - z2) <= radius;
    }
  
	public void createArenasFromConfig() {
	    for (String key : config.getConfigurationSection("instance.").getKeys(false)) {
	        String worldName = config.getString("instance." + key + ".lobbyLocation").split(", ")[0];
	        if (worldName != null) {
	            createNewWorld(worldName);
	        } else {
	            Bukkit.getLogger().warning("No world name found for arena: " + key);
	        }
	    }
	}

	public World createNewWorld(String worldName) {
	    WorldCreator worldCreator = new WorldCreator(worldName);
	    World newWorld = Bukkit.createWorld(worldCreator);
	    return newWorld;
	}

	public void saveConfig() {
	    try {
	        config.save(file);
	    	} catch (IOException ignored) {
	    }
	}

	public void getArenas() {
	    for (String key : config.getConfigurationSection("instance.").getKeys(false)) {
	        Bukkit.broadcastMessage(key);
	    }
	}

	public List<String> getArenasList() {
	    return new ArrayList<>(config.getConfigurationSection("instance.").getKeys(false));
	}
}