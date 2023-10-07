package command;

import bedwars.Main;
import config.ArenaFile;
import generators.BedwarsGenerator;
import generators.FloatingItem;
import handler.Game;
import handler.GameState;
import object.TeamColor;
import object.TeamManager;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ArenaInfo implements CommandExecutor {
		
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (!(sender instanceof Player)) {
      sender.sendMessage("This command can only be used by players.");
      return true;
      
    } 
    
    Player player = (Player)sender;
    Game game = Main.getInstance().getGame();
    if (args.length == 0) {
      if (game.isPlayerInArena(player)) {
        String arenaName = game.getArenaName(player);
        int playerCount = game.getPlayersInArena(arenaName).size();
        int aliveCount = game.getAlivePlayersInArena(arenaName).size();
        int deadCount = game.getDeadPlayersInArena(arenaName).size();
        GameState gameState = game.getGameState(arenaName);
        TeamManager teamManager = Main.getInstance().getTeamManager();
        
        player.sendMessage("Arena: " + arenaName);
        player.sendMessage("Players: " + playerCount);
        player.sendMessage("Alive: " + aliveCount);
        player.sendMessage("Dead: " + deadCount);
        player.sendMessage("Game State: " + gameState);
        player.sendMessage("");
        player.sendMessage("Remaining Teams:" + teamManager.getTeamsRemaining(arenaName).size() + "");
        player.sendMessage(teamManager.getTeamsRemainingMessage(arenaName));
        

        
      } else {
        player.sendMessage("You are not in an arena.");

      } 
    } else {
      player.sendMessage("Usage: /arenainfo");
    } 
    return true;
  }
}
