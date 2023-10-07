package handler;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import bedwars.Main;
import config.ArenaFile;
import object.Team;
import object.TeamColor;
import object.TeamManager;
import utils.Title;

public class GameListener implements Listener {
	
	public static Set<Location> placedBlocks = new HashSet<>();
	public static Set<Location> brokenBlocks = new HashSet<>(); // New HashSet for broken blocks
	
	GameManager gameManager = Main.getInstance().getGameManager();
		
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		gameManager.setScoreboardTitle(player);

	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
	    Location placedLocation = event.getBlock().getLocation();
	    placedBlocks.add(placedLocation);
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
	    Location brokenLocation = event.getBlock().getLocation();
	    Material blockType = event.getBlock().getType();

	    // Check if the broken block's location is in the set
	    if (placedBlocks.contains(brokenLocation)) {
	        // Check if the broken block is a bed or a bed head (head of the bed)
	        if (blockType == Material.RED_BED || blockType == Material.RED_BED) {
	            placedBlocks.remove(brokenLocation);
	        }
	    } else if (blockType == Material.RED_BED || blockType == Material.RED_BED) {
	    	
	    } else {
	        event.setCancelled(true);
	        // You can send a message to the player indicating they can't break the block here
	        event.getPlayer().sendMessage("You can only break your own or other players' placed blocks.");
	    }

	    // Add the broken block's location to the brokenBlocks set
	    brokenBlocks.add(brokenLocation);
	}

	@EventHandler
	public void onBedBreak(BlockBreakEvent event) {
	    Block bedBlock = event.getBlock();
	    Material bedMaterial = bedBlock.getType();
	    Player player = event.getPlayer();

	    // Check if the broken block is a bed block
	    if (bedMaterial == Material.RED_BED) {
	        Game game = Main.getInstance().getGame();
	        String arenaName = game.getArenaName(player); // Replace with your arena name

	        ArenaFile arenaFile = Main.getInstance().getFileManager().getArenaFile();

	        TeamManager teamManager = Main.getInstance().getTeamManager();
	        Team playerTeam = teamManager.getPlayerTeam(player, arenaName);

	        // Get the bed team color at the location of the broken bed
	        Location bedLocation = bedBlock.getLocation();
	        TeamColor bedTeamColor = arenaFile.getBedTeamColorAtLocation(arenaName, bedLocation);

	        // Check if the player is breaking their own team's bed
	        if (playerTeam != null && playerTeam.getTeamColor() == bedTeamColor) {
	            player.sendMessage(ChatColor.RED + "You can't break your own team's bed!");
	            event.setCancelled(true);
	            return;
	        }

	        // Delay the bed status check by 0.5 seconds
	        event.setDropItems(false);
	        event.getPlayer().playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
	        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
	            boolean isBedIntact = game.isBedBlockIntact(bedLocation);
	            if (!isBedIntact) {
	                Team destroyedTeam = new Team(bedTeamColor);
	                String destroyedTeamName = destroyedTeam.getTeamName();
	                ChatColor destroyedTeamChatColor = destroyedTeam.getTeamColor().getChatColor();

	                List<Player> playersInArena = game.getAlivePlayersInArena(arenaName);

	                for (Player arenaPlayer : playersInArena) {
	                    Title.sendTitle(arenaPlayer, 0, 20 * 2, 20 * 1, "", destroyedTeamChatColor + "" + ChatColor.BOLD + destroyedTeamName + ChatColor.RED + ChatColor.BOLD + " team's bed has been destroyed!");
	                    game.broadcast(arenaName, "");
	                    game.broadcast(arenaName, destroyedTeamChatColor + "" + ChatColor.BOLD + destroyedTeamName + ChatColor.RED + ChatColor.BOLD + " team's bed has been destroyed!");
	                    game.broadcast(arenaName, "");
	                    arenaPlayer.playSound(arenaPlayer.getLocation(), Sound.ENTITY_WITHER_DEATH, 1, 1);
	                }
	            }
	        }, 10L);
	    }
	}
	
    @EventHandler
    public void onLeafDecay(LeavesDecayEvent event) {
        event.setCancelled(true);
    }
    
    @EventHandler
    public void skipDeathScreen(PlayerDeathEvent e) {
        final Player p = e.getEntity();
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> p.spigot().respawn(), 2);
    }
    
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
    	Game game = Main.getInstance().getGame();
        Player player = event.getPlayer();
        String arenaName = game.getArenaName(player);

        ArenaFile arenaFile = Main.getInstance().getFileManager().getArenaFile();
        TeamManager teamManager = Main.getInstance().getTeamManager();

        // Get the player's team and bed location
        Team playerTeam = teamManager.getPlayerTeam(player, arenaName);
        Location bedLocation = arenaFile.getBedLocation(arenaName, playerTeam.getTeamColor());
        Location teamSpawn = arenaFile.getTeamSpawn(arenaName, playerTeam.getTeamColor());

        if (bedLocation != null && bedLocation.getBlock().getType() != Material.AIR) {
            // Player has a valid bed, respawn at the bed location
            event.setRespawnLocation(teamSpawn);
            player.sendMessage(ChatColor.GREEN + "You have respawned because your bed still exists!");

        } else {
            // Player doesn't have a valid bed, handle respawn logic here
            Location deathLocation = arenaFile.getArenaLobby(arenaName);

            if (deathLocation != null) {
                event.setRespawnLocation(deathLocation);
                player.sendMessage(ChatColor.YELLOW + "You were eliminated from the game because your bed is broken.");
                teamManager.removeFromTeam(player, arenaName);
            } else {
                // Handle the case where there's no valid spawn location, e.g., arena lobby
                Location worldSpawn = player.getWorld().getSpawnLocation();
                event.setRespawnLocation(worldSpawn);
                player.sendMessage(ChatColor.RED + "You can't respawn because your bed is broken. You've been teleported to the world spawn.");
            }
        }
    }
}