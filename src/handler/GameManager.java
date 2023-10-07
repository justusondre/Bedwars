package handler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Objective;

import bedwars.Main;
import config.ArenaFile;
import object.TeamColor;
import object.TeamManager;
import scoreboard.ScoreboardStatus;

public class GameManager {
	
	ArenaFile arenaFile = Main.getInstance().getFileManager().getArenaFile();
    TeamManager teamManager = Main.getInstance().getTeamManager();
	
    String formattedDate = new SimpleDateFormat("dd/MM/YY").format(new Date());
    String id = GameIdGenerator.generateUniqueArenaId();
	
	public void setScoreboardTitle(Player player) {
		ScoreboardStatus scoreboard = new ScoreboardStatus(player);
		Objective objective = scoreboard.getObjective();
	    startGlitchingTitleAnimation(objective);

	}
	
	public void updateLobbyBoard(Player player, ScoreboardStatus scoreboard, String arenaName) {
		
	}
	
	public void updateGameBoard(Player player, ScoreboardStatus scoreboard, String arenaName) {
		List<TeamColor> availableTeamColors = arenaFile.getTeamColorsInArena(arenaName);
        int score = availableTeamColors.size(); // Start with a high initial score
        
        scoreboard.updateLine(score+8, ChatColor.DARK_GRAY + formattedDate + " " + ChatColor.GRAY + "" + ChatColor.ITALIC + "" + id);
        scoreboard.updateLine(score+7, "");
        
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

            scoreboard.updateLine(score+6, status);
            score--;
        }

        scoreboard.updateLine((score - score) + 5, "");
        scoreboard.updateLine(4, "Total Kills: " + ChatColor.GREEN + "1");
        scoreboard.updateLine(3, "Beds Broken: " + ChatColor.GREEN + "1");
        scoreboard.updateLine(2, "");
        scoreboard.updateLine(1, "play-bedwars.com");

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