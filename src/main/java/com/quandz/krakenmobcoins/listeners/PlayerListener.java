package com.quandz.krakenmobcoins.listeners;

import com.quandz.krakenmobcoins.MobCoins;
import com.quandz.krakenmobcoins.object.PlayerCoins;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public record PlayerListener(MobCoins plugin) implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskAsynchronously(plugin,
                () -> plugin.getAccountManager().load(player, true));
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerCoins coins = plugin.getAccountManager().getPlayerData(player.getUniqueId());
        if (coins == null) return;
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            plugin.getAccountManager().save(coins, true);
            plugin.getAccountManager().getPlayerCoins().remove(player.getUniqueId().toString());
        });
    }
}
