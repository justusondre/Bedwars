package command;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import database.MySQLDatabase;

public class AddKill implements CommandExecutor {
	private final MySQLDatabase mysqlDatabase;

    public AddKill(JavaPlugin plugin, MySQLDatabase mysqlDatabase) {
        this.mysqlDatabase = mysqlDatabase;
        // Register this command with your plugin's command manager
        plugin.getCommand("addkill").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if the sender is a player
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be executed by a player.");
            return true;
        }

        // Check if the command has the correct number of arguments
        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /addkill <player>");
            return true;
        }

        String playerName = args[0];

        // Convert the player name to a UUID (You should implement this method)
        UUID targetUUID = mysqlDatabase.getPlayerUUID(playerName.toString());

        if (targetUUID == null) {
            sender.sendMessage(ChatColor.RED + "Player not found.");
            return true;
        }

        // Add a kill to the player's kills count in the database
        mysqlDatabase.addKills(targetUUID, 1);

        sender.sendMessage(ChatColor.GREEN + "Kill added to player with UUID: " + targetUUID);
        return true;
    }
}