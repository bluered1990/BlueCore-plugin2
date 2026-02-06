package me.blueb8h.bluecore.economy;

import java.util.UUID;

public class EconomyUser {
    private final UUID uuid;
    private double balance;
    
    public EconomyUser(UUID uuid, double balance) {
        this.uuid = uuid;
        this.balance = balance;
    }
    
    public UUID getUuid() {
        return uuid;
    }
    
    public double getBalance() {
        return balance;
    }
    
    public void setBalance(double balance) {
        this.balance = balance;
    }
}