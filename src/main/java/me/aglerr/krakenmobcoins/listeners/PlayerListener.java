package me.aglerr.krakenmobcoins.listeners;

import me.aglerr.krakenmobcoins.MobCoins;
import me.aglerr.krakenmobcoins.database.PlayerCoins;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    private final MobCoins plugin;

    public PlayerListener(final MobCoins plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskAsynchronously(plugin,
                () -> plugin.getAccountManager().loadPlayerData(player));
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerCoins coins = plugin.getAccountManager().getPlayerData(player.getUniqueId().toString());
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            plugin.getAccountManager().savePlayerData(coins);
            plugin.getAccountManager().getPlayerCoins().remove(player.getUniqueId().toString());
        });
    }
}
