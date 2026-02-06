package me.blueb8h.bluecore.shop;

import java.util.ArrayList;
import java.util.List;

public class ShopCategory {
    private final String id;
    private String name;
    private int slot;
    private String material;
    private final List<ShopItem> items;
    
    public ShopCategory(String id) {
        this.id = id;
        this.items = new ArrayList<>();
    }
    
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public int getSlot() {
        return slot;
    }
    
    public void setSlot(int slot) {
        this.slot = slot;
    }
    
    public String getMaterial() {
        return material;
    }
    
    public void setMaterial(String material) {
        this.material = material;
    }
    
    public List<ShopItem> getItems() {
        return items;
    }
    
    public void addItem(ShopItem item) {
        items.add(item);
    }
}