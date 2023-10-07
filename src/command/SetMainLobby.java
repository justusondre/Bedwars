package command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import bedwars.Main;
import config.LobbyFile;
import net.md_5.bungee.api.ChatColor;

public class SetMainLobby implements CommandExecutor {
		
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if(sender instanceof Player) {
			if(cmd.getName().equalsIgnoreCase("setmainlobby")) {
			Player player = (Player) sender;
			
		if(args.length != 0) {
			player.sendMessage(ChatColor.RED + "Wrong usage: /setmainlobby");
			return true;
			
			}
		
		LobbyFile lobbyFile = Main.getInstance().getFileManager().getLobbyFile();
		
		lobbyFile.setLocation(player.getLocation());
		player.sendMessage(ChatColor.GREEN + "SUCCESS!" + ChatColor.GRAY + " You have added the main lobby spawn.");
		
			}
		}
		return true;
	}
}
