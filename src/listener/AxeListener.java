package listener;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import bedwars.Main;
import utils.Cooldown;

public class AxeListener implements Listener {

	ArrayList<ArmorStand> axes = new ArrayList<ArmorStand>();
    Cooldown cooldownManager = new Cooldown(10);

    @EventHandler
    public void noManipulate(PlayerArmorStandManipulateEvent event) {
        if (axes.contains(event.getRightClicked()))
            event.setCancelled(true);
    }

    @EventHandler
    public void axeThrow(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack heldItem = event.getItem();

        // Check for cooldown only if the event is a right-click and the held item is an axe
        if (event.getAction() == Action.RIGHT_CLICK_AIR && heldItem != null && isAxe(heldItem)) {
            // Check for cooldown
            if (cooldownManager.isCooldownActive(player.getName())) {
                long currentTime = System.currentTimeMillis();
                long cooldownTime = cooldownManager.getCooldownTime(player.getName());
                long remainingCooldown = (cooldownTime + cooldownManager.getCooldownMillis()) - currentTime;

                if (remainingCooldown > 0) {
                    int secondsLeft = (int) (remainingCooldown / 1000);
                    player.sendMessage(ChatColor.RED + "You must wait " + secondsLeft + " seconds before throwing another axe.");
                    return; // Exit early to avoid spamming the message
                }
            }

            Vector v = player.getLocation().add(player.getLocation().getDirection().multiply(10)).toVector()
                    .subtract(player.getLocation().toVector()).normalize();
            player.getInventory().removeItem(heldItem);
            axe((Entity) player, v, heldItem, player);

            // Set cooldown for the player
            cooldownManager.setCooldown(player.getName());
        }
    }

    // Helper method to check if an ItemStack is an axe
    private boolean isAxe(ItemStack item) {
        Material itemType = item.getType();
        return itemType == Material.WOODEN_AXE || itemType == Material.STONE_AXE
                || itemType == Material.GOLDEN_AXE || itemType == Material.IRON_AXE
                || itemType == Material.DIAMOND_AXE;
    }

    @EventHandler
    public void axeThrow2(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        ItemStack heldItem = player.getInventory().getItemInMainHand();

        if (heldItem != null && isAxe(heldItem)) {
            Vector v = player.getLocation().add(player.getLocation().getDirection().multiply(10)).toVector()
                    .subtract(player.getLocation().toVector()).normalize();

            // Check if the player is on cooldown
            if (cooldownManager.isCooldownActive(player.getName())) {
                long currentTime = System.currentTimeMillis();
                long cooldownTime = cooldownManager.getCooldownTime(player.getName());
                long remainingCooldown = (cooldownTime + cooldownManager.getCooldownMillis()) - currentTime;

                if (remainingCooldown > 0) {
                    int secondsLeft = (int) (remainingCooldown / 1000);
                    player.sendMessage(ChatColor.RED + "You must wait " + secondsLeft + " seconds before throwing another axe.");
                    return; // Exit early to avoid spamming the message
                }
            }

            // Set cooldown for the player only when they interact with a mob
            Entity clickedEntity = event.getRightClicked();
            if (clickedEntity instanceof Damageable) {
                cooldownManager.setCooldown(player.getName());
                axe((Entity) player, v, heldItem, player);
                player.getInventory().removeItem(heldItem);
            }
        }
    }

    @SuppressWarnings("deprecation")
	public void axe(Entity e, Vector v, ItemStack m, Player p) {
        Location to = new Location(e.getWorld(), e.getLocation().getX(), e.getLocation().getY() + 1.0D,
                e.getLocation().getZ());
        ArmorStand a = (ArmorStand) e.getWorld().spawnEntity(to, EntityType.ARMOR_STAND);
        a.setVisible(false);
        a.setSmall(true);
        a.setItemInHand(m);
        axes.add(a);
        axe2(e, a, v.multiply(1.2D), 0, m, p);
    }

    public void axe2(final Entity ef, final ArmorStand a, final Vector v, final int recurse, final ItemStack m, final Player p) {
        if (recurse < 300) {
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable() {
                public void run() {
                    for (Entity e : a.getNearbyEntities(1.0D, 1.0D, 1.0D)) {
                        if (e != ef && e != a && e instanceof Damageable) {
                            if (m.getType() == Material.WOODEN_AXE)
                                ((Damageable) e).damage(2.0D);
                            if (m.getType() == Material.STONE_AXE)
                                ((Damageable) e).damage(5.0D);
                            if (m.getType() == Material.GOLDEN_AXE)
                                ((Damageable) e).damage(6.0D);
                            if (m.getType() == Material.IRON_AXE)
                                ((Damageable) e).damage(8.0D);
                            if (m.getType() == Material.DIAMOND_AXE)
                                ((Damageable) e).damage(10.0D);
                            if (m.hasItemMeta() && m.getItemMeta().hasEnchant(Enchantment.FIRE_ASPECT))
                                ((Damageable) e).setFireTicks(20);
                            axes.remove(a);
                            a.remove();
                            p.getInventory().addItem(m);
                            break;
                        }
                    }
                    double x = a.getRightArmPose().getX();
                    a.setRightArmPose(new EulerAngle(x + 0.6D, 0.0D, 0.0D));
                    Vector vec = new Vector(v.getX(), v.getY() - 0.03D, v.getZ());
                    a.setVelocity(vec);
                    if (!a.isDead() && a.isOnGround()) {
                        axes.remove(a);
                        a.remove();
                        p.getInventory().addItem(m);
                    } else if (!a.isDead()) {
                        axe2(ef, a, vec, recurse + 1, m, p);
                    }
                }
            }, 2L);
        } else {
            axes.remove(a);
            a.remove();
            p.getInventory().addItem(m);
        }
    }
}