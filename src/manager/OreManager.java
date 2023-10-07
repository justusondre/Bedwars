package manager;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import bedwars.Main;

public class OreManager {
	
	private final FileConfiguration config;
    private final Map<OreType, BukkitTask> oreSpawnTasks;

    public enum OreType {
        IRON, GOLD, DIAMOND, EMERALD
    }

    public OreManager(FileConfiguration config) {
        this.config = config;
        this.oreSpawnTasks = new HashMap<>();
    }

    public Location getOreSpawn(String arenaName, OreType oreType, int number) {
        String spawnLocationString = config.getString("instance." + arenaName + "." + oreType.toString().toLowerCase() + "SpawnPoints." + number);
        return stringToLocation(spawnLocationString);
    }

    public int getMaxOreSpawnPoints(String arenaName, OreType oreType) {
        return config.getConfigurationSection("instance." + arenaName + "." + oreType.toString().toLowerCase() + "SpawnPoints").getKeys(false).size();
    }

    public void spawnOreAtLocations(String arenaName, OreType oreType, int timeInterval) {
        removeOreSpawnTask(oreType);

        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                int maxSpawnPoints = getMaxOreSpawnPoints(arenaName, oreType);
                if (maxSpawnPoints > 0) {
                    int randomSpawnPoint = (int) (Math.random() * maxSpawnPoints) + 1;
                    Location spawnLocation = getOreSpawn(arenaName, oreType, randomSpawnPoint);
                    if (spawnLocation != null) {
                        // Customize this part to spawn the actual ore block/item at spawnLocation
                        spawnOre(spawnLocation, oreType);
                    }
                }
            }
        }.runTaskTimer(Main.getInstance(), 0, timeInterval * 20L);

        oreSpawnTasks.put(oreType, task);
    }

    // Placeholder method to spawn the ore (customize this according to your plugin's logic)
    private void spawnOre(Location spawnLocation, OreType oreType) {
        ItemStack itemStack = null;

        // Determine the item to spawn based on the ore type
        switch (oreType) {
            case IRON:
                itemStack = new ItemStack(Material.IRON_INGOT);
                break;
            case GOLD:
                itemStack = new ItemStack(Material.GOLD_INGOT);
                break;
            case DIAMOND:
                itemStack = new ItemStack(Material.DIAMOND);
                break;
            case EMERALD:
                itemStack = new ItemStack(Material.EMERALD);
                break;
            // Add more cases for other ore types as needed
        }

        if (itemStack != null) {
            // Spawn the item at the location
            spawnLocation.getWorld().dropItemNaturally(spawnLocation, itemStack);
        }
    }

    public void removeOreSpawnTask(OreType oreType) {
        BukkitTask task = oreSpawnTasks.get(oreType);
        if (task != null) {
            task.cancel();
            oreSpawnTasks.remove(oreType);
        }
    }

    private Location stringToLocation(String locationString) {
        if (locationString == null || locationString.isEmpty()) {
            return null;
        }
        
        String[] parts = locationString.split(",");
        if (parts.length >= 6) {
            World world = Bukkit.getWorld(parts[0]);
            double x = Double.parseDouble(parts[1]);
            double y = Double.parseDouble(parts[2]);
            double z = Double.parseDouble(parts[3]);
            float yaw = Float.parseFloat(parts[4]);
            float pitch = Float.parseFloat(parts[5]);
            return new Location(world, x, y, z, yaw, pitch);
        } else {
            return null;
        }
    }
}