package utils;

import org.bukkit.ChatColor;

public class MathUtils {
	public static boolean isInteger(String str) {
		if (str == null)
			return false;
		int length = str.length();
		if (length == 0)
			return false;
		int i = 0;
		if (str.charAt(0) == '-') {
			if (length == 1)
				return false;
			i = 1;
		}
		for (; i < length; i++) {
			char c = str.charAt(i);
			if (c <= '/' || c >= ':')
				return false;
		}
		return true;
	}

	public static int getInt(String string) {
		return getInt(string, 0);
	}

	public static int getInt(String string, int def) {
		if (string == null)
			return def;
		try {
			return Integer.parseInt(string);
		} catch (NumberFormatException ignored) {
			return def;
		}
	}

	public static boolean isDouble(String str) {
		try {
			Double.parseDouble(str);
			return true;
		} catch (NumberFormatException ignored) {
			return false;
		}
	}

	public static double getDouble(String string) {
		return getDouble(string, 0.0D);
	}

	public static double getDouble(String string, double def) {
		if (string == null)
			return def;
		try {
			return Double.parseDouble(string);
		} catch (NumberFormatException ignored) {
			return def;
		}
	}

	public static boolean isLong(String str) {
		try {
			Long.parseLong(str);
			return true;
		} catch (NumberFormatException ignored) {
			return false;
		}
	}

	public static long getLong(String string) {
		return getLong(string, 0L);
	}

	public static long getLong(String string, long def) {
		if (string == null)
			return def;
		try {
			return Long.parseLong(string);
		} catch (NumberFormatException ignored) {
			return def;
		}
	}

	public static boolean isShort(String str) {
		try {
			Short.parseShort(str);
			return true;
		} catch (NumberFormatException ignored) {
			return false;
		}
	}

	public static long getShort(String string) {
		return getShort(string, (short) 0);
	}

	public static long getShort(String string, short def) {
		if (string == null)
			return def;
		try {
			return Short.parseShort(string);
		} catch (NumberFormatException ignored) {
			return def;
		}
	}

	public static boolean isFloat(String str) {
		try {
			Float.parseFloat(str);
			return true;
		} catch (NumberFormatException ignored) {
			return false;
		}
	}

	public static float getFloat(String string) {
		return getFloat(string, 0.0F);
	}

	public static float getFloat(String string, float def) {
		if (string == null)
			return def;
		try {
			return Float.parseFloat(string);
		} catch (NumberFormatException ignored) {
			return def;
		}
	}

	public static boolean isBetween(int value, int min, int max) {
		return (value >= min && value <= max);
	}

	public static int roundInteger(int integer, int floor) {
		return (integer < floor) ? floor : (int) (floor * Math.round(integer / floor));
	}
	
	public static String formatTime(int seconds, ChatColor numberColor, ChatColor wordColor) {
        if (seconds == 60) {
            return numberColor + "60 " + wordColor + "seconds";
        } else if (seconds < 60) {
            return numberColor + Integer.toString(seconds) + " " + wordColor + (seconds == 1 ? "second" : "seconds");
        }

        int minutes = (int) Math.ceil(seconds / 60.0);

        return numberColor + Integer.toString(minutes) + " " + wordColor + (minutes == 1 ? "minute" : "minutes");
    }
}