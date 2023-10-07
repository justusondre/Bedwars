package command;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import bedwars.Main;
import config.ArenaFile;
import object.TeamColor;
import object.TeamManager;

public class SetTeamSpawn implements CommandExecutor {
	
	TeamManager teamManager = Main.getInstance().getTeamManager();
    ArenaFile arenaFile = Main.getInstance().getFileManager().getArenaFile();

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        if (args.length != 2) {
            sender.sendMessage("Usage: /setteamspawn <arena> <color>");
            return true;
        }

        Player player = (Player) sender;
        String arenaName = args[0];
        String colorName = args[1].toUpperCase(); // Convert color to uppercase for enum

        // Check if the specified team color is valid
        TeamColor teamColor = null;
        for (TeamColor color : TeamColor.values()) {
            if (color.name().equals(colorName)) {
                teamColor = color;
                break;
            }
        }

        if (!arenaFile.getTeamColorsInArena(arenaName).contains(teamColor)) {
            sender.sendMessage("Invalid team color. Available colors: " + arenaFile.getTeamColorsInArena(arenaName));
            return true;
        }

        // Check if the arena exists
        if (!arenaFile.getArenasList().contains(arenaName)) {
            sender.sendMessage("Arena not found: " + arenaName);
            return true;
        }

        // Get the player's current location as the team spawn point
        Location teamSpawnLocation = player.getLocation();

        // Set the team spawn point in the configuration
        arenaFile.setTeamSpawn(arenaName, teamColor, teamSpawnLocation);
        sender.sendMessage(teamColor.getChatColor() + "(" + teamColor.getDisplayName() + ") team spawn location was set for arena: " + arenaName + "!");
        return true;
    }
}