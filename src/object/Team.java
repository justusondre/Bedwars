package object;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import utils.StringUtils;

public class Team {
	
	private TeamColor teamColor;
    private List<Player> players;

    public Team(TeamColor teamColor) {
        this.teamColor = teamColor;
        this.players = new ArrayList<>();
    }

    public TeamColor getTeamColor() {
        return teamColor;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void addPlayer(Player player) {
        if (!players.contains(player)) {
            players.add(player);
            // Implement logic to assign the player to the team in your plugin
        }
    }

    public void removePlayer(Player player) {
        if (players.contains(player)) {
            players.remove(player);
            // Implement logic to remove the player from the team in your plugin
        }
    }

    public String getTeamName() {
        if (teamColor == TeamColor.PINK) {
            return "Pink";
        }
        return StringUtils.formatTeamColorName(teamColor.getChatColor());
    }

    public int getSize() {
        return players.size();
    }
}