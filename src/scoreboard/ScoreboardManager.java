package scoreboard;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import bedwars.Main;
import config.ArenaFile;
import handler.Game;
import handler.GameCountdown;
import handler.GameIdGenerator;
import handler.GameState;
import object.TeamColor;
import object.TeamManager;

public class ScoreboardManager {
	
    String formattedDate = new SimpleDateFormat("dd/MM/YY").format(new Date());
    String id = GameIdGenerator.generateUniqueArenaId();

    public Scoreboard createScoreboard() {
        return org.bukkit.Bukkit.getScoreboardManager().getNewScoreboard();
        
    }
    
    public void updateTitle(Player player, Scoreboard scoreboard) {
    	ScoreboardStatus scoreboardStatus = new ScoreboardStatus(player);
		Objective objective = scoreboardStatus.getObjective();
	    startGlitchingTitleAnimation(objective);
    }
    
    public void updateLobbyObjectives(Player player, Scoreboard scoreboard) {
    	
    }
    
    public void updateWaitingObjectives(Player player, ScoreboardStatus scoreboard, String arenaName) {
    	Objective objective = scoreboard.getObjective();
	    startGlitchingTitleAnimation(objective);
    	
    	ArenaFile arenaFile = Main.getInstance().getFileManager().getArenaFile();
    	GameCountdown gameCountdown = Main.getInstance().getCountdownManager();
        Game game = Main.getInstance().getGame();
        
        scoreboard.updateLine(11, ChatColor.DARK_GRAY + formattedDate + " " + ChatColor.GRAY + "" + ChatColor.ITALIC + "" + id);
        scoreboard.updateLine(10, "");
        
        new BukkitRunnable() {
            int tick = 0;

            @Override
            public void run() {
                // Update the specific lines here
                scoreboard.updateLine(9, "Map: " + ChatColor.GREEN + arenaName);
                scoreboard.updateLine(8, "Players: " + ChatColor.GREEN + game.getPlayersInArena(arenaName).size());
                scoreboard.updateLine(7, "");
                scoreboard.updateLine(6, "Start in: " + ChatColor.GREEN + (gameCountdown.getRemainingSeconds(arenaName)+1));
                scoreboard.updateLine(5, "");
                scoreboard.updateLine(4, "Mode: " + ChatColor.GREEN + arenaFile.getMode(arenaName));
                scoreboard.updateLine(3, "Ver: " + ChatColor.GREEN + "1.20");
                scoreboard.updateLine(2, "");
                scoreboard.updateLine(1, ChatColor.AQUA + "play-bedwars.com");

                // Increment the tick count
                tick++;

                // If you want to stop the task after a certain duration (e.g., 30 seconds), you can do it like this:
                if (tick >= (gameCountdown.getRemainingSeconds(arenaName)*20) || game.getGameState(arenaName) == GameState.INGAME) {
                    cancel(); // Stop the task
                }
            }
        }.runTaskTimer(Main.getInstance(), 0L, 20L); // Run every second (20 ticks)
    }

    public void updateGameObjectives(Player player, ScoreboardStatus scoreboard, String arenaName) {
    	Objective objective = scoreboard.getObjective();
	    startGlitchingTitleAnimation(objective);
    	
    	ArenaFile arenaFile = Main.getInstance().getFileManager().getArenaFile();
        TeamManager teamManager = Main.getInstance().getTeamManager();
        Game game = Main.getInstance().getGame();
    	GameCountdown gameCountdown = Main.getInstance().getCountdownManager();
        
		List<TeamColor> availableTeamColors = arenaFile.getTeamColorsInArena(arenaName);
        int score = availableTeamColors.size(); // Start with a high initial score
        
        scoreboard.updateLine(score+8, ChatColor.DARK_GRAY + formattedDate + " " + ChatColor.GRAY + "" + ChatColor.ITALIC + "" + id);
        scoreboard.updateLine(score+7, "");
        
        new BukkitRunnable() {
            int tick = 0;

            @Override
            public void run() {
                int score = availableTeamColors.size(); // Start with a high initial score
                // Update the specific lines here
                for (TeamColor teamColor : availableTeamColors) {
                    object.Team team = (object.Team) teamManager.getTeam(teamColor.getChatColor()); // Use ChatColor to get the team
                    String teamName = team.getTeamName();

                    ChatColor teamTextColor = teamColor.getChatColor();
                    String initials = teamTextColor + teamName.substring(0, 1);
                    String formattedTeamName = ChatColor.WHITE + teamName;

                    boolean bedExists = teamManager.doesBedExist(arenaName, teamColor);

                    String status;
                    if (bedExists) {
                        status = initials + " " + formattedTeamName + ": " + ChatColor.GREEN + "✔";
                    } else {
                        int playerCount = team.getSize();
                        String countText = playerCount > 0 ? String.valueOf(playerCount) : ChatColor.RED + "✘";
                        status = initials + " " + formattedTeamName + ": " + ChatColor.GREEN + countText;
                    }

                    scoreboard.updateLine(score + 6, status);
                    score--;
                }

                scoreboard.updateLine((score - score) + 5, "");
                scoreboard.updateLine(4, "Total Kills: " + ChatColor.GREEN + "1");
                scoreboard.updateLine(3, "Beds Broken: " + ChatColor.GREEN + "1");
                scoreboard.updateLine(2, "");
                scoreboard.updateLine(1, ChatColor.AQUA + "play-bedwars.com");

                // Increment the tick count
                tick++;

                // If you want to stop the task after a certain duration (e.g., 30 seconds), you can do it like this:
                if (tick >= (gameCountdown.getRemainingSeconds(arenaName)*20) || game.getGameState(arenaName) == GameState.ENDGAME) {
                    cancel(); // Stop the task
                }
            }
        }.runTaskTimer(Main.getInstance(), 0L, 20L); // Run every second (20 ticks)
    }
    
    public void clearScoreboard(Player player) {
    	Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable() {
    		public void run() {
    		player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    		
    		}
    	}, 0L);
    }
    
    public void startGlitchingTitleAnimation(Objective objective) {
        BukkitRunnable animationTask = new BukkitRunnable() {
            int tick = 0;
            boolean isGlitching = false;

            @Override
            public void run() {
                tick++;

                if (tick % 200 == 0) { // Every 10 seconds (200 ticks)
                    isGlitching = true;
                }

                if (isGlitching) {
                    if (tick % 20 >= 0 && tick % 20 < 10) { // Glitch for 1 second (20 ticks)
                        if (tick % 2 == 0) { // Switch every 2 ticks
                            objective.setDisplayName(ChatColor.WHITE + "" + ChatColor.ITALIC + "" + ChatColor.BOLD + " BED WARS ");
                        } else {
                            objective.setDisplayName(ChatColor.RED + "" + ChatColor.ITALIC + "" + ChatColor.BOLD + " BED WARS ");
                        }
                    } else {
                        objective.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + " BED WARS ");
                        isGlitching = false;
                    }
                } else {
                    objective.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + " BED WARS ");
                }
            }
        };
        animationTask.runTaskTimer(Main.getInstance(), 0L, 1L); // Run every tick
    }
}