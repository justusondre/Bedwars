package generators;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import bedwars.Main;
import generators.BedwarsGenerator.GeneratorType;
import handler.Game;

public class BedwarsGenerator {
	
	Game game = Main.getInstance().getGame();
	
    private final GeneratorType generatorType;
    private final int tier;
    private final Location location;
    private final int speed; // Speed in ticks
    private BukkitTask generationTask; // Stores the task for resource generation
    private int countdownValue; // To store the current countdown value
    private String arenaName;
    private BukkitTask countdownTask; // Stores the task for countdown updates
    private Game arena; // Reference to the arena

    public BedwarsGenerator(String arenaName, GeneratorType generatorType, int tier, Location location, int speed) {
        this.generatorType = generatorType;
        this.tier = tier;
        this.location = location;
        this.speed = speed;
        this.generationTask = null;
        this.countdownValue = speed; // Initialize countdownValue with the speed
        this.arenaName = arenaName;
    }

    public void startGeneration() {
        if (generationTask != null && !generationTask.isCancelled()) {
            return;
        }

        // Start the countdown task to update countdown in chat
        startCountdownTask();

        generationTask = new BukkitRunnable() {
            @Override
            public void run() {
                generateResources();
                countdownValue--; // Decrease the countdown value

                // Check if countdown is finished
                if (countdownValue == 0) {
                    stopGeneration();
                    // Broadcast a message to players in the arena when generation is complete
                    game.broadcastMessageToPlayersInArena(arenaName, "Resource generation is complete!");
                }
            }
        }.runTaskTimer(Main.getInstance(), 0, 20L); // Use 20L for one-second intervals
    }

    public void stopGeneration() {
        // Cancel the generation task if it's running
        if (generationTask != null && !generationTask.isCancelled()) {
            generationTask.cancel();
            generationTask = null;
        }

        // Cancel the countdown task if it's running
        if (countdownTask != null && !countdownTask.isCancelled()) {
            countdownTask.cancel();
            countdownTask = null;
        }
    }

    public int getCountdownValue() {
        return countdownValue;
    }

    private void generateResources() {
        ItemStack[] resources = getGeneratorResources(generatorType, tier);

        // Generate resources at the generator location
        for (ItemStack resource : resources) {
            location.getWorld().dropItemNaturally(location, resource);
        }
    }

	private ItemStack[] getGeneratorResources(GeneratorType generatorType, int tier) {

		switch (generatorType) {

		case BASE_GENERATOR:
			if (tier == 1 || tier == 2) {
				return new ItemStack[] { new ItemStack(Material.IRON_INGOT), new ItemStack(Material.GOLD_INGOT) };
			} else if (tier == 3 || tier == 4) {
				return new ItemStack[] { new ItemStack(Material.IRON_INGOT), new ItemStack(Material.GOLD_INGOT),
						new ItemStack(Material.EMERALD) };
			}
			break;
		case DIAMOND_GENERATOR:
			if (tier == 1) {
				return new ItemStack[] { new ItemStack(Material.DIAMOND) };
			} else if (tier == 2) {
				return new ItemStack[] { new ItemStack(Material.DIAMOND, 2) };
			} // Add more tiers as needed

			break;
		case EMERALD_GENERATOR:
			if (tier == 1) {
				return new ItemStack[] { new ItemStack(Material.EMERALD) };
			} else if (tier == 2) {
				return new ItemStack[] { new ItemStack(Material.EMERALD, 2) };
			} // Add more tiers as needed

			break;
		}
		return new ItemStack[0]; // Return an empty array if no resources should spawn
	}
	
	private void startCountdownTask() {
        countdownTask = new BukkitRunnable() {
            @Override
            public void run() {
                // Broadcast the countdown value to players in the arena
                game.broadcastMessageToPlayersInArena(arenaName, "Countdown: " + countdownValue);
            }
        }.runTaskTimer(Main.getInstance(), 0, 20L); // Update every second
    }

	public enum GeneratorType {
		BASE_GENERATOR, DIAMOND_GENERATOR, EMERALD_GENERATOR
	}
}