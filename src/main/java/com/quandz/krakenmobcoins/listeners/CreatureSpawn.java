package com.quandz.krakenmobcoins.listeners;

import com.quandz.krakenmobcoins.MobCoins;
import com.quandz.krakenmobcoins.utils.ConfigUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public record CreatureSpawn(MobCoins plugin) implements Listener {

    @EventHandler
    public void onSpawn(CreatureSpawnEvent event) {
        if (ConfigUtils.getInstance().disableMobCoinsFromSpawner) {
            if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER) {
                plugin.getMobSpawner().add(event.getEntity().getUniqueId());
            }
        }
    }
}
