package shop;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.md_5.bungee.api.ChatColor;

public class ShopItem {
	
	public static ItemStack[] getPageItems(int pageNumber) {
	    switch (pageNumber) {
	        case 0:
	            return new ItemStack[]{
	                createShopItem(Material.NETHER_STAR, 1, "Nether Star", "GOLD", 10),
	                createShopItem(Material.WHITE_WOOL, 16, "Wool", "GOLD", 10),
	                createShopItem(Material.GOLDEN_SWORD, 1, "Golden Sword", "GOLD", 10),
	                createShopItem(Material.CHAINMAIL_BOOTS, 1, "Chainmail Boots", "GOLD", 10),
	                createShopItem(Material.STONE_PICKAXE, 1, "Stone Pickaxe", "GOLD", 10),
	                createShopItem(Material.BOW, 1, "Bow", "GOLD", 10),
	                createShopItem(Material.BREWING_STAND, 1, "Brewing Stand", "GOLD", 10),
	                createShopItem(Material.TNT, 1, "TNT", "GOLD", 10)
	            };
	        case 1:
	            return new ItemStack[]{
	                createShopItem(Material.WHITE_WOOL, 16, "White Wool", "IRON", 4),
	                createShopItem(Material.WHITE_TERRACOTTA, 1, "White Terracotta", "GOLD", 10),
	                createShopItem(Material.GLASS, 1, "Glass", "GOLD", 10),
	                createShopItem(Material.END_STONE, 1, "End Stone", "GOLD", 10),
	                createShopItem(Material.LADDER, 1, "Ladder", "GOLD", 10),
	                createShopItem(Material.OAK_PLANKS, 1, "Oak Planks", "GOLD", 10),
	                createShopItem(Material.OBSIDIAN, 4, "Obsidian", "ERMALD", 4)
	            };
	        case 2:
	            return new ItemStack[]{
	                createShopItem(Material.STONE_SWORD, 1, ChatColor.YELLOW + "Stone Sword", "IRON", 10),
	                createShopItem(Material.IRON_SWORD, 1, ChatColor.YELLOW + "Iron Sword", "GOLD", 7),
	                createShopItem(Material.DIAMOND_SWORD, 1, ChatColor.YELLOW + "Diamond Sword", "EMERALD", 4),
	                createShopItem(Material.STICK, 1, ChatColor.LIGHT_PURPLE + "Stick", "GOLD", 10)
	            };
	        case 3:
	            return new ItemStack[]{
	                createShopItem(Material.CHAINMAIL_BOOTS, 1, "Chainmail Boots", "GOLD", 10),
	                createShopItem(Material.IRON_BOOTS, 1, "Iron Boots", "GOLD", 10),
	                createShopItem(Material.DIAMOND_BOOTS, 1, "Diamond Boots", "GOLD", 10)
	            };
	        case 4:
	            return new ItemStack[]{
	                createShopItem(Material.SHEARS, 1, "Shears", "GOLD", 10),
	                createShopItem(Material.WOODEN_PICKAXE, 1, "Wooden Pickaxe", "GOLD", 10),
	                createShopItem(Material.WOODEN_AXE, 1, "Wooden Axe", "GOLD", 10)
	            };
	        case 5:
	            return new ItemStack[]{
	                createShopItem(Material.BOW, 1, "Bow", "GOLD", 10),
	                createShopItem(Material.ARROW, 1, "Arrow", "GOLD", 10),
	                createShopItem(Material.ARROW, 1, "Arrow", "GOLD", 10),
	                createShopItem(Material.ARROW, 1, "Arrow", "GOLD", 10)
	            };
	        case 6:
	            return new ItemStack[]{
	                createShopItem(Material.POTION, 1, "Potion", "GOLD", 10),
	                createShopItem(Material.POTION, 1, "Potion", "GOLD", 10),
	                createShopItem(Material.POTION, 1, "Potion", "GOLD", 10)
	            };
	        case 7:
	            return new ItemStack[]{
	                createShopItem(Material.GOLDEN_APPLE, 1, "Golden Apple", "GOLD", 10),
	                createShopItem(Material.SNOWBALL, 1, "Snowball", "GOLD", 10),
	                createShopItem(Material.FIRE_CHARGE, 1, "Fire Charge", "GOLD", 10),
	                createShopItem(Material.TNT, 1, "TNT", "GOLD", 10),
	                createShopItem(Material.ENDER_PEARL, 1, "Ender Pearl", "GOLD", 10),
	                createShopItem(Material.WATER_BUCKET, 1, "Water Bucket", "GOLD", 10),
	                createShopItem(Material.EGG, 1, "Egg", "GOLD", 10),
	                createShopItem(Material.MILK_BUCKET, 1, "Milk Bucket", "GOLD", 10),
	                createShopItem(Material.SPONGE, 1, "Sponge", "GOLD", 10),
	                createShopItem(Material.IRON_GOLEM_SPAWN_EGG, 1, "Iron Golem Spawn Egg", "GOLD", 10),
	                createShopItem(Material.COMPASS, 1, "Compass", "GOLD", 10)
	            };
	        default:
	            return new ItemStack[8]; // Empty items for other pages
	    }
	}

	private static ItemStack createShopItem(Material material, int stackSize, String displayName, String currency, int cost) {
	    ItemStack item = new ItemStack(material);
	    item.setAmount(stackSize); // Set the stack size
	    ItemMeta meta = item.getItemMeta();
	    
	    // Set the display name if provided
	    if (displayName != null && !displayName.isEmpty()) {
	        meta.setDisplayName(displayName);
	    } else {
	        String defaultDisplayName = material.toString() + " x" + stackSize; // Default display name
	        meta.setDisplayName(defaultDisplayName);
	    }

	    // Set lore to display currency and cost
	    meta.setLore(List.of("Currency: " + currency, "Cost: " + cost));
	    item.setItemMeta(meta);
	    return item;
	}
}