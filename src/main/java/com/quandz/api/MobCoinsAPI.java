package com.quandz.api;

import com.quandz.krakenmobcoins.MobCoins;
import com.quandz.krakenmobcoins.manager.SalaryManager;
import com.quandz.krakenmobcoins.manager.ToggleNotificationManager;
import com.quandz.krakenmobcoins.object.PlayerCoins;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public record MobCoinsAPI(MobCoins plugin) {

    @Nullable
    public PlayerCoins getPlayerData(Player player) {
        return plugin.getAccountManager().getPlayerData(player.getUniqueId());
    }

    public ToggleNotificationManager getNotificationManager() {
        return plugin.getNotificationManager();
    }

    public SalaryManager getSalaryManager() {
        return plugin.getSalaryManager();
    }

}
