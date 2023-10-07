package command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import bedwars.Main;
import config.ArenaFile;
import handler.Game;
import object.Team;
import object.TeamManager;

public class JoinTeam implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be run by a player.");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage("Usage: /jointeam <color>");
            return true;
        }

        Player player = (Player) sender;
        String teamColor = args[0].toUpperCase(); // Convert to uppercase

        // Check if the team is listed in the arena's configuration
        String arenaName = getArenaName(player); // Implement this method
        ArenaFile arenaFile = Main.getInstance().getFileManager().getArenaFile();
        
        if (!arenaFile.isTeamListedInArena(arenaName, teamColor)) {
            sender.sendMessage("The team " + teamColor + " is not available for this arena.");
            return true;
        }

        // Get the corresponding ChatColor for the team
        ChatColor chatColor = ChatColor.valueOf(teamColor);

        TeamManager teamManager = Main.getInstance().getTeamManager();
        Team team = teamManager.getTeam(chatColor);

        if (team != null) {
            // Add the player to the team
            teamManager.addPlayerToTeam(player, team, arenaName);
            sender.sendMessage("You have joined the " + team.getTeamName() + " team.");
            
        } else {
        	
            sender.sendMessage("Invalid team color.");
        }

        return true;
    }

    // Implement this method to get the player's arena name based on your logic
    private String getArenaName(Player player) {
        Game game = Main.getInstance().getGame();
        return game.getArenaName(player);
    }
}