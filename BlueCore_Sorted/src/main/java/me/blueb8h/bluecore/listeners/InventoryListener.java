package me.blueb8h.bluecore.listeners;

import me.blueb8h.bluecore.BlueCore;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryListener implements Listener {
    private final BlueCore plugin;
    
    public InventoryListener(BlueCore plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        
        if (event.getCurrentItem() == null) return;
        
        // Check if it's a shop inventory
        String title = event.getView().title().toString();
        if (title.contains("Marketplace") || title.contains("Shop")) {
            event.setCancelled(true);
            
            boolean isShiftClick = event.isShiftClick();
            boolean isRightClick = event.isRightClick();
            ItemStack clickedItem = event.getCurrentItem();
            
            plugin.getShopGUI().handleClick(player, clickedItem, isShiftClick, isRightClick);
        }
    }
    
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        // Optional: Play sound when closing shop
        if (event.getView().title().toString().contains("Marketplace")) {
            Player player = (Player) event.getPlayer();
            player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_CHEST_CLOSE, 0.5f, 1.0f);
        }
    }
}