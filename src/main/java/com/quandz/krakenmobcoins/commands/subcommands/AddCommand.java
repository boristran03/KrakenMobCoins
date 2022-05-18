package com.quandz.krakenmobcoins.commands.subcommands;

import com.quandz.krakenmobcoins.MobCoins;
import com.quandz.krakenmobcoins.abstraction.SubCommand;
import com.quandz.krakenmobcoins.enums.ConfigMessages;
import com.quandz.krakenmobcoins.object.PlayerCoins;
import com.quandz.krakenmobcoins.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AddCommand extends SubCommand {

    @Override
    public @Nullable String getPermission() {
        return "krakenmobcoins.admin";
    }

    @Override
    public @Nullable List<String> parseTabCompletions(MobCoins plugin, CommandSender sender, String[] args) {

        if (args.length == 2) {
            List<String> suggestions = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                suggestions.add(player.getName());
            }
            return suggestions;
        }

        if (args.length == 3) return (Collections.singletonList("<amount>"));

        return null;
    }

    @Override
    public void perform(MobCoins plugin, CommandSender sender, String[] args) {

        Utils utils = plugin.getUtils();

        if (args.length < 3) {
            sender.sendMessage(utils.color("&cUsage: /mobcoins add <player> <amount>"));
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(utils.color(ConfigMessages.TARGET_NOT_FOUND.toString())
                    .replace("%prefix%", utils.getPrefix())
                    .replace("%player%", args[1]));
        } else {
            if (utils.isDouble(args[2])) {
                double amount = Double.parseDouble(args[2]);
                PlayerCoins targetCoins = plugin.getAccountManager().getPlayerData(target.getUniqueId());
                if (targetCoins == null) {
                    sender.sendMessage(utils.color(ConfigMessages.NO_ACCOUNT_OTHERS.toString())
                            .replace("%prefix%", utils.getPrefix())
                            .replace("%player%", args[1]));

                } else {

                    targetCoins.setMoney(targetCoins.getMoney() + amount);
                    sender.sendMessage(utils.color(ConfigMessages.ADD_COINS.toString())
                            .replace("%prefix%", utils.getPrefix())
                            .replace("%player%", target.getName())
                            .replace("%coins%", String.valueOf(amount)));

                    target.sendMessage(utils.color(ConfigMessages.TARGET_ADD_COINS.toString())
                            .replace("%prefix%", utils.getPrefix())
                            .replace("%coins%", String.valueOf(amount)));

                }

            } else {
                sender.sendMessage(utils.color(ConfigMessages.NOT_INTEGER.toString())
                        .replace("%prefix%", utils.getPrefix()));
            }
        }

    }
}
