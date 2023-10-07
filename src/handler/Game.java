package handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;

import bedwars.Main;
import bedwars.Mode;
import config.ArenaFile;
import config.LobbyFile;
import generators.BedwarsGenerator;
import generators.FloatingItem;
import object.Team;
import object.TeamColor;
import object.TeamManager;
import scoreboard.ScoreboardManager;
import scoreboard.ScoreboardStatus;
import utils.Title;

public class Game {
	
    private ArenaFile arenaFile = Main.getInstance().getFileManager().getArenaFile();
    private GameCountdown gameCountdown = Main.getInstance().getCountdownManager();
	
	private Map<String, GameState> arenaStates;
    private Map<Player, String> playerArenas = new HashMap<>();
    private List<Player> alivePlayers = new ArrayList<>();
    private List<Player> deadPlayers = new ArrayList<>();
    
    private final int id;
    private final Mode mode;
    private final String arenaName;

    public Game(String arenaName, int id, Mode mode) {
        arenaStates = new HashMap<>();
        this.id = id;
        this.mode = mode;
        this.arenaName = arenaName;
    }
    
    public String getArenaName() {
    	return arenaName;
    }
    
    public int getID() {
    	return id;
    }
    
    public Mode getMode() {
    	return mode;
    	
    }
    
    public void joinArena(Player player, String arenaName) {
		if (!isPlayerInArena(player)) {
			playerArenas.put(player, arenaName);
			addToAlive(player);
			player.setHealth(20.0D);
			player.setFoodLevel(40);
			player.setGameMode(GameMode.SURVIVAL);
			player.setExp(0);
			player.setTotalExperience(0);
			player.getInventory().clear();
			ArenaFile arenaFile = Main.getInstance().getFileManager().getArenaFile();
			player.teleport(arenaFile.getArenaLobby(arenaName));
			checkMinPlayers(arenaName);
			broadcastMessageToPlayersInArena(arenaName, ChatColor.GREEN + "▶ " + ChatColor.YELLOW + "" + player.getDisplayName().toString() + " has joined the game!");
			TeamManager teamManager = Main.getInstance().getTeamManager();
			teamManager.assignPlayerToRandomTeam(player, arenaName);
			ScoreboardManager scoreboardManager = Main.getInstance().getScoreboardManager();
            ScoreboardStatus scoreboardStatus = new ScoreboardStatus(player);
            scoreboardManager.updateWaitingObjectives(player, scoreboardStatus, arenaName);
            
		} else {
			player.sendMessage(ChatColor.RED + "You are already in an arena. Leave the current arena to join another one.");
		}
	}

	public void leaveArena(Player player) {
		if (isPlayerInArena(player)) {
			String arenaName = getArenaName(player);
			playerArenas.remove(player);
			removeFromAlive(player);
			removeFromDead(player);
			
			Game game = Main.getInstance().getGame();
			game.resetArenaIfEmpty(arenaName);
			
			LobbyFile lobbyFile = Main.getInstance().getFileManager().getLobbyFile();
            Location spawnLocation = lobbyFile.getLocation();
            player.teleport(spawnLocation);
		        	
		} else {
			
			player.sendMessage("You are not in any arena.");
		}
	}

    public void setGameState(String arenaName, GameState state) {
        arenaStates.put(arenaName, state);
    }

    public GameState getGameState(String arenaName) {
        return arenaStates.getOrDefault(arenaName, GameState.WAITING);
    }

    public void start(String arenaName) {
    	setGameState(arenaName, GameState.WAITING);
        gameCountdown.startCountdown(arenaName, 15, GameState.INGAME);
    }

    public void broadcast(String arenaName, String message) {
        List<Player> playersInArena = getPlayersInArena(arenaName);
        for (Player player : playersInArena) {
            player.sendMessage(message);
        }
    }

    public void stop(String arenaName) {
    	setGameState(arenaName, GameState.ENDGAME);
        resetArenaIfEmpty(arenaName);
    }

    public void run(String arenaName) {
    	
    }

    public void checkMinPlayers(String arenaName) {
        List<Player> playersInArena = getPlayersInArena(arenaName);
        if (playersInArena.size() == 1) {
            start(arenaName);
        }
    }

    public void resetArenaIfEmpty(String arenaName) {
        if (getPlayersInArena(arenaName).isEmpty()) {
        	gameCountdown.cancelCountdown(arenaName);
        	setGameState(arenaName, GameState.WAITING);
        }
    }

    public List<Player> getPlayersInArena(String arenaName) {
        List<Player> playersInArena = new ArrayList<>();
        for (Map.Entry<Player, String> entry : playerArenas.entrySet()) {
            if (entry.getValue().equalsIgnoreCase(arenaName))
                playersInArena.add(entry.getKey());
        }
        return playersInArena;
    }

    public boolean isPlayerInArena(Player player) {
        return playerArenas.containsKey(player);
    }

    public List<Player> getAlivePlayersInArena(String arenaName) {
        List<Player> alivePlayersInArena = new ArrayList<>();
        for (Player player : alivePlayers) {
            if (playerArenas.containsKey(player) && playerArenas.get(player).equalsIgnoreCase(arenaName))
                alivePlayersInArena.add(player);
        }
        return alivePlayersInArena;
    }

    public List<Player> getDeadPlayersInArena(String arenaName) {
        List<Player> deadPlayersInArena = new ArrayList<>();
        for (Player player : deadPlayers) {
            if (playerArenas.containsKey(player) && playerArenas.get(player).equalsIgnoreCase(arenaName))
                deadPlayersInArena.add(player);
        }
        return deadPlayersInArena;
    }
    
