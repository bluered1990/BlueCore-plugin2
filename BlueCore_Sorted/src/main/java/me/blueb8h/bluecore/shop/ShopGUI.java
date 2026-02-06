package me.blueb8h.bluecore.shop;

import me.blueb8h.bluecore.BlueCore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class ShopGUI {
    private final BlueCore plugin;
    private final Map<String, ShopCategory> categories = new HashMap<>();
    private String shopName;
    private int rows;
    
    public ShopGUI(BlueCore plugin) {
        this.plugin = plugin;
        loadConfig();
    }
    
    public void loadConfig() {
        File configFile = new File(plugin.getDataFolder(), "shops/default.yml");
        if (!configFile.exists()) {
            plugin.saveResource("shops/default.yml", false);
        }
        
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        categories.clear();
        
        shopName = config.getString("shop-name", "&8&lMarketplace");
        rows = config.getInt("rows", 6);
        
        if (config.contains("categories")) {
            for (String categoryId : config.getConfigurationSection("categories").getKeys(false)) {
                ShopCategory category = new ShopCategory(categoryId);
                category.setName(config.getString("categories." + categoryId + ".name", "&fCategory"));
                category.setSlot(config.getInt("categories." + categoryId + ".slot", 0));
                category.setMaterial(config.getString("categories." + categoryId + ".material", "CHEST"));
                
                if (config.contains("categories." + categoryId + ".items")) {
                    for (String itemId : config.getConfigurationSection("categories." + categoryId + ".items").getKeys(false)) {
                        ShopItem item = new ShopItem(itemId);
                        item.setMaterial(config.getString("categories." + categoryId + ".items." + itemId + ".material", itemId));
                        item.setBuyPrice(config.getDouble("categories." + categoryId + ".items." + itemId + ".buy", 0));
                        item.setSellPrice(config.getDouble("categories." + categoryId + ".items." + itemId + ".sell", 0));
                        item.setName(config.getString("categories." + categoryId + ".items." + itemId + ".name", formatMaterialName(itemId)));
                        
                        List<String> lore = new ArrayList<>();
                        if (config.contains("categories." + categoryId + ".items." + itemId + ".lore")) {
                            lore = config.getStringList("categories." + categoryId + ".items." + itemId + ".lore");
                        } else {
                            lore.add("&7Buy: &a" + plugin.getEconomyManager().getCurrencySymbol() + item.getBuyPrice());
                            lore.add("&7Sell: &c" + plugin.getEconomyManager().getCurrencySymbol() + item.getSellPrice());
                            lore.add("");
                            lore.add("&eLeft-click to buy");
                            lore.add("&eRight-click to sell");
                            lore.add("&eShift-click for stack!");
                        }
                        item.setLore(lore);
                        
                        item.setEnchanted(config.getBoolean("categories." + categoryId + ".items." + itemId + ".enchant", false));
                        category.addItem(item);
                    }
                }
                
                categories.put(categoryId, category);
            }
        }
    }
    
    public void openShop(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 9, LegacyComponentSerializer.legacyAmpersand().deserialize(shopName));
        
        // Add category items
        for (ShopCategory category : categories.values()) {
            Material material = Material.getMaterial(category.getMaterial());
            if (material == null) material = Material.CHEST;
            
            ItemStack item = new ItemStack(material);
            ItemMeta meta = item.getItemMeta();
            meta.displayName(LegacyComponentSerializer.legacyAmpersand().deserialize(category.getName()).decoration(TextDecoration.ITALIC, false));
            
            List<Component> lore = new ArrayList<>();
            lore.add(LegacyComponentSerializer.legacyAmpersand().deserialize("&7Click to view items").decoration(TextDecoration.ITALIC, false));
            meta.lore(lore);
            
            item.setItemMeta(meta);
            
            if (category.getSlot() >= 0 && category.getSlot() < 9) {
                inventory.setItem(category.getSlot(), item);
            }
        }
        
        // Fill empty slots with glass panes
        ItemStack filler = createFillerItem();
        for (int i = 0; i < 9; i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, filler);
            }
        }
        
        player.openInventory(inventory);
        player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1.0f, 1.0f);
    }
    
    public void openCategory(Player player, String categoryId) {
        ShopCategory category = categories.get(categoryId);
        if (category == null) return;
        
        Inventory inventory = Bukkit.createInventory(null, rows * 9, 
            LegacyComponentSerializer.legacyAmpersand().deserialize(category.getName()));
        
        int slot = 0;
        for (ShopItem item : category.getItems()) {
            if (slot >= rows * 9) break;
            
            Material material = Material.getMaterial(item.getMaterial());
            if (material == null) material = Material.STONE;
            
            ItemStack itemStack = new ItemStack(material);
            ItemMeta meta = itemStack.getItemMeta();
            meta.displayName(LegacyComponentSerializer.legacyAmpersand().deserialize(item.getName()).decoration(TextDecoration.ITALIC, false));
            
            List<Component> lore = item.getLore().stream()
                .map(line -> LegacyComponentSerializer.legacyAmpersand().deserialize(line).decoration(TextDecoration.ITALIC, false))
                .collect(Collectors.toList());
            meta.lore(lore);
            
            itemStack.setItemMeta(meta);
            
            if (item.isEnchanted()) {
                itemStack.addUnsafeEnchantment(org.bukkit.enchantments.Enchantment.DURABILITY, 1);
            }
            
            inventory.setItem(slot, itemStack);
            slot++;
        }
        
        // Add back button
        ItemStack backButton = new ItemStack(Material.ARROW);
        ItemMeta backMeta = backButton.getItemMeta();
        backMeta.displayName(LegacyComponentSerializer.legacyAmpersand().deserialize("&c← Back to Shop").decoration(TextDecoration.ITALIC, false));
        backButton.setItemMeta(backMeta);
        inventory.setItem(rows * 9 - 5, backButton);
        
        player.openInventory(inventory);
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
    }
    
    public boolean handleClick(Player player, ItemStack clickedItem, boolean isShiftClick, boolean isRightClick) {
        if (clickedItem == null) return false;
        
        String displayName = LegacyComponentSerializer.legacyAmpersand().serialize(clickedItem.getItemMeta().displayName());
        
        // Check if it's a category item
        for (ShopCategory category : categories.values()) {
            if (displayName.contains(category.getName().replace("&", ""))) {
                openCategory(player, category.getId());
                return true;
            }
        }
        
        // Check if it's a back button
        if (displayName.contains("Back to Shop")) {
            openShop(player);
            return true;
        }
        
        // Check if it's a shop item
        for (ShopCategory category : categories.values()) {
            for (ShopItem shopItem : category.getItems()) {
                if (displayName.contains(shopItem.getName().replace("&", ""))) {
                    handleItemTransaction(player, shopItem, isShiftClick, isRightClick);
                    return true;
                }
            }
        }
        
        return false;
    }
    
    private void handleItemTransaction(Player player, ShopItem shopItem, boolean isShiftClick, boolean isRightClick) {
        Material material = Material.getMaterial(shopItem.getMaterial());
        if (material == null) return;
        
        int amount = isShiftClick ? 64 : 1;
        ItemStack item = new ItemStack(material, amount);
        
        if (isRightClick) {
            // Sell
            int playerAmount = countPlayerItems(player, material);
            if (playerAmount == 0) {
                player.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("&cYou don't have any " + formatMaterialName(material.name()) + " to sell!"));
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                return;
            }
            
            if (isShiftClick) {
                amount = Math.min(playerAmount, 64);
            } else {
                amount = Math.min(playerAmount, 1);
            }
            
            double totalPrice = shopItem.getSellPrice() * amount;
            if (totalPrice <= 0) {
                player.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("&cThis item cannot be sold!"));
                return;
            }
            
            // Remove items from inventory
            removeItems(player, material, amount);
            
            // Add money
            plugin.getEconomyManager().addBalance(player.getUniqueId(), totalPrice);
            
            player.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(
                "&aSold " + amount + " " + formatMaterialName(material.name()) + 
                " for " + plugin.getEconomyManager().getCurrencySymbol() + totalPrice));
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
            
        } else {
            // Buy
            double totalPrice = shopItem.getBuyPrice() * amount;
            if (totalPrice <= 0) {
                player.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("&cThis item cannot be bought!"));
                return;
            }
            
            if (!plugin.getEconomyManager().hasBalance(player.getUniqueId(), totalPrice)) {
                player.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("&cInsufficient funds!"));
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                return;
            }
            
            // Check inventory space
            if (!hasInventorySpace(player, item)) {
                player.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("&cNot enough inventory space!"));
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                return;
            }
            
            // Remove money
            plugin.getEconomyManager().removeBalance(player.getUniqueId(), totalPrice);
            
            // Give item
            player.getInventory().addItem(new ItemStack(material, amount));
            
            player.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(
                "&aBought " + amount + " " + formatMaterialName(material.name()) + 
                " for " + plugin.getEconomyManager().getCurrencySymbol() + totalPrice));
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
        }
    }
    
    private int countPlayerItems(Player player, Material material) {
        int count = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == material) {
                count += item.getAmount();
            }
        }
        return count;
    }
    
    private void removeItems(Player player, Material material, int amount) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == material) {
                if (item.getAmount() > amount) {
                    item.setAmount(item.getAmount() - amount);
                    return;
                } else {
                    amount -= item.getAmount();
                    player.getInventory().remove(item);
                    if (amount <= 0) return;
                }
            }
        }
    }
    
    private boolean hasInventorySpace(Player player, ItemStack item) {
        Inventory inv = player.getInventory();
        int freeSpace = 0;
        
        for (ItemStack invItem : inv.getStorageContents()) {
            if (invItem == null) {
                freeSpace += item.getMaxStackSize();
            } else if (invItem.isSimilar(item)) {
                freeSpace += item.getMaxStackSize() - invItem.getAmount();
            }
        }
        
        return freeSpace >= item.getAmount();
    }
    
    private ItemStack createFillerItem() {
        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = filler.getItemMeta();
        meta.displayName(Component.text(" "));
        filler.setItemMeta(meta);
        return filler;
    }
    
    private String formatMaterialName(String materialName) {
        return Arrays.stream(materialName.toLowerCase().split("_"))
            .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1))
            .collect(Collectors.joining(" "));
    }
    
    public Map<String, ShopCategory> getCategories() {
        return categories;
    }
}