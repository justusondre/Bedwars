package utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class StringUtils {
	
	public static String capitalizeLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        String lowerInput = input.toLowerCase();
        return lowerInput.substring(0, 1).toUpperCase() + lowerInput.substring(1);
    }
	
	public static void clearChat(Player player, int lines) {
        for (int i = 0; i < lines; i++) {
            player.sendMessage(""); // Sending empty messages to clear chat lines
        }
    }
	
	public static String formatTeamColorName(ChatColor teamColor) {
	    String colorName = teamColor.name().replace("_", " ").toLowerCase();
	    StringBuilder formattedName = new StringBuilder();

	    // Split the color name by space and capitalize each word
	    for (String word : colorName.split(" ")) {
	        if (formattedName.length() > 0) {
	            formattedName.append(" ");
	        }
	        formattedName.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
	    }

	    return formattedName.toString();
	}
}
