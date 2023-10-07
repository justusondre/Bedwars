package command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import bedwars.Main;
import config.ArenaFile;
import object.TeamColor;
import object.TeamManager;

public class SetBedLocation implements CommandExecutor {
	
	TeamManager teamManager = Main.getInstance().getTeamManager();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be run by a player.");
            return true;
        }

        if (args.length != 2) {
            sender.sendMessage("Usage: /setbedlocation <arena> <color>");
            return true;
        }

        Player player = (Player) sender;
        String arenaName = args[0];
        String colorName = args[1]; // Accept color name from the command

        // Map the color name to the corresponding TeamColor enum value
        TeamColor teamColor = mapColorNameToEnum(colorName);

        if (teamColor == null) {
            sender.sendMessage("Invalid color name. Available colors are: " + getAvailableColors());
            return true;
        }

        // Set the bed location in the arena configuration
        ArenaFile arenaFile = Main.getInstance().getFileManager().getArenaFile();
        arenaFile.setBedLocation(arenaName, teamColor, player.getLocation());

        // Get the team name based on the team color
        String teamName = teamManager.getTeam(teamColor.getChatColor()).getTeamName();

        sender.sendMessage("Bed location for team " + teamColor.getChatColor() + "(" + teamName + ")" + ChatColor.WHITE + " in arena " + arenaName + " set to your current location.");

        return true;
    }

    private TeamColor mapColorNameToEnum(String colorName) {
        for (TeamColor teamColor : TeamColor.values()) {
            if (teamColor.getDisplayName().equalsIgnoreCase(colorName)) {
                return teamColor;
            }
        }
        return null;
    }

    private String getAvailableColors() {
        StringBuilder colors = new StringBuilder();
        for (TeamColor teamColor : TeamColor.values()) {
            colors.append(teamColor.getDisplayName()).append(", ");
        }
        if (colors.length() > 2) {
            colors.setLength(colors.length() - 2); // Remove the trailing comma and space
        }
        return colors.toString();
    }
}