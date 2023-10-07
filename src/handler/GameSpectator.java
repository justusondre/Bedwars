package handler;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import bedwars.Main;
import net.md_5.bungee.api.ChatColor;

public class GameSpectator implements Listener {
	
	private Player player;
	private int currentPage = 0;

    public GameSpectator(Player player) {
        this.player = player;
        player.getInventory().clear();
        makeInvisible();
        disableInteractions();
        giveCompass();
        giveLeaveBed();
        openSpectateGUI(player);
    }

    public void makeInvisible() {
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1));
    }

    public void disableInteractions() {
    	
    }

    public void giveCompass() {
        ItemStack compass = new ItemStack(Material.COMPASS);
        ItemMeta compassMeta = compass.getItemMeta();
        compassMeta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Spectate Players");
        compass.setItemMeta(compassMeta);
        player.getInventory().setItem(0, compass);
    }

    public void giveLeaveBed() {
        ItemStack leaveBed = new ItemStack(Material.RED_BED);
        ItemMeta bedMeta = leaveBed.getItemMeta();
        bedMeta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "Return to Lobby");
        leaveBed.setItemMeta(bedMeta);

        player.getInventory().setItem(8, leaveBed);
    }

    public void startLeavingCountdown(int seconds) {
        Game game = Main.getInstance().getGame();
        new BukkitRunnable() {
            int countdown = seconds;

            @Override
            public void run() {
                if (countdown <= 0) {
                    player.sendMessage("You are leaving the arena.");
                    cancel();
                    game.leaveArena(player);
                } else {
                    player.sendMessage("Leaving in " + countdown + " seconds...");
                    countdown--;
                }
            }
        }.runTaskTimer(Main.getInstance(), 0, 20);
    }
    
	private void openSpectateGUI(Player player) {
	    Inventory gui = Bukkit.createInventory(null, 6 * 9, "Spectate Menu (Page " + (currentPage + 1) + ")");

	    Game game = Main.getInstance().getGame();
	    List<Player> alivePlayers = game.getAlivePlayersInArena(game.getArenaName(player)); // Use getArenaName here

	    int[] slotsToPopulate = {10, 11, 12, 13, 14, 15, 16,
	                             19, 20, 21, 22, 23, 24, 25,
	                             28, 29, 30, 31, 32, 33, 34};

	    for (int i = 0; i < slotsToPopulate.length; i++) {
	        int slot = slotsToPopulate[i];
	        if (i < alivePlayers.size()) {
	            Player onlinePlayer = alivePlayers.get(i);

	            ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
	            SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
	            skullMeta.setOwningPlayer(onlinePlayer);
	            skullMeta.setDisplayName(ChatColor.YELLOW + "Spectate " + onlinePlayer.getName()); // Set display name
	            skull.setItemMeta(skullMeta);
	            gui.setItem(slot, skull);
	        } else {
	            gui.setItem(slot, new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE));
	        }
	    }

        // Add arrow heads
        ItemStack arrowLeft = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta arrowLeftMeta = (SkullMeta) arrowLeft.getItemMeta();
        arrowLeftMeta.setOwningPlayer(Bukkit.getOfflinePlayer("MHF_ArrowLeft"));
        arrowLeftMeta.setDisplayName(ChatColor.GREEN + "Previous Page");
        arrowLeft.setItemMeta(arrowLeftMeta);
        gui.setItem(47, arrowLeft);

        ItemStack arrowRight = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta arrowRightMeta = (SkullMeta) arrowRight.getItemMeta();
        arrowRightMeta.setOwningPlayer(Bukkit.getOfflinePlayer("MHF_ArrowRight"));
        arrowRightMeta.setDisplayName(ChatColor.GREEN + "Next Page");
        arrowRight.setItemMeta(arrowRightMeta);
        gui.setItem(51, arrowRight);

        // Add close button
        ItemStack closeBarrier = new ItemStack(Material.BARRIER);
        ItemMeta closeBarrierMeta = closeBarrier.getItemMeta();
        closeBarrierMeta.setDisplayName(ChatColor.RED + "Close Menu");
        closeBarrier.setItemMeta(closeBarrierMeta);
        gui.setItem(49, closeBarrier);

        player.openInventory(gui);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().startsWith("Spectate Menu")) {
            event.setCancelled(true);

            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();

            if (clickedItem != null) {
                if (clickedItem.getType() == Material.BARRIER) {
                    // Implement close button logic
                    player.closeInventory();
                } else if (clickedItem.getType() == Material.PLAYER_HEAD) {
                    if (clickedItem.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Previous Page")) {
                        if (currentPage > 0) {
                            currentPage--;
                            openSpectateGUI(player);
                        }
                        
                    } else if (clickedItem.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Next Page")) {
                        int totalPages = (int) Math.ceil((double) Bukkit.getOnlinePlayers().size() / 21);
                        if (currentPage < totalPages - 1) {
                            currentPage++;
                            openSpectateGUI(player);
                        }
                    }
                }
            }
        }
    }
}