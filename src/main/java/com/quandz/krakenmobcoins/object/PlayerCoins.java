package com.quandz.krakenmobcoins.object;

import org.bukkit.Bukkit;

import java.util.UUID;

public class PlayerCoins {

    private final String uuid;
    private double coins = 0;

    public PlayerCoins(String uuid) {
        this.uuid = uuid;
    }

    public double getMoney() {
        return this.coins;
    }

    public void setMoney(double value) {
        this.coins = value;
    }

    public String getUUID() {
        return this.uuid;
    }

    public void reduceMoney(double value) {
        setMoney(getMoney() - value);
    }

    public void addMoney(double value) {
        setMoney(getMoney() + value);
    }

    public String getPlayerName() {
        return Bukkit.getOfflinePlayer(UUID.fromString(this.uuid)).getName();
    }

}
