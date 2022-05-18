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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class BalanceCommand extends SubCommand {

    @Override
    public @Nullable String getPermission() {
        return "krakenmobcoins.balance";
    }

    @Override
    public @Nullable List<String> parseTabCompletions(MobCoins plugin, CommandSender sender, String[] args) {

        if (sender.hasPermission("krakenmobcoins.balance.others")) {
            if (args.length == 2) {
                List<String> suggestions = new ArrayList<>();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    suggestions.add(player.getName());
                }
                return suggestions;
            }
        }


        return null;
    }

    @Override
    public void perform(MobCoins plugin, CommandSender sender, String[] args) {

        Utils utils = plugin.getUtils();
        DecimalFormat df = utils.getDecimalFormat();

        if (args.length == 1) {
            if (sender instanceof Player player) {
                PlayerCoins coins = plugin.getAccountManager().getPlayerData(player.getUniqueId());

                if (coins == null) {
                    player.sendMessage(utils.color(ConfigMessages.NO_ACCOUNT.toString())
                            .replace("%prefix%", utils.getPrefix()));

                } else {

                    player.sendMessage(utils.color(ConfigMessages.BALANCE.toString())
                            .replace("%prefix%", utils.getPrefix())
                            .replace("%coins%", df.format(coins.getMoney())));

                }

            } else {

                sender.sendMessage(utils.color("&cUsage: /mobcoins balance <player>"));

            }

        } else if (args.length == 2) {
            if (!(sender.hasPermission("krakenmobcoins.balance.others"))) {
                sender.sendMessage(utils.color(ConfigMessages.NO_PERMISSION.toString())
                        .replace("%prefix%", utils.getPrefix())
                        .replace("%permission%", "krakenmobcoins.balance.others"));
                return;
            }

            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(utils.color(ConfigMessages.TARGET_NOT_FOUND.toString())
                        .replace("%prefix%", utils.getPrefix())
                        .replace("%player%", args[1]));

            } else {

                PlayerCoins coins = plugin.getAccountManager().getPlayerData(target.getUniqueId());
                if (coins == null) {
                    sender.sendMessage(utils.color(ConfigMessages.NO_ACCOUNT_OTHERS.toString())
                            .replace("%prefix%", utils.getPrefix())
                            .replace("%player%", args[1]));

                } else {

                    sender.sendMessage(utils.color(ConfigMessages.BALANCE_OTHERS.toString())
                            .replace("%prefix%", utils.getPrefix())
                            .replace("%coins%", df.format(coins.getMoney()))
                            .replace("%player%", args[1]));

                }

            }

        }

    }

}
