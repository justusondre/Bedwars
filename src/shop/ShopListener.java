package shop;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ShopListener implements Listener {
	
	@EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory clickedInventory = event.getClickedInventory();
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedInventory != null && event.getView().getTitle().equalsIgnoreCase("Bedwars Shop")) {
            event.setCancelled(true); // Prevent players from taking items from the shop inventory

            if (clickedItem != null && clickedItem.getType() != Material.AIR) {
                // Check if the clicked item has lore (currency and cost information)
                ItemMeta itemMeta = clickedItem.getItemMeta();
                if (itemMeta != null && itemMeta.hasLore()) {
                    // Parse currency and cost from lore
                    String loreLine = itemMeta.getLore().get(0);
                    String[] loreParts = loreLine.split(": ");
                    if (loreParts.length == 2) {
                        String currency = loreParts[1];
                        int cost = Integer.parseInt(itemMeta.getLore().get(1).split(": ")[1]);

                        // Check if the player has enough currency
                        int currencyBalance = ShopCurrency.getInstance().getCurrencyBalance(player, currency);
                        if (currencyBalance >= cost) {
                            // Deduct the currency
                            boolean success = ShopCurrency.getInstance().deductCurrency(player, currency, cost);
                            if (success) {
                                // Give the player the item
                                player.getInventory().addItem(clickedItem);
                                player.updateInventory();
                                player.sendMessage("You purchased " + clickedItem.getType() + " for " + cost + " " + currency);
                            } else {
                                player.sendMessage("Failed to purchase " + clickedItem.getType() + ". Insufficient currency.");
                            }
                        } else {
                            player.sendMessage("Failed to purchase " + clickedItem.getType() + ". Insufficient " + currency);
                        }
                    }
                }
            }
        }
    }
}