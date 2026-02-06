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

public class PayCommand implements CommandExecutor, TabCompleter {
    private final BlueCore plugin;
    
    public PayCommand(BlueCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("This command can only be used by players!", NamedTextColor.RED));
            return true;
        }
        
        if (!player.hasPermission("bluecore.pay")) {
            player.sendMessage(Component.text("You don't have permission to use this command!", NamedTextColor.RED));
            return true;
        }
        
        if (args.length != 2) {
            player.sendMessage(Component.text("Usage: /pay <player> <amount>", NamedTextColor.YELLOW));
            return true;
        }
        
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(Component.text("Player not found!", NamedTextColor.RED));
            return true;
        }
        
        if (target.equals(player)) {
            player.sendMessage(Component.text("You cannot pay yourself!", NamedTextColor.RED));
            return true;
        }
        
        double amount;
        try {
            amount = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage(Component.text("Invalid amount!", NamedTextColor.RED));
            return true;
        }
        
        if (amount <= 0) {
            player.sendMessage(Component.text("Amount must be positive!", NamedTextColor.RED));
            return true;
        }
        
        if (amount > plugin.getEconomyManager().getMaxBalance()) {
            player.sendMessage(Component.text("Amount exceeds maximum balance!", NamedTextColor.RED));
            return true;
        }
        
        boolean success = plugin.getEconomyManager().transfer(player.getUniqueId(), target.getUniqueId(), amount);
        
        if (success) {
            String currency = plugin.getEconomyManager().getCurrencySymbol();
            player.sendMessage(Component.text("You paid " + target.getName() + " " + currency + amount, NamedTextColor.GREEN));
            target.sendMessage(Component.text(player.getName() + " paid you " + currency + amount, NamedTextColor.GREEN));
        } else {
            player.sendMessage(Component.text("Insufficient balance or transfer failed!", NamedTextColor.RED));
        }
        
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!player.equals(sender) && player.getName().toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(player.getName());
                }
            }
        }
        
        return completions;
    }
}