package me.aglerr.krakenmobcoins.listeners;

import me.aglerr.krakenmobcoins.MobCoins;
import me.aglerr.krakenmobcoins.utils.ConfigUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class CreatureSpawn implements Listener {

    private final MobCoins plugin;
    public CreatureSpawn(final MobCoins plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onSpawn(CreatureSpawnEvent event) {
        if (ConfigUtils.getInstance().disableMobCoinsFromSpawner) {
            if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER) {
                plugin.getMobSpawner().add(event.getEntity().getUniqueId());
            }
        }

    }

}
