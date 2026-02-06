package me.blueb8h.bluecore.ranks;

import me.blueb8h.bluecore.BlueCore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RankManager {
    private final BlueCore plugin;
    private final Map<String, Rank> ranks = new HashMap<>();
    private final Map<UUID, String> playerRanks = new HashMap<>();
    private final File dataFile;
    
    public RankManager(BlueCore plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "ranks_data.yml");
    }
    
    public void load() {
        // Load ranks from config
        File configFile = new File(plugin.getDataFolder(), "ranks.yml");
        if (!configFile.exists()) {
            plugin.saveResource("ranks.yml", false);
        }
        
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        ranks.clear();
        
        if (config.contains("ranks")) {
            for (String rankId : config.getConfigurationSection("ranks").getKeys(false)) {
                Rank rank = new Rank(rankId);
                rank.setPrefix(config.getString("ranks." + rankId + ".prefix", ""));
                rank.setColor(config.getString("ranks." + rankId + ".color", "&f"));
                rank.setPriority(config.getInt("ranks." + rankId + ".priority", 0));
                rank.setPermissions(config.getStringList("ranks." + rankId + ".permissions"));
                ranks.put(rankId, rank);
            }
        }
        
        // Load player ranks
        if (dataFile.exists()) {
            FileConfiguration data = YamlConfiguration.loadConfiguration(dataFile);
            playerRanks.clear();
            
            if (data.contains("players")) {
                for (String uuidStr : data.getConfigurationSection("players").getKeys(false)) {
                    UUID uuid = UUID.fromString(uuidStr);
                    String rankId = data.getString("players." + uuidStr);
                    playerRanks.put(uuid, rankId);
                }
            }
        }
        
        // Update all online players
        for (Player player : Bukkit.getOnlinePlayers()) {
            updatePlayerRank(player);
        }
    }
    
    public void save() {
        FileConfiguration data = new YamlConfiguration();
        
        for (Map.Entry<UUID, String> entry : playerRanks.entrySet()) {
            data.set("players." + entry.getKey().toString(), entry.getValue());
        }
        
        try {
            data.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save rank data: " + e.getMessage());
        }
    }
    
    public Rank getRank(String rankId) {
        return ranks.get(rankId);
    }
    
    public Rank getPlayerRank(UUID uuid) {
        String rankId = playerRanks.get(uuid);
        if (rankId == null) {
            return ranks.get("default");
        }
        return ranks.getOrDefault(rankId, ranks.get("default"));
    }
    
    public boolean setPlayerRank(UUID uuid, String rankId) {
        if (!ranks.containsKey(rankId)) {
            return false;
        }
        
        playerRanks.put(uuid, rankId);
        
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            updatePlayerRank(player);
        }
        
        return true;
    }
    
    public void updatePlayerRank(Player player) {
        Rank rank = getPlayerRank(player.getUniqueId());
        
        // Update TAB list with scoreboard teams
        Scoreboard scoreboard = player.getScoreboard();
        if (scoreboard == Bukkit.getScoreboardManager().getMainScoreboard()) {
            scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            player.setScoreboard(scoreboard);
        }
        
        String teamName = "rank_" + rank.getPriority() + "_" + rank.getId();
        Team team = scoreboard.getTeam(teamName);
        if (team == null) {
            team = scoreboard.registerNewTeam(teamName);
            team.prefix(rank.getFullPrefix());
            team.color(TextColor.color(rank.getChatColor().asBungee().getColor().getRGB()));
        }
        
        team.addEntry(player.getName());
        
        // Update player display name
        player.displayName(rank.getFullPrefix().append(Component.text(player.getName())
            .color(TextColor.color(rank.getChatColor().asBungee().getColor().getRGB()))));
        player.playerListName(rank.getFullPrefix().append(Component.text(player.getName())
            .color(TextColor.color(rank.getChatColor().asBungee().getColor().getRGB()))));
    }
    
    public Map<String, Rank> getRanks() {
        return ranks;
    }
    
    public String[] getRankIds() {
        return ranks.keySet().toArray(new String[0]);
    }
}