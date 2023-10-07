package command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;

import bedwars.Main;
import shop.ShopInventory;

public class SetShop implements CommandExecutor {
	
	ShopInventory shopInventory = Main.getInstance().getShopInventory();

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            shopInventory.openShopPage(player, 1);

            if (player.getTargetBlockExact(5) != null) {
       
            }

        }
        return false;
    }
}