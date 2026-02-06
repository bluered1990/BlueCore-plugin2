package me.blueb8h.bluecore.shop;

import java.util.ArrayList;
import java.util.List;

public class ShopItem {
    private final String id;
    private String material;
    private String name;
    private double buyPrice;
    private double sellPrice;
    private List<String> lore;
    private boolean enchanted;
    
    public ShopItem(String id) {
        this.id = id;
        this.lore = new ArrayList<>();
        this.enchanted = false;
    }
    
    public String getId() {
        return id;
    }
    
    public String getMaterial() {
        return material;
    }
    
    public void setMaterial(String material) {
        this.material = material;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public double getBuyPrice() {
        return buyPrice;
    }
    
    public void setBuyPrice(double buyPrice) {
        this.buyPrice = buyPrice;
    }
    
    public double getSellPrice() {
        return sellPrice;
    }
    
    public void setSellPrice(double sellPrice) {
        this.sellPrice = sellPrice;
    }
    
    public List<String> getLore() {
        return lore;
    }
    
    public void setLore(List<String> lore) {
        this.lore = lore;
    }
    
    public boolean isEnchanted() {
        return enchanted;
    }
    
    public void setEnchanted(boolean enchanted) {
        this.enchanted = enchanted;
    }
}