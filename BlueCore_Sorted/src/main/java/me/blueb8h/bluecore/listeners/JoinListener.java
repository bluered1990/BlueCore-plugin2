package me.blueb8h.bluecore.listeners;

import me.blueb8h.bluecore.BlueCore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinListener implements Listener {
    private final BlueCore plugin;
    
    public JoinListener(BlueCore plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // Initialize economy data if needed
        plugin.getEconomyManager().getBalance(player.getUniqueId());
        
        // Update player rank display
        plugin.getRankManager().updatePlayerRank(player);
        
        // Send welcome message with balance
        double balance = plugin.getEconomyManager().getBalance(player.getUniqueId());
        String currency = plugin.getEconomyManager().getCurrencySymbol();
        player.sendMessage(Component.text(
            "Welcome! Your balance: " + currency + balance,
            NamedTextColor.GREEN
        ));
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Save player data if needed
        plugin.getEconomyManager().save();
        plugin.getRankManager().save();
    }
}