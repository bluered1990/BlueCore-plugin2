package me.blueb8h.bluecore.commands;

import me.blueb8h.bluecore.BlueCore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class BalanceCommand implements CommandExecutor, TabCompleter {
    private final BlueCore plugin;
    
    public BalanceCommand(BlueCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player) && args.length == 0) {
            sender.sendMessage(Component.text("Console must specify a player: /bal <player>", NamedTextColor.RED));
            return true;
        }
        
        if (args.length == 0) {
            // Check own balance
            if (!(sender instanceof Player playerCmd)) {
                sender.sendMessage(Component.text("You must be a player to check your own balance!", NamedTextColor.RED));
                return true;
            }
            
            double balance = plugin.getEconomyManager().getBalance(playerCmd.getUniqueId());
            playerCmd.sendMessage(Component.text("Your balance: " + plugin.getEconomyManager().getCurrencySymbol() + balance, NamedTextColor.GREEN));
            return true;
        }
        
        if (args.length == 1) {
            // Check other player's balance
            if (!sender.hasPermission("bluecore.balance.others")) {
                sender.sendMessage(Component.text("You don't have permission to check others' balance!", NamedTextColor.RED));
                return true;
            }
            
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(Component.text("Player not found!", NamedTextColor.RED));
                return true;
            }
            
            double balance = plugin.getEconomyManager().getBalance(target.getUniqueId());
            sender.sendMessage(Component.text(target.getName() + "'s balance: " + plugin.getEconomyManager().getCurrencySymbol() + balance, NamedTextColor.GREEN));
            return true;
        }
        
        // Show help
        sender.sendMessage(Component.text("Usage: /bal [player]", NamedTextColor.YELLOW));
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1 && sender.hasPermission("bluecore.balance.others")) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(player.getName());
                }
            }
        }
        
        return completions;
    }
}