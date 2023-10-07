package command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import bedwars.Main;
import bedwars.Mode;
import config.ArenaFile;

public class SetMode implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        if (args.length != 2) {
            sender.sendMessage("Usage: /setmode <arena> <mode>");
            return true;
        }

        String arenaName = args[0];
        String mode = args[1].toLowerCase(); // Convert the mode to lowercase for case-insensitive comparison

        // Parse the mode and check if it's a valid Mode enum value
        Mode selectedMode = Mode.valueOf(mode.toUpperCase());

        if (selectedMode == null) {
            sender.sendMessage("Invalid mode. Available modes: Solo, Doubles, 3v3v3v3, 4v4v4v4");
            return true;
        }

        ArenaFile arenaFile = Main.getInstance().getFileManager().getArenaFile();

        // Check if the arena exists
        if (!arenaFile.getArenasList().contains(arenaName)) {
            sender.sendMessage("Arena not found: " + arenaName);
            return true;
        }

        // Set the mode for the arena
        arenaFile.setMode(arenaName, selectedMode);
        sender.sendMessage("Mode for arena " + arenaName + " set to " + selectedMode.getDisplayName());
        return true;
    }
}