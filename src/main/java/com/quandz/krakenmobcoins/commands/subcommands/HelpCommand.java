package com.quandz.krakenmobcoins.commands.subcommands;

import com.quandz.krakenmobcoins.MobCoins;
import com.quandz.krakenmobcoins.abstraction.SubCommand;
import com.quandz.krakenmobcoins.enums.ConfigMessagesList;
import com.quandz.krakenmobcoins.utils.Utils;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HelpCommand extends SubCommand {

    @Override
    public @Nullable String getPermission() {
        return null;
    }

    @Override
    public @Nullable List<String> parseTabCompletions(MobCoins plugin, CommandSender sender, String[] args) {
        return null;
    }


    @Override
    public void perform(MobCoins plugin, CommandSender sender, String[] args) {
        sendHelp(sender, plugin);
    }

    private void sendHelp(CommandSender sender, MobCoins plugin) {
        Utils utils = plugin.getUtils();

        if (sender.hasPermission("krakenmobcoins.admin")) {
            for (String message : ConfigMessagesList.HELP_ADMIN.toStringList()) {
                sender.sendMessage(utils.color(message));
            }
        } else {
            for (String message : ConfigMessagesList.HELP.toStringList()) {
                sender.sendMessage(utils.color(message));
            }
        }
    }

}
