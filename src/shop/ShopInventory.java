package shop;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ShopInventory implements Listener {

	private Inventory[] shopPages = new Inventory[8];

    private final String shopTitle = "Bedwars Shop";
    private final int shopSize = 54; // Inventory size for each page
    private final int totalPages = 8;

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory clickedInventory = event.getClickedInventory();

        if (clickedInventory != null && event.getView().getTitle().equalsIgnoreCase(shopTitle)) {
            event.setCancelled(true); // Prevent players from taking items from the shop inventory

            // Check if the player clicked an item representing a page
            int clickedSlot = event.getSlot();
            if (clickedSlot >= 0 && clickedSlot < totalPages) {
                openShopPage(player, clickedSlot);
            }
        }
    }

    public void openShopPage(Player player, int page) {
        Inventory shopPage = Bukkit.createInventory(player, shopSize, shopTitle);

        // Fill the top two rows with static items
        for (int i = 0; i < 18; i++) {
            // Replace these items with your desired static items
            ItemStack staticItem = createStaticShopItem(i);
            shopPage.setItem(i, staticItem);
        }

        // Fill slots 9 through 17 with gray stained glass panes
        for (int i = 9; i <= 17; i++) {
            ItemStack grayGlassPane = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
            shopPage.setItem(i, grayGlassPane);
        }

        // Set the pane corresponding to the current page to green
        ItemStack greenGlassPane = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        shopPage.setItem(9 + page, greenGlassPane);

        // Fill the bottom row (inventory slots 19 to 43) based on the page number
        ItemStack[] items = ShopItem.getPageItems(page);

        int itemIndex = 0;
        for (int i = 19; i <= 43; i++) {
            // Check if the current slot is an edge slot
            if (i % 9 == 0 || i % 9 == 8) {
                continue; // Skip edge slots
            }

            // Populate the slot with the item or AIR if there are no more items
            if (itemIndex < items.length) {
                shopPage.setItem(i, items[itemIndex]);
                itemIndex++;
            } else {
                shopPage.setItem(i, new ItemStack(Material.AIR));
            }
        }

        player.openInventory(shopPage);
    }

    private ItemStack createStaticShopItem(int itemNumber) {
        // Create and return an ItemStack for the static items in the top two rows
        // You can customize this method to create different static items
        ItemStack itemStack = null;
        String displayName = null;

        switch (itemNumber) {
            case 0:
                itemStack = new ItemStack(Material.NETHER_STAR);
                displayName = "Quick Buy";
                break;
            case 1:
                itemStack = new ItemStack(Material.WHITE_WOOL);
                displayName = "Blocks";
                break;
            case 2:
                itemStack = new ItemStack(Material.GOLDEN_SWORD);
                displayName = "Weapons";
                break;
            case 3:
                itemStack = new ItemStack(Material.CHAINMAIL_BOOTS);
                displayName = "Armour";
                break;
            case 4:
                itemStack = new ItemStack(Material.STONE_PICKAXE);
                displayName = "Tools";
                break;
            case 5:
                itemStack = new ItemStack(Material.BOW);
                displayName = "Archery";
                break;
            case 6:
                itemStack = new ItemStack(Material.BREWING_STAND);
                displayName = "Potions";
                break;
            case 7:
                itemStack = new ItemStack(Material.TNT);
                displayName = "Misc";
                break;
        }

        if (itemStack != null) {
            ItemMeta itemMeta = itemStack.getItemMeta();

            if (displayName != null) {
                itemMeta.setDisplayName(displayName);
            }

            itemStack.setItemMeta(itemMeta);
        }

        return itemStack != null ? itemStack : new ItemStack(Material.AIR);
    }

	public Inventory[] getShopPages() {
		return shopPages;
	}
}