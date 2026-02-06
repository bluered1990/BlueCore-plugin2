package me.blueb8h.bluecore.commands;

import me.blueb8h.bluecore.BlueCore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShopCommand implements CommandExecutor {
    private final BlueCore plugin;
    
    public ShopCommand(BlueCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("This command can only be used by players!", NamedTextColor.RED));
            return true;
        }
        
        if (!player.hasPermission("bluecore.shop")) {
            player.sendMessage(Component.text("You don't have permission to use the shop!", NamedTextColor.RED));
            return true;
        }
        
        plugin.getShopGUI().openShop(player);
        return true;
    }
}