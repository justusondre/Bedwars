package utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocationUtils {

	public static String serializeLocation(Location location) {
        if (location == null) {
            return "";
        }

        return location.getWorld().getName() + ","
                + location.getX() + ","
                + location.getY() + ","
                + location.getZ() + ","
                + location.getYaw() + ","
                + location.getPitch();
    }

    public static Location deserializeLocation(String serializedLocation) {
        if (serializedLocation == null || serializedLocation.isEmpty()) {
            return null;
        }

        String[] parts = serializedLocation.split(",");
        if (parts.length != 6) {
            return null;
        }

        String worldName = parts[0];
        double x = Double.parseDouble(parts[1]);
        double y = Double.parseDouble(parts[2]);
        double z = Double.parseDouble(parts[3]);
        float yaw = Float.parseFloat(parts[4]);
        float pitch = Float.parseFloat(parts[5]);

        return new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch);
    }
}