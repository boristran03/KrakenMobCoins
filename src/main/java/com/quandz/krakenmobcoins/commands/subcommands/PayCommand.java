package com.quandz.krakenmobcoins.commands.subcommands;

import com.quandz.krakenmobcoins.MobCoins;
import com.quandz.krakenmobcoins.abstraction.SubCommand;
import com.quandz.krakenmobcoins.enums.ConfigMessages;
import com.quandz.krakenmobcoins.manager.AccountManager;
import com.quandz.krakenmobcoins.object.PlayerCoins;
import com.quandz.krakenmobcoins.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PayCommand extends SubCommand {

    @Override
    public @Nullable String getPermission() {
        return "krakenmobcoins.pay";
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

        if (args.length == 3) return Collections.singletonList("<amount>");

        return null;
    }

    @Override
    public void perform(MobCoins plugin, CommandSender sender, String[] args) {

        Utils utils = plugin.getUtils();
        AccountManager accountManager = plugin.getAccountManager();

        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length < 3) {
                player.sendMessage(utils.color("&cUsage: /mobcoins pay <player> <amount>"));
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
                    if (amount < 0) {
                        sender.sendMessage(utils.color(ConfigMessages.NEGATIVE_AMOUNT.toString())
                                .replace("%prefix%", utils.getPrefix()));
                        return;
                    }

                    PlayerCoins playerCoins = accountManager.getPlayerData(player.getUniqueId());
                    PlayerCoins targetCoins = accountManager.getPlayerData(target.getUniqueId());

                    if (playerCoins == null) {
                        sender.sendMessage(utils.color(ConfigMessages.NO_ACCOUNT.toString())
                                .replace("%prefix%", utils.getPrefix()));
                        return;
                    }

                    if (targetCoins == null) {
                        sender.sendMessage(utils.color(ConfigMessages.NO_ACCOUNT_OTHERS.toString())
                                .replace("%prefix%", utils.getPrefix())
                                .replace("%player%", args[1]));
                        return;
                    }

                    if (playerCoins.getMoney() >= amount) {
                        playerCoins.setMoney(playerCoins.getMoney() - amount);
                        targetCoins.setMoney(targetCoins.getMoney() + amount);

                        player.sendMessage(utils.color(ConfigMessages.SEND_COINS.toString())
                                .replace("%prefix%", utils.getPrefix())
                                .replace("%player%", target.getName())
                                .replace("%coins%", String.valueOf(amount)));

                        target.sendMessage(utils.color(ConfigMessages.RECEIVED_COINS.toString())
                                .replace("%prefix%", utils.getPrefix())
                                .replace("%player%", player.getName())
                                .replace("%coins%", String.valueOf(amount)));
                    } else {
                        player.sendMessage(utils.color(ConfigMessages.NOT_ENOUGH_COINS.toString())
                                .replace("%prefix%", utils.getPrefix()));
                    }


                } else {
                    player.sendMessage(utils.color(ConfigMessages.NOT_INTEGER.toString())
                            .replace("%prefix%", utils.getPrefix()));
                }
            }

        } else {
            sender.sendMessage(utils.color(ConfigMessages.ONLY_PLAYER.toString())
                    .replace("%player%", utils.getPrefix()));
        }

    }

}
