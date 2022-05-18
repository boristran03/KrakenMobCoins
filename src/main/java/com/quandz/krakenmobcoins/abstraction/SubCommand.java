package com.quandz.krakenmobcoins.abstraction;

import com.quandz.krakenmobcoins.MobCoins;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class SubCommand {

    @Nullable
    public abstract String getPermission();

    @Nullable
    public abstract List<String> parseTabCompletions(MobCoins plugin, CommandSender sender, String[] args);

    public abstract void perform(MobCoins plugin, CommandSender sender, String[] args);

}
