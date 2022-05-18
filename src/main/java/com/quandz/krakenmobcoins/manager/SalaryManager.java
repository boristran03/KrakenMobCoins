package com.quandz.krakenmobcoins.manager;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SalaryManager {

    private final ConcurrentHashMap<UUID, Double> playerSalary = new ConcurrentHashMap<>();

    public ConcurrentHashMap<UUID, Double> getPlayerSalaries() {
        return playerSalary;
    }

    public boolean isPlayerExist(UUID uuid) {
        return playerSalary.containsKey(uuid);
    }

    public double getPlayerSalary(UUID uuid) {
        return playerSalary.get(uuid);
    }

    public void setPlayerSalary(UUID uuid, double amount) {
        playerSalary.put(uuid, amount);
    }

    public void removePlayer(UUID uuid) {
        playerSalary.remove(uuid);
    }


}
