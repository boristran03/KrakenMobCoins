package com.quandz.krakenmobcoins.listeners;

import com.quandz.krakenmobcoins.MobCoins;
import com.quandz.krakenmobcoins.coinmob.CoinMob;
import com.quandz.krakenmobcoins.coinmob.CoinMobManager;
import com.quandz.krakenmobcoins.utils.ConfigUtils;
import com.quandz.krakenmobcoins.utils.Utils;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.List;

public record EntityDeathPhysical(MobCoins plugin) implements Listener {

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        ConfigUtils config = ConfigUtils.getInstance();
        if (!config.enabledPhysicalCoin) return;

        Utils utils = plugin.getUtils();
        LivingEntity entity = event.getEntity();

        if (!config.ignoreDeathCause) {
            if (event.getEntity().getLastDamageCause() == null) return;
            if (!config.damageCauses.contains(event.getEntity().getLastDamageCause().getCause())) return;
        }
        List<String> worlds = config.disabledWorlds;
        if (worlds.contains(entity.getWorld().getName())) return;

        if (config.disableMobCoinsFromSpawner) {
            if (plugin.getMobSpawner().contains(entity.getUniqueId())) {
                plugin.getMobSpawner().remove(entity.getUniqueId());
                return;
            }
        }

        String type = entity.getType().toString();
        CoinMobManager manager = plugin.getCoinMobManager();

        CoinMob coinMob = manager.getCoinMob(type);
        if (coinMob == null) return;
        if (!coinMob.willDropCoins()) return;
        double amount = coinMob.getAmountToDrop();

        if (entity.getKiller() == null) {
            entity.getWorld().dropItemNaturally(entity.getLocation(), utils.getMobCoinItem(amount));
        } else {
            Player player = entity.getKiller();
            int multiplier = utils.getBooster(player);
            double multiplierAmount = amount * multiplier / 100;
            double amountAfter = amount + multiplierAmount;

            entity.getWorld().dropItemNaturally(entity.getLocation(), utils.getMobCoinItem(amountAfter));
        }

    }

}