    public void broadcastMessageToPlayersInArena(String arenaName, String message) {
		List<Player> playersInArena = getPlayersInArena(arenaName);
		for (Player player : playersInArena)
			player.sendMessage(message);
	}
    
    public void teleportPlayersToTeamSpawns(String arenaName) {
        List<Player> playersInArena = getAlivePlayersInArena(arenaName);
        TeamManager teamManager = Main.getInstance().getTeamManager();
        for (Player player : playersInArena) {
            Team playerTeam = teamManager.getPlayerTeam(player, arenaName);
            if (playerTeam != null) {
                Location teamSpawn = arenaFile.getTeamSpawn(arenaName, playerTeam.getTeamColor());
                if (teamSpawn != null) {
                    player.teleport(teamSpawn);
                    
                    ScoreboardManager scoreboardManager = Main.getInstance().getScoreboardManager();
                    ScoreboardStatus scoreboardStatus = new ScoreboardStatus(player);
                    scoreboardManager.updateGameObjectives(player, scoreboardStatus, arenaName);
                }
            }
        }
    }

    public void checkBedStatusInArena(String arenaName) {
        List<Player> playersInArena = getAlivePlayersInArena(arenaName);
        boolean bedDestroyed = false;
        TeamColor destroyedTeamColor = null;

        for (TeamColor teamColor : arenaFile.getTeamColorsInArena(arenaName)) {
            Location bedLocation = arenaFile.getBedLocation(arenaName, teamColor);

            if (bedLocation != null && !isBedBlockIntact(bedLocation)) {
                bedDestroyed = true;
                destroyedTeamColor = teamColor;
                break;
            }
        }

        if (bedDestroyed && destroyedTeamColor != null) {
            String destroyedTeamName = destroyedTeamColor.getDisplayName();
            ChatColor destroyedTeamChatColor = destroyedTeamColor.getChatColor();

            for (Player player : playersInArena) {
                Title.sendTitle(player, 0, 20 * 2, 20 * 1, "", destroyedTeamChatColor + "" + ChatColor.BOLD + destroyedTeamName + ChatColor.RED + ChatColor.BOLD + " team's bed has been destroyed!");
                broadcast(arenaName, "");
                broadcast(arenaName, destroyedTeamChatColor + "" + ChatColor.BOLD + destroyedTeamName + ChatColor.RED + ChatColor.BOLD + " team's bed has been destroyed!");
                broadcast(arenaName, "");
                player.playSound(player.getLocation(), Sound.ENTITY_WITHER_DEATH, 1, 1);
            }
        }
    }
    
    public void startEmeraldGenerators(String arenaName, int tier, int speed) {
    	for(Location location : arenaFile.getAllEmeraldSpawns(arenaName)) {
        	BedwarsGenerator emeraldGenerator = new BedwarsGenerator(arenaName, BedwarsGenerator.GeneratorType.EMERALD_GENERATOR, tier, location, speed); // 20 ticks = 1 second
        	emeraldGenerator.startGeneration();
            FloatingItem emeraldItem = new FloatingItem(location);
            emeraldItem.spawn(location, new ItemStack(Material.EMERALD_BLOCK), ChatColor.GREEN + "Emerald Generator " + emeraldGenerator.getCountdownValue());
            emeraldItem.updateTitle(emeraldItem.getTitle());

        }
      } 
    
    public void startDiamondGenerators(String arenaName, int tier, int speed) {
    	for(Location location : arenaFile.getAllDiamondSpawns(arenaName)) {
        	BedwarsGenerator diamondGenerator = new BedwarsGenerator(arenaName, BedwarsGenerator.GeneratorType.DIAMOND_GENERATOR, tier, location, speed); // 20 ticks = 1 second
        	diamondGenerator.startGeneration();
            FloatingItem diamondItem = new FloatingItem(location);
            diamondItem.spawn(location, new ItemStack(Material.DIAMOND_BLOCK), ChatColor.AQUA + "Diamond Generator " + diamondGenerator.getCountdownValue());
            diamondItem.updateTitle(diamondItem.getTitle());

        }
      } 
    
    public void removeAllPlayersFromArena(String arenaName, Location mainLobbyLocation) {
		List<Player> playersInArena = getPlayersInArena(arenaName);
		TeamManager teamManager = Main.getInstance().getTeamManager();
		for (Player player : playersInArena) {
			this.playerArenas.remove(player);
			removeFromAlive(player);
			removeFromDead(player);
			player.sendMessage("You have left the arena.");
			player.teleport(mainLobbyLocation);
			teamManager.removeFromTeam(player, arenaName);
			
			ScoreboardManager scoreboardManager = Main.getInstance().getScoreboardManager();
            Scoreboard scoreboard = scoreboardManager.createScoreboard();
            scoreboardManager.updateLobbyObjectives(player, scoreboard);
		}
		Game game = Main.getInstance().getGame();
		game.resetArenaIfEmpty(arenaName);
		
	}

    public boolean isBedBlockIntact(Location bedLocation) {
        Block bedBlock = bedLocation.getBlock();
        return bedBlock.getType() == Material.RED_BED;
    }

    public String getArenaName(Player player) {
        return playerArenas.get(player);
    }

    public void addToAlive(Player player) {
        alivePlayers.add(player);
    }

    public void removeFromAlive(Player player) {
        alivePlayers.remove(player);
    }

    public void addToDead(Player player) {
        deadPlayers.add(player);
    }

    public void removeFromDead(Player player) {
        deadPlayers.remove(player);
    }
}