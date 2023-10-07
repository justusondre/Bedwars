package generators;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import bedwars.Main;
import handler.Game;

public class Generator {
	private final Location spawnLocation;
    private final GeneratorType generatorType;
    private BukkitTask spawnTask;
    private Game arena;

    public Generator(Location spawnLocation, GeneratorType generatorType) {
        this.spawnLocation = spawnLocation;
        this.generatorType = generatorType;
        this.spawnTask = null; // Initially, no spawn task is scheduled
        this.arena = null;
    }

    public void startSpawning(Game arena) {
        this.arena = arena;
        startSpawning();
    }

    public void startSpawning() {
        if (spawnTask == null) {
            int spawnInterval = generatorType.getSpawnInterval();

            spawnTask = new BukkitRunnable() {
                @Override
                public void run() {
                	
                }
            }.runTaskTimer(Main.getInstance(), 0, spawnInterval * 20L);
        }
    }

    public void stopSpawning() {
        if (spawnTask != null) {
            spawnTask.cancel();
            spawnTask = null;
        }
   

        // Customize this part to spawn the actual ore block/item at spawnLocation
        Material oreMaterial = Material.IRON_INGOT; // Default to iron ingot

        // Determine the ore material based on the generator type
        if (generatorType.getName().equalsIgnoreCase("gold")) {
            oreMaterial = Material.GOLD_INGOT;
        } else if (generatorType.getName().equalsIgnoreCase("diamond")) {
            oreMaterial = Material.DIAMOND;
        } else if (generatorType.getName().equalsIgnoreCase("emerald")) {
            oreMaterial = Material.EMERALD;
        }

        // Create and drop the ore item
        ItemStack oreItemStack = new ItemStack(oreMaterial);
        spawnLocation.getWorld().dropItemNaturally(spawnLocation, oreItemStack);
    }
}