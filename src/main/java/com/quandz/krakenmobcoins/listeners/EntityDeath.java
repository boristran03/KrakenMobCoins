package com.quandz.krakenmobcoins.listeners;

import com.bgsoftware.wildstacker.api.WildStackerAPI;
import com.quandz.api.events.MobCoinsReceiveEvent;
import com.quandz.krakenmobcoins.MobCoins;
import com.quandz.krakenmobcoins.coinmob.CoinMob;
import com.quandz.krakenmobcoins.coinmob.CoinMobManager;
import com.quandz.krakenmobcoins.manager.DependencyManager;
import com.quandz.krakenmobcoins.manager.SalaryManager;
import com.quandz.krakenmobcoins.object.PlayerCoins;
import com.quandz.krakenmobcoins.utils.ConfigUtils;
import com.quandz.krakenmobcoins.utils.Utils;
import dev.rosewood.rosestacker.api.RoseStackerAPI;
import dev.rosewood.rosestacker.manager.ConfigurationManager;
import dev.rosewood.rosestacker.stack.StackedEntity;
import dev.rosewood.rosestacker.utils.PersistentDataUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.List;

public record EntityDeath(MobCoins plugin) implements Listener {

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Utils utils = plugin.getUtils();
        ConfigUtils configUtils = ConfigUtils.getInstance();
        DependencyManager dependencyManager = plugin.getDependencyManager();

        LivingEntity entity = event.getEntity();

        if (configUtils.enabledPhysicalCoin) return;
        if (entity.getKiller() == null) return;


        Player player = event.getEntity().getKiller();
        PlayerCoins playerCoins = plugin.getAccountManager().getPlayerData(player.getUniqueId());
        if (playerCoins == null) return;
        List<String> worlds = configUtils.disabledWorlds;
        if (worlds.contains(player.getWorld().getName())) return;

        if (configUtils.disableMobCoinsFromSpawner) {
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

        double amount = coinMob.getAmountToDrop(plugin.getConfig(), player);

        int multiplier = utils.getBooster(player);
        double multiplierAmount = amount * multiplier / 100;
        double amountAfter = amount + multiplierAmount;

        MobCoinsReceiveEvent mobCoinsReceiveEvent = new MobCoinsReceiveEvent(player, amount, amountAfter, multiplierAmount, entity, multiplier);
        Bukkit.getPluginManager().callEvent(mobCoinsReceiveEvent);
        if (mobCoinsReceiveEvent.isCancelled()) return;

        if (configUtils.wildStackerSupport) {
            if (dependencyManager.isWildStacker()) {
                mobCoinsReceiveEvent.setAmountAfterMultiplier(mobCoinsReceiveEvent.getAmountAfterMultiplier() * WildStackerAPI.getEntityAmount(entity));
            }
        }
        if (configUtils.roseStackerSupport) {
            {
                if (dependencyManager.isRoseStacker()) {
                    StackedEntity stackedEntity = RoseStackerAPI.getInstance().getStackedEntity(entity);
                    EntityDamageEvent lastDamageCause = entity.getLastDamageCause();
                    if (stackedEntity != null && (stackedEntity.getStackSettings().shouldKillEntireStackOnDeath()
                            || (ConfigurationManager.Setting.SPAWNER_DISABLE_MOB_AI_OPTIONS_KILL_ENTIRE_STACK_ON_DEATH.getBoolean() && PersistentDataUtils.isAiDisabled(entity))
                            || (lastDamageCause != null && ConfigurationManager.Setting.ENTITY_KILL_ENTIRE_STACK_CONDITIONS.getStringList()
                            .stream().anyMatch(x -> x.equalsIgnoreCase(lastDamageCause.getCause().name()))))) {
                        mobCoinsReceiveEvent.setAmountAfterMultiplier(mobCoinsReceiveEvent.getAmountAfterMultiplier() * stackedEntity.getStackSize());
                    }
                }
            }
        }

        if (configUtils.enabledSalaryMode) {
            if (!configUtils.receiveAfterMessage) {
                playerCoins.setMoney(playerCoins.getMoney() + mobCoinsReceiveEvent.getAmountAfterMultiplier());
            }
        } else {
            playerCoins.setMoney(playerCoins.getMoney() + mobCoinsReceiveEvent.getAmountAfterMultiplier());
        }

        if (!plugin.getNotificationManager().isPlayerExist(player.getUniqueId())) {
            if (configUtils.enabledSalaryMode) {
                SalaryManager salaryManager = plugin.getSalaryManager();
                if (salaryManager.isPlayerExist(player.getUniqueId())) {
                    double current = salaryManager.getPlayerSalary(player.getUniqueId());
                    double currentFinal = current + mobCoinsReceiveEvent.getAmountAfterMultiplier();
                    salaryManager.setPlayerSalary(player.getUniqueId(), currentFinal);
                } else {
                    salaryManager.setPlayerSalary(player.getUniqueId(), mobCoinsReceiveEvent.getAmountAfterMultiplier());
                }
            } else {
                utils.sendSound(player);
                utils.sendMessage(player, mobCoinsReceiveEvent.getAmountAfterMultiplier());
                utils.sendTitle(player, mobCoinsReceiveEvent.getAmountAfterMultiplier());
            }
        }

    }

}
