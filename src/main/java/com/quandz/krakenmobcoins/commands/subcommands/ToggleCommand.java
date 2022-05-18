package com.quandz.krakenmobcoins.commands.subcommands;

import com.quandz.krakenmobcoins.MobCoins;
import com.quandz.krakenmobcoins.abstraction.SubCommand;
import com.quandz.krakenmobcoins.enums.ConfigMessages;
import com.quandz.krakenmobcoins.manager.ToggleNotificationManager;
import com.quandz.krakenmobcoins.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ToggleCommand extends SubCommand {

    @Override
    public @Nullable String getPermission() {
        return "krakenmobcoins.toggle";
    }

    @Override
    public @Nullable List<String> parseTabCompletions(MobCoins plugin, CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public void perform(MobCoins plugin, CommandSender sender, String[] args) {

        final Utils utils = plugin.getUtils();
        final ToggleNotificationManager notificationManager = plugin.getNotificationManager();

        if (sender instanceof Player player) {
            if (notificationManager.isPlayerExist(player.getUniqueId())) {

                notificationManager.unBlockNotification(player.getUniqueId());
                player.sendMessage(utils.color(ConfigMessages.TOGGLE_ON.toString())
                        .replace("%prefix%", utils.getPrefix()));

            } else {

                notificationManager.blockNotification(player.getUniqueId());
                player.sendMessage(utils.color(ConfigMessages.TOGGLE_OFF.toString())
                        .replace("%prefix%", utils.getPrefix()));
            }

        } else {
            sender.sendMessage(utils.color(ConfigMessages.ONLY_PLAYER.toString())
                    .replace("%player%", utils.getPrefix()));
        }

    }
}
