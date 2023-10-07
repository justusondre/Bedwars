package listener;

import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Scoreboard;

import bedwars.Main;
import database.MySQLConnection;
import database.MySQLDatabase;
import scoreboard.ScoreboardManager;

public class JoinListener implements Listener {
	
	private final MySQLDatabase mysqlDatabase;

    public JoinListener(MySQLConnection mysqlConnection) {
        this.mysqlDatabase = new MySQLDatabase(mysqlConnection);
        // Call the method to create the player data table during listener initialization
        mysqlDatabase.createPlayerDataTable();
    }

    @EventHandler
    public void onPlayerJoin2(PlayerJoinEvent event) throws SQLException {
        Player player = event.getPlayer();

        UUID playerUUID = player.getUniqueId();
        String playerName = player.getName();
        int innocentWins = 0;
        int murdererWins = 0;
        int deaths = 0;
        int kills = 0;

        mysqlDatabase.setUserData(playerUUID, playerName, innocentWins, murdererWins, deaths, kills);

        player.sendMessage("Welcome to the server! You've been added to the database.");
    }
    
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		Location mainLobbyLocation = Main.getInstance().getFileManager().getLobbyFile().getLocation();
		player.teleport(mainLobbyLocation);
		player.setGameMode(GameMode.ADVENTURE);
		
		ScoreboardManager scoreboardManager = Main.getInstance().getScoreboardManager();
    	scoreboardManager.clearScoreboard(player);
        Scoreboard scoreboard = scoreboardManager.createScoreboard();
        scoreboardManager.updateLobbyObjectives(player, scoreboard);

	}
}