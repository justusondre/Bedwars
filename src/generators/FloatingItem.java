package generators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.Lists;

import bedwars.Main;

public class FloatingItem {
	
    private Map<UUID, Integer> floatingTasks = new HashMap<>();
	
	private static List<FloatingItem> items = new ArrayList<>();
    private Location location, sameLocation;
    private ArmorStand armorStand;
    private boolean floatLoop;
    private List<ArmorStand> texts = new ArrayList<>();

    /**
     * Constructs a new floating item and adds it to the items list
     *
     * @param plugin The plugin
     * @param location The location to spawn item at
     */
    public FloatingItem(Location location) {
        this.location = location;
        this.floatLoop = true;
        
        items.add(this);
    }

    /**
     * @note This needs to be ran on onEnable in order to update properly
     */
    public static void enable(JavaPlugin plugin) {
        new BukkitRunnable() {
        	
            @Override
            public void run() {
                FloatingItem.getFloatingItems().stream().filter(i -> i.getArmorStand() != null).forEach(i -> i.update());
            }
        }.runTaskTimerAsynchronously(plugin, 0, 1);
    }

    /**
     * Spawns the floating item with the given text and item type
     *
     * @param title Then text of the floating item
     * @param itemStack The itemstack
     * @param big Whether the item should be big or not
     */
    public void spawn(Location location, ItemStack itemStack, String displayName) {
        // Create a stationary armor stand for the display name
        ArmorStand nameArmorStand = (ArmorStand) location.getWorld().spawnEntity(location.clone().add(0, 1.5, 0), EntityType.ARMOR_STAND);

        // Customize the armor stand's appearance and behavior for the display name
        nameArmorStand.setGravity(false); // Disable gravity so it stays stationary
        nameArmorStand.setCustomName(displayName);
        nameArmorStand.setCustomNameVisible(true);
        nameArmorStand.setVisible(false);

        // Spawn the armor stand for the item directly under the title
        ArmorStand itemArmorStand = (ArmorStand) location.getWorld().spawnEntity(location.clone().add(0, 0.0, 0), EntityType.ARMOR_STAND);

        // Customize the armor stand's appearance and behavior for the item
        itemArmorStand.setGravity(false); // Disable gravity so it can float
        itemArmorStand.setVisible(false); // Make it invisible if needed
        itemArmorStand.setBasePlate(false); // Hide the base plate if needed
        itemArmorStand.setCanPickupItems(false); // Disable item pickup

        // Set the item on the armor stand's head
        itemArmorStand.getEquipment().setHelmet(itemStack);

        // Store the initial Y position for the item
        double initialY = location.getY()+1.3;

        // Schedule a repeating task to make the item stack float up and down and spin
        int itemTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), () -> {
            double yOffset = Math.sin(System.currentTimeMillis() * 0.002) * 0.2; // Adjust the amplitude for float height
            double newYaw = itemArmorStand.getLocation().getYaw() + 5.0; // Adjust the rotation speed

            // Update the Y position for floating without changing X and Z
            Location newLocation = itemArmorStand.getLocation().clone();
            newLocation.setY(initialY + yOffset);
            newLocation.setYaw((float) newYaw);

            itemArmorStand.teleport(newLocation);
        }, 0L, 1L); // Adjust the delay and period to control the speed of floating and rotation for the item

        // Store the task ID in a map or list so you can cancel it when needed
        floatingTasks.put(itemArmorStand.getUniqueId(), itemTaskId);
    }
    
    public void removeFloatingItem(UUID armorStandUUID) {
        // Get the task ID associated with the armor stand
        Integer taskId = floatingTasks.get(armorStandUUID);

        // Cancel the task if it exists
        if (taskId != null) {
            Bukkit.getScheduler().cancelTask(taskId);
            floatingTasks.remove(armorStandUUID);
        }

        // Remove the armor stand
        // ...
    }

    /**
     * Updates the floating item
     */
    public void update() {
        Location location = armorStand.getLocation();

        if (!this.floatLoop) {
            location.add(0, 0.01, 0);
            location.setYaw((location.getYaw() + 7.5F));

            armorStand.teleport(location);

            if (armorStand.getLocation().getY() > (0.25 + sameLocation.getY()))
                this.floatLoop = true;
        } 
        else {
            location.subtract(0, 0.01, 0);
            location.setYaw((location.getYaw() - 7.5F));

            armorStand.teleport(location);

            if (armorStand.getLocation().getY() < (-0.25 + sameLocation.getY()))
                this.floatLoop = false;
        }
    }

    public void addText(FloatingItem floatingItem, String... text) {
        ArmorStand armorStand = null;
        List<String> lines = Arrays.asList(text);
        lines = Lists.reverse(lines);

        double y = 0.25D;

        for (int i = 0; i < lines.size(); i++) {
            armorStand = (ArmorStand) location.getWorld().spawnEntity(location.clone().add(0, y, 0), EntityType.ARMOR_STAND);
            armorStand.setGravity(false);
            armorStand.setCustomName(lines.get(i).replace('&', '§'));
            armorStand.setCustomNameVisible(true);
            armorStand.setVisible(false);
            y += 0.21D;
            
            texts.add(armorStand);
        }
    }
    
    public void updateTitle(String title) {
        if (armorStand != null) {
            armorStand.setCustomName(title);
        }
        
        // Schedule a task to remove the armor stand if it's too far from players
        BukkitRunnable removalTask = new BukkitRunnable() {
            @Override
            public void run() {
                // Check if the armor stand is far from players and remove it
                if (armorStand != null && armorStand.getLocation().distanceSquared(location) > 100) {
                    armorStand.remove();
                    armorStand = null;
                    // Also cancel this task since the armor stand is removed
                    this.cancel();
                }
            }
        };
        
        // Schedule the removal task to run in the future
        removalTask.runTaskLater(Main.getInstance(), 20L * 60); // Remove after 60 seconds (adjust as needed)
    }
    
    public String getTitle() {
        if (armorStand != null) {
            return armorStand.getCustomName();
        }
        return null; // Return null if the armor stand is not present
    }

    /**
     * Deletes all text that the floating item has
     */
    public void deleteAllText() {
        texts.forEach(t -> t.remove());
    }

    /**
     * Deletes this floating item
     */
    public void delete() {
        deleteAllText();
        if (armorStand != null)
            armorStand.remove();
    }
    
    /**
     * Resets all floating items
     */
    public void reset() {
        getFloatingItems().remove(this);
    }

    /**
     * Deletes all floating items on the server
     */
    public static void deleteAll() {
        getFloatingItems().forEach(i -> i.delete());
        getFloatingItems().clear();
    }

    /**
     * Gets all registered floating items
     * @return All floating items
     */
    public static List<FloatingItem> getFloatingItems() {
        return items;
    }

    /**
     * Gets all text holders
     * @return All text holders
     */
    public List<ArmorStand> getTexts() {
        return texts;
    }

    /**
     * Gets the location of the floating item
     * @return The location
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Gets the armorstand of the floating item
     * @return The armorstand
     */
    public ArmorStand getArmorStand() {
        return armorStand;
    }
}