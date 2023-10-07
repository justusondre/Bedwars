package command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import bedwars.Main;
import config.ArenaFile;

public class CreateArena implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
	    if (sender instanceof Player) {
	        if (cmd.getName().equalsIgnoreCase("createarena")) {
	            Player player = (Player) sender;

	            if (args.length != 1) {
	                player.sendMessage(ChatColor.RED + "Wrong usage: /createarena <arena name>");
	                return true;
	            }

	            String arenaName = args[0].toLowerCase();
	        	ArenaFile arenaFile = Main.getInstance().getFileManager().getArenaFile();

	            if (arenaFile.config.contains("instance." + arenaName.toLowerCase())) {
	                player.sendMessage(ChatColor.RED + "That arena already exists!");
	            } else {
	                try {
	                	arenaFile.createArena(arenaName);
	                    player.sendMessage(ChatColor.GRAY + "You have created an arena: " + ChatColor.GREEN + arenaName.toUpperCase());
	                } catch (Exception ex) {
	                    player.sendMessage(ChatColor.RED + "An error occurred while creating the arena.");
	                }
	            }
	        }
	    }
	    return true;
	}
}