package database;

public class UserData {
	
	private final String playerName;
    private final int innocentWins;
    private final int murdererWins;
    private final int deaths;
    private final int kills;

    public UserData(String playerName, int innocentWins, int murdererWins, int deaths, int kills) {
        this.playerName = playerName;
        this.innocentWins = innocentWins;
        this.murdererWins = murdererWins;
        this.deaths = deaths;
        this.kills = kills;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getInnocentWins() {
        return innocentWins;
    }

    public int getMurdererWins() {
        return murdererWins;
    }

    public int getDeaths() {
        return deaths;
    }
    
    public int getKills() {
        return kills;
    }
}
