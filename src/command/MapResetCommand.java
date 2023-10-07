package command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import mapreset.MapReset;

public class MapResetCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("unload")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                // Check if the player has the necessary permissions to use this command.
                if (!player.hasPermission("yourplugin.unloadmap")) {
                    player.sendMessage("You do not have permission to use this command.");
                    return true;
                }

                // Check if the correct number of arguments is provided.
                if (args.length != 1) {
                    player.sendMessage("Usage: /unloadMap <mapName>");
                    return true;
                }

                String mapName = args[0];
                // Call your unloadMap method here.
                MapReset.unloadMap(mapName);
                player.sendMessage("Map " + mapName + " unloaded.");
                return true;
            } else {
                sender.sendMessage("This command can only be run by a player.");
                return true;
            }
        } else if (cmd.getName().equalsIgnoreCase("load")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                // Check if the player has the necessary permissions to use this command.
                if (!player.hasPermission("yourplugin.loadmap")) {
                    player.sendMessage("You do not have permission to use this command.");
                    return true;
                }

                // Check if the correct number of arguments is provided.
                if (args.length != 1) {
                    player.sendMessage("Usage: /loadMap <mapName>");
                    return true;
                }

                String mapName = args[0];
                // Call your loadMap method here.
                MapReset.loadMap(mapName);
                player.sendMessage("Map " + mapName + " loaded.");
                return true;
            } else {
                sender.sendMessage("This command can only be run by a player.");
                return true;
            }
        }
        return false;
    }
}
