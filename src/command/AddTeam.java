package command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import bedwars.Main;
import config.ArenaFile;
import object.Team;
import object.TeamColor;
import object.TeamManager;

public class AddTeam implements CommandExecutor {
	
	ArenaFile arenaFile = Main.getInstance().getFileManager().getArenaFile();
    TeamManager teamManager = Main.getInstance().getTeamManager();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            // The command can only be executed by players
            sender.sendMessage(ChatColor.RED + "This command can only be executed by players.");
            return true;
        }

        if (args.length != 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /addteam <arena> <color>");
            return true;
        }

        String arenaName = args[0];
        String teamColorName = args[1].toUpperCase(); // Convert input to uppercase

        // Check if the specified team color is valid
        TeamColor teamColor = null;
        for (TeamColor color : TeamColor.values()) {
            if (color.name().equals(teamColorName)) {
                teamColor = color;
                break;
            }
        }

        if (teamColor == null) {
            sender.sendMessage(ChatColor.RED + "Invalid team color. Available colors: " + getValidTeamColorsList());
            return true;
        }

        // Check if the team already exists in the arena configuration
        if (arenaFile.getTeamColorsInArena(arenaName).contains(teamColor)) {
            sender.sendMessage(ChatColor.YELLOW + "Team " + teamColor.getChatColor() + teamColor.getDisplayName() + ChatColor.YELLOW + " is already added to arena " + arenaName + ".");
            return true;
        }

        // Create a new team with the specified color
        Team team = new Team(teamColor);

        // Add the team to the specified arena
        teamManager.addTeamToArena(arenaName, team);

        sender.sendMessage(ChatColor.YELLOW + "Team " + teamColor.getChatColor() + teamColor.getDisplayName() + ChatColor.YELLOW + " added to arena " + arenaName + ".");
        return true;
    }

    // Retrieve valid team colors from your configuration file
    private String getValidTeamColorsList() {
        StringBuilder validColorsList = new StringBuilder();
        TeamColor[] teamColors = TeamColor.values();

        for (int i = 0; i < teamColors.length; i++) {
            TeamColor color = teamColors[i];
            validColorsList.append(color.getChatColor() + "(" + color.getDisplayName() + ")");
            
            // Add a comma and space unless it's the last color in the list
            if (i < teamColors.length - 1) {
                validColorsList.append(", ");
            }
        }

        return validColorsList.toString();
    }
}