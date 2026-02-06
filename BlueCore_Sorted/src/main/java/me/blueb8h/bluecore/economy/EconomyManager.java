package me.blueb8h.bluecore.economy;

import me.blueb8h.bluecore.BlueCore;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class EconomyManager {
    private final BlueCore plugin;
    private final Map<UUID, EconomyUser> users = new ConcurrentHashMap<>();
    private final File dataFile;
    private FileConfiguration economyConfig;
    private double startingBalance;
    private double maxBalance;
    private String currencySymbol;
    private Map<String, Double> sellValues = new HashMap<>();
    
    public EconomyManager(BlueCore plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "economy_data.yml");
        loadConfig();
    }
    
    private void loadConfig() {
        File configFile = new File(plugin.getDataFolder(), "economy.yml");
        if (!configFile.exists()) {
            plugin.saveResource("economy.yml", false);
        }
        economyConfig = YamlConfiguration.loadConfiguration(configFile);
        
        startingBalance = economyConfig.getDouble("starting-balance", 1000.0);
        maxBalance = economyConfig.getDouble("max-balance", 1000000.0);
        currencySymbol = economyConfig.getString("currency-symbol", "$");
        
        sellValues.clear();
        if (economyConfig.contains("sell-values")) {
            for (String key : economyConfig.getConfigurationSection("sell-values").getKeys(false)) {
                sellValues.put(key, economyConfig.getDouble("sell-values." + key));
            }
        }
    }
    
    public void load() {
        if (!dataFile.exists()) {
            return;
        }
        
        FileConfiguration data = YamlConfiguration.loadConfiguration(dataFile);
        users.clear();
        
        if (data.contains("users")) {
            for (String uuidStr : data.getConfigurationSection("users").getKeys(false)) {
                UUID uuid = UUID.fromString(uuidStr);
                double balance = data.getDouble("users." + uuidStr + ".balance", startingBalance);
                users.put(uuid, new EconomyUser(uuid, balance));
            }
        }
    }
    
    public void save() {
        FileConfiguration data = new YamlConfiguration();
        
        for (Map.Entry<UUID, EconomyUser> entry : users.entrySet()) {
            String path = "users." + entry.getKey().toString();
            data.set(path + ".balance", entry.getValue().getBalance());
        }
        
        try {
            data.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save economy data: " + e.getMessage());
        }
    }
    
    public double getBalance(UUID uuid) {
        EconomyUser user = users.get(uuid);
        if (user == null) {
            user = new EconomyUser(uuid, startingBalance);
            users.put(uuid, user);
        }
        return user.getBalance();
    }
    
    public boolean addBalance(UUID uuid, double amount) {
        if (amount <= 0) return false;
        
        EconomyUser user = users.get(uuid);
        if (user == null) {
            user = new EconomyUser(uuid, startingBalance);
            users.put(uuid, user);
        }
        
        double newBalance = user.getBalance() + amount;
        if (newBalance > maxBalance) {
            newBalance = maxBalance;
        }
        
        user.setBalance(newBalance);
        return true;
    }
    
    public boolean removeBalance(UUID uuid, double amount) {
        if (amount <= 0) return false;
        
        EconomyUser user = users.get(uuid);
        if (user == null) {
            user = new EconomyUser(uuid, startingBalance);
            users.put(uuid, user);
        }
        
        if (user.getBalance() < amount) {
            return false;
        }
        
        user.setBalance(user.getBalance() - amount);
        return true;
    }
    
    public boolean hasBalance(UUID uuid, double amount) {
        return getBalance(uuid) >= amount;
    }
    
    public boolean transfer(UUID from, UUID to, double amount) {
        if (amount <= 0) return false;
        
        if (!hasBalance(from, amount)) {
            return false;
        }
        
        if (removeBalance(from, amount)) {
            return addBalance(to, amount);
        }
        
        return false;
    }
    
    public double getSellValue(String material) {
        return sellValues.getOrDefault(material, 0.0);
    }
    
    public String getCurrencySymbol() {
        return currencySymbol;
    }
    
    public double getMaxBalance() {
        return maxBalance;
    }
    
    public EconomyUser getUser(UUID uuid) {
        EconomyUser user = users.get(uuid);
        if (user == null) {
            user = new EconomyUser(uuid, startingBalance);
            users.put(uuid, user);
        }
        return user;
    }
}