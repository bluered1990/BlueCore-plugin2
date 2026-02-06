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

public class RankCommand implements CommandExecutor, TabCompleter {
    private final BlueCore plugin;
    
    public RankCommand(BlueCore plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "set":
                return handleSet(sender, args);
            case "get":
                return handleGet(sender, args);
            default:
                sendHelp(sender);
                return true;
        }
    }
    
    private boolean handleSet(CommandSender sender, String[] args) {
        if (!sender.hasPermission("bluecore.rank.set")) {
            sender.sendMessage(Component.text("You don't have permission to set ranks!", NamedTextColor.RED));
            return true;
        }
        
        if (args.length != 3) {
            sender.sendMessage(Component.text("Usage: /rank set <player> <rank>", NamedTextColor.YELLOW));
            return true;
        }
        
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(Component.text("Player not found!", NamedTextColor.RED));
            return true;
        }
        
        String rankId = args[2];
        if (plugin.getRankManager().getRank(rankId) == null) {
            sender.sendMessage(Component.text("Rank not found! Available ranks: " + 
                String.join(", ", plugin.getRankManager().getRankIds()), NamedTextColor.RED));
            return true;
        }
        
        boolean success = plugin.getRankManager().setPlayerRank(target.getUniqueId(), rankId);
        if (success) {
            sender.sendMessage(Component.text("Set " + target.getName() + "'s rank to " + rankId, NamedTextColor.GREEN));
            target.sendMessage(Component.text("Your rank has been set to " + rankId, NamedTextColor.GREEN));
        } else {
            sender.sendMessage(Component.text("Failed to set rank!", NamedTextColor.RED));
        }
        
        return true;
    }
    
    private boolean handleGet(CommandSender sender, String[] args) {
        if (!sender.hasPermission("bluecore.rank.get")) {
            sender.sendMessage(Component.text("You don't have permission to view ranks!", NamedTextColor.RED));
            return true;
        }
        
        if (args.length != 2) {
            sender.sendMessage(Component.text("Usage: /rank get <player>", NamedTextColor.YELLOW));
            return true;
        }
        
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(Component.text("Player not found!", NamedTextColor.RED));
            return true;
        }
        
        String rankId = plugin.getRankManager().getPlayerRank(target.getUniqueId()).getId();
        sender.sendMessage(Component.text(target.getName() + "'s rank: " + rankId, NamedTextColor.GREEN));
        return true;
    }
    
    private void sendHelp(CommandSender sender) {
        sender.sendMessage(Component.text("Rank Commands:", NamedTextColor.GOLD));
        sender.sendMessage(Component.text("/rank set <player> <rank> - Set player rank", NamedTextColor.YELLOW));
        sender.sendMessage(Component.text("/rank get <player> - Get player rank", NamedTextColor.YELLOW));
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            completions.add("set");
            completions.add("get");
        } else if (args.length == 2) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                    completions.add(player.getName());
                }
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("set")) {
            for (String rankId : plugin.getRankManager().getRankIds()) {
                if (rankId.toLowerCase().startsWith(args[2].toLowerCase())) {
                    completions.add(rankId);
                }
            }
        }
        
        return completions;
    }
}