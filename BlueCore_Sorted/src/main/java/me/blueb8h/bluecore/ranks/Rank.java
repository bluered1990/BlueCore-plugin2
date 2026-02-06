package me.blueb8h.bluecore.ranks;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class Rank {
    private final String id;
    private String prefix;
    private String color;
    private int priority;
    private List<String> permissions;
    
    public Rank(String id) {
        this.id = id;
        this.prefix = "";
        this.color = "&f";
        this.priority = 0;
        this.permissions = new ArrayList<>();
    }
    
    public String getId() {
        return id;
    }
    
    public String getPrefix() {
        return prefix;
    }
    
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
    
    public String getColor() {
        return color;
    }
    
    public void setColor(String color) {
        this.color = color;
    }
    
    public int getPriority() {
        return priority;
    }
    
    public void setPriority(int priority) {
        this.priority = priority;
    }
    
    public List<String> getPermissions() {
        return permissions;
    }
    
    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }
    
    public String getColoredName() {
        return color + id;
    }
    
    public Component getDisplayName() {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(color + id);
    }
    
    public Component getFullPrefix() {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(prefix + " ");
    }
    
    public ChatColor getChatColor() {
        if (color.length() > 1 && color.charAt(0) == '&') {
            char colorCode = color.charAt(1);
            return ChatColor.getByChar(colorCode);
        }
        return ChatColor.WHITE;
    }
}