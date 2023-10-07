package command;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import bedwars.Main;
import config.ArenaFile;
import object.TeamColor;

public class SetGenerator implements CommandExecutor {

	@Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (args.length < 2) {
                player.sendMessage(ChatColor.RED + "Wrong usage: /" + cmd.getName() + " <genType> <arena> <number> or /" + cmd.getName() + " base <arena> <team>");
                return true;
            }

            String generatorType = args[0].toLowerCase();
            String arenaName = args[1].toLowerCase();

            ArenaFile arenaFile = Main.getInstance().getFileManager().getArenaFile();

            if (!arenaFile.config.contains("instance." + arenaName)) {
                player.sendMessage(ChatColor.RED + "The specified arena does not exist!");
                return true;
            }

            Location spawnLocation = player.getLocation();

            if (generatorType.equals("base")) {
                // Handle the "base" case here
                if (args.length != 3) {
                    player.sendMessage(ChatColor.RED + "Wrong usage for 'base' generator: /" + cmd.getName() + " base <arena> <team>");
                    return true;
                }

                String teamName = args[2].toUpperCase(); // Convert team name to uppercase
                TeamColor teamColor = null;

                // Check if the specified team color is valid
                for (TeamColor color : TeamColor.values()) {
                    if (color.name().equals(teamName)) {
                        teamColor = color;
                        break;
                    }
                }

                if (teamColor == null) {
                    player.sendMessage(ChatColor.RED + "Invalid team color. Available colors: " + arenaFile.getTeamColorsInArena(arenaName));
                    return true;
                }

                // Set the base generator spawn for the team
                arenaFile.setBaseGenerator(arenaName, teamColor, spawnLocation);

                player.sendMessage(ChatColor.GRAY + "You have set the base generator spawn for " + teamColor.getChatColor() + teamColor.getDisplayName() + ChatColor.GRAY + " in arena " + ChatColor.GREEN + arenaName);
            } else {
                // Handle other generator types
                if (args.length != 3) {
                    player.sendMessage(ChatColor.RED + "Wrong usage: /" + cmd.getName() + " <genType> <arena> <number>");
                    return true;
                }

                int number;

                try {
                    number = Integer.parseInt(args[2]);
                } catch (NumberFormatException ex) {
                    player.sendMessage(ChatColor.RED + "You need to specify a valid number!");
                    return true;
                }

                switch (generatorType) {
                    case "gold":
                        arenaFile.setGoldSpawn(arenaName, number, spawnLocation);
                        player.sendMessage(ChatColor.GRAY + "You have set the gold spawnpoint " + ChatColor.GREEN + number + ChatColor.GRAY + " for " + ChatColor.GREEN + arenaName);
                        break;
                    case "iron":
                        arenaFile.setIronSpawn(arenaName, number, spawnLocation);
                        player.sendMessage(ChatColor.GRAY + "You have set the iron spawnpoint " + ChatColor.GREEN + number + ChatColor.GRAY + " for " + ChatColor.GREEN + arenaName);
                        break;
                    case "diamond":
                        arenaFile.setDiamondSpawn(arenaName, number, spawnLocation);
                        player.sendMessage(ChatColor.GRAY + "You have set the diamond spawnpoint " + ChatColor.GREEN + number + ChatColor.GRAY + " for " + ChatColor.GREEN + arenaName);
                        break;
                    case "emerald":
                        arenaFile.setEmeraldSpawn(arenaName, number, spawnLocation);
                        player.sendMessage(ChatColor.GRAY + "You have set the emerald spawnpoint " + ChatColor.GREEN + number + ChatColor.GRAY + " for " + ChatColor.GREEN + arenaName);
                        break;
                    default:
                        player.sendMessage(ChatColor.RED + "Invalid genType. Available types: gold, iron, diamond, emerald, base.");
                        break;
                }
            }
        }
        return true;
    }
}