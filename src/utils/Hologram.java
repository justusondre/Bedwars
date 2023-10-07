package utils;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;

public class Hologram {
	
	public static ArmorStand spawnHologram(Location location, String text) {
        ArmorStand armorStand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        armorStand.setVisible(false);
        armorStand.setCustomNameVisible(true);
        armorStand.setCustomName(text);
        armorStand.setGravity(false);
        armorStand.setSmall(true);
        armorStand.setMarker(true); // Ensures the armor stand is invisible when looked at
        armorStand.setBasePlate(false); // Remove base plate

        // Customize appearance
        armorStand.setArms(false);
        armorStand.setCanPickupItems(false);
        armorStand.setCustomNameVisible(true);

        return armorStand;
    }

    public static void updateHologram(ArmorStand armorStand, String text) {
        armorStand.setCustomName(text);
    }

    public static void removeHologram(ArmorStand armorStand) {
        armorStand.remove();
    }
}