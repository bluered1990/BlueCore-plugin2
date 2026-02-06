package me.blueb8h.bluecore;

import me.blueb8h.bluecore.commands.BalanceCommand;
import me.blueb8h.bluecore.commands.PayCommand;
import me.blueb8h.bluecore.commands.RankCommand;
import me.blueb8h.bluecore.commands.ShopCommand;
import me.blueb8h.bluecore.economy.EconomyManager;
import me.blueb8h.bluecore.listeners.InventoryListener;
import me.blueb8h.bluecore.listeners.JoinListener;
import me.blueb8h.bluecore.ranks.RankManager;
import me.blueb8h.bluecore.shop.ShopGUI;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class BlueCore extends JavaPlugin {
    private static BlueCore instance;
    private EconomyManager economyManager;
    private RankManager rankManager;
    private ShopGUI shopGUI;

    @Override
    public void onEnable() {
        instance = this;
        
        // Save default configs
        saveDefaultConfig();
        saveResource("economy.yml", false);
        saveResource("ranks.yml", false);
        saveResource("shops/default.yml", false);
        
        // Initialize managers
        economyManager = new EconomyManager(this);
        rankManager = new RankManager(this);
        shopGUI = new ShopGUI(this);
        
        // Register commands
        Objects.requireNonNull(getCommand("bal")).setExecutor(new BalanceCommand(this));
        Objects.requireNonNull(getCommand("pay")).setExecutor(new PayCommand(this));
        Objects.requireNonNull(getCommand("rank")).setExecutor(new RankCommand(this));
        Objects.requireNonNull(getCommand("shop")).setExecutor(new ShopCommand(this));
        
        // Register listeners
        getServer().getPluginManager().registerEvents(new JoinListener(this), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(this), this);
        
        // Load data
        economyManager.load();
        rankManager.load();
        
        getLogger().info("BlueCore enabled!");
    }
    
    @Override
    public void onDisable() {
        // Save data
        if (economyManager != null) economyManager.save();
        if (rankManager != null) rankManager.save();
        
        getLogger().info("BlueCore disabled!");
    }
    
    public static BlueCore getInstance() {
        return instance;
    }
    
    public EconomyManager getEconomyManager() {
        return economyManager;
    }
    
    public RankManager getRankManager() {
        return rankManager;
    }
    
    public ShopGUI getShopGUI() {
        return shopGUI;
    }
}