package config;

import bedwars.Main;
import handler.Game;
import handler.GameState;

public enum Messages {
	PREFIX("§b[Bedwars] "), GAME_IS_FULL("is full!"), GAME_NULL("dosen't exist!"),
	GAME_HAS_STARTED("already started!"), GAME_JOIN_ANOTHER_GAME("can't play in more than 1 game!"),
	GAME_START("starts in seconds."), GAME_JOIN("joined the game. "), GAME_LEAVE("left the game. "),
	GAME_LEFT("left the game"), GAME_NOGAME_LEAVE("have to be in game to use this command!"),
	GAME_NO_PLAYERS("enough player for game to continue!"), GAME_YOU_STOLE("stole a present!"), GAME_OVER("Over!"),
	GAME_WON("WIN!"), GAME_NAME("  Grinch Simulator"), SIGN_FIRST("%prefix%"), SIGN_SECOND("Right click"),
	STATE_WAITING("WAITING"), STATE_IN_GAME("IN GAME"), STATE_ENDING("ENDING"), TITLE_FIRST("got FIRST PLACE!"),
	TITLE_SECOND("got SECOND PLACE!"), TITLE_THIRD("got THIRD PLACE!"), TITLE_OVER("got %rank%th place!"),
	TITLE_REMAINING("remaining"), NOT_ENOUGH_PLAYERS("enough players!"), ITEM_RIGHT_CLICK("Right click"),
	ITEM_LEFTGAME_NAME("game"), ITEM_LEFTGAME_LORE("the game."), BAR_PLAYERS("should be at least for game to begin!"),
	RESTRICTED_COMMAND("can't use commands in-game!"), SCOREBOARD_TITLE("Simulator"), SCOREBOARD_LOBBY_ID("Map: "),
	SCOREBOARD_LOBBY_PLAYERS("Players:"), SCOREBOARD_LOBBY_GAME_START("Game starts in:"),
	SCOREBOARD_LOBBY_WAITING("Waiting..."),
	GAME_START_MESSAGE("Steal more presents than any#player before the time runs out!"),

	BAR_WAITING_COUNTDOWN("§e▶ §7The game will begin in §a%timer% §7seconds"),
	BAR_ENDGAME_COUNTDOWN("§e▶ §7The map will reset in §a%timer% §7seconds"),
	BAR_INGAME_COUNTDOWN("§e▶ §7Deathmatch will begin in §a%timer% §7seconds");

	private String msg;

	Messages(String msg) {
		this.msg = msg;
	}

	public void setMessage(String msg) {
		this.msg = msg.replace("&", "§");
	}

	public String toString() {
		return this.msg;
	}

	public static Messages getEnum(String name) {
		byte b;
		int i;
		Messages[] arrayOfMessages;
		for (i = (arrayOfMessages = values()).length, b = 0; b < i;) {
			Messages type = arrayOfMessages[b];
			if (type.name().equals(name))
				return type;
			b++;
		}
		return null;
	}

	public static String getActionBarMessage(String arenaName) {
		Game game = Main.getInstance().getGame();
		GameState gameState = game.getGameState(arenaName);

		switch (gameState) {
		case ENDGAME:
			return Messages.BAR_ENDGAME_COUNTDOWN.toString();

		case INGAME:
			return Messages.BAR_INGAME_COUNTDOWN.toString();

		case WAITING:
			return Messages.BAR_WAITING_COUNTDOWN.toString();

		default:
			return "";
		}
	}
}
