package mapreset;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import bedwars.Main;

public class MapReset {

	public static void unloadMap(String mapName) {
	    World w = Bukkit.getServer().getWorld(mapName);
	    if (w == null) {
	        // The world doesn't exist, kick the player from the server
	        for (Player p : Bukkit.getOnlinePlayers()) {
	            p.kickPlayer("The map you were in no longer exists.");
	        }
	        return;
	    }

	    for (Entity en : w.getEntities()) {
	        if (en instanceof Player) {
	            Player p = (Player) en;
	            p.teleport(Bukkit.getWorld("world").getSpawnLocation()); // Teleport to the "lobby" world
	        }
	    }

	    Bukkit.getServer().unloadWorld(w, false);
	}

    public static void loadMap(String mapName) {
        World w;
        if (Bukkit.getWorld(mapName) == null) {
            w = Bukkit.getServer().createWorld(new WorldCreator(mapName));
        } else {
            w = Bukkit.getWorld(mapName);
        }
        
        // Disable mob spawning
        w.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        w.setGameRule(GameRule.DO_FIRE_TICK, false);
        w.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        w.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        	
        // Kill all mobs in the world
        for (Entity entity : w.getEntities()) {
            if (entity instanceof LivingEntity && !(entity instanceof Player)) {
                entity.remove();
            }
        }
        
        // Remove all drops on the ground
        for (Entity drop : w.getEntitiesByClasses(Item.class)) {
            drop.remove();
        }
        
        w.setAutoSave(false);
    }
    
    public static void resetMap(String mapName) {
        unloadMap(mapName);
        
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
            loadMap(mapName);
        }, 200L);
    }
}