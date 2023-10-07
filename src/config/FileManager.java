package config;

public class FileManager {
	
	public LobbyFile lobbyFile;
	public ConfigFile configFile;
	public ArenaFile arenaFile;

	public FileManager() {
		this.lobbyFile = new LobbyFile();
		this.configFile = new ConfigFile();
		this.arenaFile = new ArenaFile();
		
	}
	
	public LobbyFile getLobbyFile() {
		return this.lobbyFile;
	}
	
	public ConfigFile getConfigFile() {
		return this.configFile;
	}
	
	public ArenaFile getArenaFile() {
		return this.arenaFile;
	}
}