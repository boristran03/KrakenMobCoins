package com.quandz.krakenmobcoins.commands.subcommands;

import com.quandz.krakenmobcoins.MobCoins;
import com.quandz.krakenmobcoins.abstraction.SubCommand;
import com.quandz.krakenmobcoins.enums.ConfigMessages;
import com.quandz.krakenmobcoins.manager.RotatingManager;
import com.quandz.krakenmobcoins.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RefreshCommand extends SubCommand {

    @Override
    public @Nullable String getPermission() {
        return "krakenmobcoins.admin";
    }

    @Override
    public @Nullable List<String> parseTabCompletions(MobCoins plugin, CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public void perform(MobCoins plugin, CommandSender sender, String[] args) {

        Utils utils = plugin.getUtils();
        FileConfiguration config = plugin.getConfig();

        if (config.getBoolean("rotatingShop.enabled")) {

            sender.sendMessage(utils.color(ConfigMessages.REFRESH.toString())
                    .replace("%prefix%", utils.getPrefix()));

            RotatingManager rotatingManager = plugin.getRotatingManager();

            rotatingManager.setNormalTime(rotatingManager.getDefaultNormalTime());
            rotatingManager.setSpecialTime(rotatingManager.getDefaultSpecialTime());

            rotatingManager.refreshNormalItems();
            rotatingManager.refreshSpecialItems();

        }

        plugin.getItemStockManager().clearStock();
        plugin.getPurchaseLimitManager().resetLimit();

    }

}
