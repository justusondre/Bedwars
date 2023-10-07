package handler;

public enum GameState {
	WAITING("Waiting"), 
	INGAME("Playing"), 
	ENDGAME("Ending"),
	MAPRESET("Resetting");

	public final String name;

	GameState(String name) {
		this.name = name;
	}
}