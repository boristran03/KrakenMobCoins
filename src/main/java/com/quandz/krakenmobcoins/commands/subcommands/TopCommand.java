package com.quandz.krakenmobcoins.commands.subcommands;

import com.quandz.krakenmobcoins.MobCoins;
import com.quandz.krakenmobcoins.abstraction.SubCommand;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TopCommand extends SubCommand {

    @Override
    public @Nullable String getPermission() {
        return "krakenmobcoins.top";
    }

    @Override
    public @Nullable List<String> parseTabCompletions(MobCoins plugin, CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public void perform(MobCoins plugin, CommandSender sender, String[] args) {
        sender.sendMessage("§cTính năng đã bị xóa để tối ưu plugin! hãy sử dụng leaderboard thay thế");
        sender.sendMessage("§eKrakenMobCoins - Recoded By quanphungg_");
    }

}
