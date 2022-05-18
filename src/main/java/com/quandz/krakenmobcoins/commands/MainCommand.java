package com.quandz.krakenmobcoins.commands;

import com.quandz.krakenmobcoins.MobCoins;
import com.quandz.krakenmobcoins.abstraction.SubCommand;
import com.quandz.krakenmobcoins.commands.subcommands.*;
import com.quandz.krakenmobcoins.enums.ConfigMessages;
import com.quandz.krakenmobcoins.enums.ConfigMessagesList;
import com.quandz.krakenmobcoins.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class MainCommand implements CommandExecutor, TabCompleter {

    private final Map<String, SubCommand> subCommands = new HashMap<>();

    private final MobCoins plugin;

    public MainCommand(final MobCoins plugin) {
        this.plugin = plugin;

        AddCommand addCommand = new AddCommand();
        BalanceCommand balanceCommand = new BalanceCommand();
        CategoryCommand categoryCommand = new CategoryCommand();
        HelpCommand helpCommand = new HelpCommand();
        PayCommand payCommand = new PayCommand();
        RefreshCommand refreshCommand = new RefreshCommand();
        ReloadCommand reloadCommand = new ReloadCommand();
        RemoveCommand removeCommand = new RemoveCommand();
        SetCommand setCommand = new SetCommand();
        ShopCommand shopCommand = new ShopCommand();
        ToggleCommand toggleCommand = new ToggleCommand();
        TopCommand topCommand = new TopCommand();
        WithdrawCommand withdrawCommand = new WithdrawCommand();

        subCommands.put("add", addCommand);

        subCommands.put("balance", balanceCommand);
        subCommands.put("bal", balanceCommand);

        subCommands.put("category", categoryCommand);
        subCommands.put("help", helpCommand);
        subCommands.put("pay", payCommand);
        subCommands.put("refresh", refreshCommand);
        subCommands.put("reload", reloadCommand);
        subCommands.put("remove", removeCommand);
        subCommands.put("set", setCommand);
        subCommands.put("shop", shopCommand);
        subCommands.put("toggle", toggleCommand);
        subCommands.put("top", topCommand);

        subCommands.put("withdraw", withdrawCommand);
        subCommands.put("wd", withdrawCommand);

    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {

        Utils utils = plugin.getUtils();

        if (args.length > 0) {
            SubCommand subCommand = subCommands.get(args[0].toLowerCase());
            if (subCommand != null) {
                if (subCommand.getPermission() != null) {
                    if (!(sender.hasPermission(subCommand.getPermission()))) {
                        sender.sendMessage(utils.color(ConfigMessages.NO_PERMISSION.toString())
                                .replace("%prefix%", utils.getPrefix())
                                .replace("%permission%", subCommand.getPermission()));
                        return true;
                    }
                }

                subCommand.perform(plugin, sender, args);
                return true;

            }

        } else {
            this.sendHelp(sender);
        }

        return false;
    }

    private void sendHelp(CommandSender sender) {
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

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {

        if (args.length == 1) {
            List<String> suggestions = new ArrayList<>();
            if (sender.hasPermission("krakenmobcoins.admin")) {
                suggestions.addAll(Arrays.asList("add", "category", "convert", "refresh", "reload",
                        "remove", "set"));
            }
            suggestions.addAll(Arrays.asList("balance", "help", "pay", "shop", "toggle", "top", "withdraw"));
            return suggestions;
        } else {

            return switch (args[0].toLowerCase()) {
                case "add" -> subCommands.get("add").parseTabCompletions(plugin, sender, args);
                case "balance" -> subCommands.get("balance").parseTabCompletions(plugin, sender, args);
                case "bal" -> subCommands.get("bal").parseTabCompletions(plugin, sender, args);
                case "category" -> subCommands.get("category").parseTabCompletions(plugin, sender, args);
                case "convert" -> subCommands.get("convert").parseTabCompletions(plugin, sender, args);
                case "help" -> subCommands.get("help").parseTabCompletions(plugin, sender, args);
                case "pay" -> subCommands.get("pay").parseTabCompletions(plugin, sender, args);
                case "refresh" -> subCommands.get("refresh").parseTabCompletions(plugin, sender, args);
                case "reload" -> subCommands.get("reload").parseTabCompletions(plugin, sender, args);
                case "remove" -> subCommands.get("remove").parseTabCompletions(plugin, sender, args);
                case "set" -> subCommands.get("set").parseTabCompletions(plugin, sender, args);
                case "shop" -> subCommands.get("shop").parseTabCompletions(plugin, sender, args);
                case "toggle" -> subCommands.get("toggle").parseTabCompletions(plugin, sender, args);
                case "top" -> subCommands.get("top").parseTabCompletions(plugin, sender, args);
                case "withdraw" -> subCommands.get("withdraw").parseTabCompletions(plugin, sender, args);
                case "wd" -> subCommands.get("wd").parseTabCompletions(plugin, sender, args);
                default -> null;
            };

        }

    }
}
