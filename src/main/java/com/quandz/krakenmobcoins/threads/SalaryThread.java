package com.quandz.krakenmobcoins.threads;

import com.quandz.krakenmobcoins.MobCoins;
import com.quandz.krakenmobcoins.enums.ConfigMessagesList;
import com.quandz.krakenmobcoins.manager.AccountManager;
import com.quandz.krakenmobcoins.manager.SalaryManager;
import com.quandz.krakenmobcoins.object.PlayerCoins;
import com.quandz.krakenmobcoins.utils.ConfigUtils;
import com.quandz.krakenmobcoins.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class SalaryThread extends BukkitRunnable {

    private final MobCoins plugin;

    public SalaryThread(MobCoins plugin, long delay) {
        this.plugin = plugin;
        this.runTaskTimerAsynchronously(plugin, delay, delay);
    }

    @Override
    public void run() {
        ConfigUtils config = ConfigUtils.getInstance();

        Utils utils = plugin.getUtils();
        AccountManager accountManager = plugin.getAccountManager();
        SalaryManager salaryManager = plugin.getSalaryManager();

        salaryManager.getPlayerSalaries().keySet().stream().filter
                ((uuid) -> Bukkit.getPlayer(uuid) != null).forEach((uuid) -> {

            Player player = Bukkit.getPlayer(uuid);
            PlayerCoins playerCoins = accountManager.getPlayerData(uuid);
            double amount = salaryManager.getPlayerSalary(uuid);

            utils.sendSound(player);
            utils.sendTitle(player, amount);

            ConfigMessagesList.SALARY.toStringList().forEach((message)
                    -> player.sendMessage(utils.color(message.replace("%coins%",
                    utils.getDecimalFormat().format(amount)))));

            if (config.receiveAfterMessage) playerCoins.setMoney(playerCoins.getMoney() + amount);

            salaryManager.removePlayer(uuid);
        });
    }
}
