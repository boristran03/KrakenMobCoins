package com.quandz.krakenmobcoins.commands.subcommands;

import com.quandz.api.events.MobCoinsWithdrawEvent;
import com.quandz.krakenmobcoins.MobCoins;
import com.quandz.krakenmobcoins.abstraction.SubCommand;
import com.quandz.krakenmobcoins.enums.ConfigMessages;
import com.quandz.krakenmobcoins.object.PlayerCoins;
import com.quandz.krakenmobcoins.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class WithdrawCommand extends SubCommand {

    @Override
    public String getPermission() {
        return "krakenmobcoins.withdraw";
    }

    @Override
    public @Nullable List<String> parseTabCompletions(MobCoins plugin, CommandSender sender, String[] args) {
        if (args.length == 2) return Collections.singletonList("<amount>");
        return null;
    }

    @Override
    public void perform(MobCoins plugin, CommandSender sender, String[] args) {

        Utils utils = plugin.getUtils();

        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length < 2) {
                sender.sendMessage(utils.color("&cUsage: /mobcoins withdraw <amount>"));
                return;
            }

            PlayerCoins playerCoins = plugin.getAccountManager().getPlayerData(player.getUniqueId());
            if (playerCoins == null) {
                sender.sendMessage(utils.color(ConfigMessages.NO_ACCOUNT.toString())
                        .replace("%prefix%", utils.getPrefix()));
                return;
            }

            if (utils.isDouble(args[1])) {
                double amount = Double.parseDouble(args[1]);
                if (amount < 0) {
                    player.sendMessage(utils.color(ConfigMessages.NEGATIVE_AMOUNT.toString())
                            .replace("%prefix%", utils.getPrefix()));
                    return;
                }

                if (playerCoins.getMoney() >= amount) {
                    if (player.getInventory().firstEmpty() == -1) {
                        player.sendMessage(utils.color(ConfigMessages.INVENTORY_FULL.toString())
                                .replace("%prefix%", utils.getPrefix()));
                        return;
                    }

                    MobCoinsWithdrawEvent mobCoinsWithdrawEvent = new MobCoinsWithdrawEvent(player, amount, utils.getMobCoinItem(amount));
                    Bukkit.getPluginManager().callEvent(mobCoinsWithdrawEvent);
                    if (mobCoinsWithdrawEvent.isCancelled()) return;

                    playerCoins.setMoney(playerCoins.getMoney() - mobCoinsWithdrawEvent.getAmount());
                    player.getInventory().addItem(utils.getMobCoinItem(mobCoinsWithdrawEvent.getAmount()));
                    player.sendMessage(utils.color(ConfigMessages.WITHDRAW.toString())
                            .replace("%prefix%", utils.getPrefix())
                            .replace("%coins%", String.valueOf(mobCoinsWithdrawEvent.getAmount())));
                    player.updateInventory();

                } else {
                    player.sendMessage(utils.color(ConfigMessages.NOT_ENOUGH_COINS.toString())
                            .replace("%prefix%", utils.getPrefix()));
                }

            } else {
                player.sendMessage(utils.color(ConfigMessages.NOT_INTEGER.toString())
                        .replace("%prefix%", utils.getPrefix()));
            }

        } else {
            sender.sendMessage(utils.color(ConfigMessages.ONLY_PLAYER.toString())
                    .replace("%player%", utils.getPrefix()));
        }
    }
}
