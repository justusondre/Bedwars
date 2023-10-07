package shop;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ShopCurrency {
    private static ShopCurrency instance;

    private ShopCurrency() {
        // Private constructor to ensure a single instance.
    }

    public static ShopCurrency getInstance() {
        if (instance == null) {
            instance = new ShopCurrency();
        }
        return instance;
    }

    public int getCurrencyBalance(Player player, String currencyType) {
        Material material = getCurrencyMaterial(currencyType);
        if (material == null) {
            return 0; // Invalid currency type
        }
        return countItemsInInventory(player.getInventory(), material);
    }

    public boolean deductCurrency(Player player, String currencyType, int amount) {
        Material material = getCurrencyMaterial(currencyType);
        if (material == null) {
            return false; // Invalid currency type
        }
        return removeItemsFromInventory(player.getInventory(), material, amount);
    }

    // Helper method to get the Material corresponding to a currency type
    private Material getCurrencyMaterial(String currencyType) {
        switch (currencyType) {
            case "IRON":
                return Material.IRON_INGOT;
            case "GOLD":
                return Material.GOLD_INGOT;
            case "EMERALD":
                return Material.EMERALD;
            default:
                return null; // Invalid currency type
        }
    }

    // Helper method to count the number of specific items in an inventory
    private int countItemsInInventory(Inventory inventory, Material material) {
        int count = 0;
        ItemStack[] contents = inventory.getContents();
        for (ItemStack item : contents) {
            if (item != null && item.getType() == material) {
                count += item.getAmount();
            }
        }
        return count;
    }

    // Helper method to remove a specific number of items from an inventory
    private boolean removeItemsFromInventory(Inventory inventory, Material material, int amount) {
        ItemStack[] contents = inventory.getContents();
        for (int i = 0; i < contents.length && amount > 0; i++) {
            ItemStack item = contents[i];
            if (item != null && item.getType() == material) {
                int itemAmount = item.getAmount();
                if (itemAmount <= amount) {
                    inventory.setItem(i, null);
                    amount -= itemAmount;
                } else {
                    item.setAmount(itemAmount - amount);
                    inventory.setItem(i, item);
                    amount = 0;
                }
            }
        }
        return amount == 0;
    }
}