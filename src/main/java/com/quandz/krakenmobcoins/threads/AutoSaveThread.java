package com.quandz.krakenmobcoins.threads;

import com.quandz.krakenmobcoins.MobCoins;
import com.quandz.krakenmobcoins.manager.AccountManager;
import org.bukkit.scheduler.BukkitRunnable;

public class AutoSaveThread extends BukkitRunnable {

    private final MobCoins plugin;

    public AutoSaveThread(MobCoins plugin, long delay) {
        this.plugin = plugin;
        this.runTaskTimerAsynchronously(plugin, delay, delay);
    }

    @Override
    public void run() {
        AccountManager accountManager = plugin.getAccountManager();
        accountManager.saveAll();
    }
}
