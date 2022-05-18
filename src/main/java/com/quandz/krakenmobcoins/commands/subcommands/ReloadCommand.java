package com.quandz.krakenmobcoins.commands.subcommands;

import com.quandz.krakenmobcoins.MobCoins;
import com.quandz.krakenmobcoins.abstraction.SubCommand;
import com.quandz.krakenmobcoins.enums.ConfigMessages;
import com.quandz.krakenmobcoins.utils.ConfigUtils;
import com.quandz.krakenmobcoins.utils.Utils;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ReloadCommand extends SubCommand {

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

        sender.sendMessage(utils.color(ConfigMessages.RELOAD.toString())
                .replace("%prefix%", utils.getPrefix()));
        plugin.reloadConfigs();
        ConfigUtils.getInstance().loadConfig();
    }

}
