package com.quandz.krakenmobcoins.shops;

import com.quandz.krakenmobcoins.MobCoins;
import com.quandz.krakenmobcoins.enums.ConfigMessages;
import com.quandz.krakenmobcoins.manager.AccountManager;
import com.quandz.krakenmobcoins.manager.ItemStockManager;
import com.quandz.krakenmobcoins.manager.PurchaseLimitManager;
import com.quandz.krakenmobcoins.object.PlayerCoins;
import com.quandz.krakenmobcoins.shops.items.ShopItems;
import com.quandz.krakenmobcoins.shops.items.ShopNormalItems;
import com.quandz.krakenmobcoins.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public record ShopUtils(MobCoins plugin) {

    public void buyHandler(ShopItems items, Player player, ItemStack stack) {

        ItemStockManager stockManager = plugin.getItemStockManager();
        AccountManager accountManager = plugin.getAccountManager();
        PurchaseLimitManager limitManager = plugin.getPurchaseLimitManager();

        PlayerCoins playerCoins = accountManager.getPlayerData(player.getUniqueId());

        FileConfiguration config = plugin.getConfig();
        FileConfiguration shop = plugin.getShopManager().getConfiguration();

        Utils utils = plugin.getUtils();

        if (playerCoins.getMoney() >= items.price()) {
            if (config.getBoolean("options.confirmationMenu")) {

                int sizeConfirmation = shop.getInt("confirmationMenu.size");
                String titleConfirmation = utils.color(shop.getString("confirmationMenu.title"));
                new ConfirmationInventory(sizeConfirmation, titleConfirmation, stack, items.price(), items.commands(), items.configKey(), items.limit(), items.useStock(), plugin).open(player);

                return;
            }

            if (config.getBoolean("options.purchaseLimit") && items.limit() > 0) {
                if (limitManager.containsLimit(player.getUniqueId(), items.configKey())) {
                    int playerLimit = limitManager.getLimit(player.getUniqueId(), items.configKey());
                    if (playerLimit >= items.limit()) {
                        player.sendMessage(utils.color(ConfigMessages.MAX_LIMIT.toString())
                                .replace("%prefix%", utils.getPrefix()));
                        return;
                    }
                }
            }

            if (items.useStock()) {
                int currentStock = stockManager.getItemStock(items.configKey());
                if (currentStock <= 0) {
                    player.sendMessage(utils.color(ConfigMessages.OUT_OF_STOCK.toString())
                            .replace("%prefix%", utils.getPrefix()));
                    return;
                }
            }

            playerCoins.reduceMoney(items.price());

            player.sendMessage(utils.color(ConfigMessages.PURCHASED_ITEM.toString())
                    .replace("%prefix%", utils.getPrefix())
                    .replace("%item%", utils.color(items.name())));

            for (String command : items.commands()) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName()));
            }

            if (config.getBoolean("options.closeAfterPurchase")) {
                player.closeInventory();
            }

            if (items.useStock()) {
                int currentStock = stockManager.getItemStock(items.configKey());
                stockManager.setStock(items.configKey(), currentStock - 1);
            }

            if (config.getBoolean("options.purchaseLimit") && items.limit() > 0) {
                if (limitManager.containsLimit(player.getUniqueId(), items.configKey())) {
                    limitManager.incrementLimit(player.getUniqueId(), items.configKey());
                    return;
                }

                limitManager.modifyLimit(player.getUniqueId(), items.configKey(), 1);

            }

        } else {

            player.closeInventory();
            player.sendMessage(utils.color(ConfigMessages.NOT_ENOUGH_COINS.toString())
                    .replace("%prefix%", utils.getPrefix()));

        }

    }

    public void buyHandler(ShopNormalItems items, Player player, ItemStack stack) {

        ItemStockManager stockManager = plugin.getItemStockManager();
        AccountManager accountManager = plugin.getAccountManager();
        PurchaseLimitManager limitManager = plugin.getPurchaseLimitManager();

        PlayerCoins playerCoins = accountManager.getPlayerData(player.getUniqueId());

        FileConfiguration config = plugin.getConfig();
        FileConfiguration shop = plugin.getShopManager().getConfiguration();

        Utils utils = plugin.getUtils();

        if (playerCoins.getMoney() >= items.price()) {
            if (config.getBoolean("options.confirmationMenu")) {
                int sizeConfirmation = shop.getInt("confirmationMenu.size");
                String titleConfirmation = utils.color(shop.getString("confirmationMenu.title"));
                new ConfirmationInventory(sizeConfirmation, titleConfirmation, stack, items.price(), items.commands(), items.configKey(), items.limit(), items.useStock(), plugin).open(player);
                return;
            }

            if (config.getBoolean("options.purchaseLimit") && items.limit() > 0) {
                if (limitManager.containsLimit(player.getUniqueId(), items.configKey())) {
                    int playerLimit = limitManager.getLimit(player.getUniqueId(), items.configKey());
                    if (playerLimit >= items.limit()) {
                        player.sendMessage(utils.color(ConfigMessages.MAX_LIMIT.toString())
                                .replace("%prefix%", utils.getPrefix()));
                        return;
                    }
                }
            }

            if (items.useStock()) {
                int currentStock = stockManager.getItemStock(items.configKey());
                if (currentStock <= 0) {
                    player.sendMessage(utils.color(ConfigMessages.OUT_OF_STOCK.toString())
                            .replace("%prefix%", utils.getPrefix()));
                    return;
                }
            }

            playerCoins.setMoney(playerCoins.getMoney() - items.price());
            player.sendMessage(utils.color(ConfigMessages.PURCHASED_ITEM.toString())
                    .replace("%prefix%", utils.getPrefix())
                    .replace("%item%", utils.color(items.name())));

            for (String command : items.commands()) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player%", player.getName()));
            }

            if (config.getBoolean("options.closeAfterPurchase")) {
                player.closeInventory();
            }

            if (items.useStock()) {
                int currentStock = stockManager.getItemStock(items.configKey());
                stockManager.setStock(items.configKey(), currentStock - 1);
            }

            if (config.getBoolean("options.purchaseLimit") && items.limit() > 0) {
                if (limitManager.containsLimit(player.getUniqueId(), items.configKey())) {
                    limitManager.incrementLimit(player.getUniqueId(), items.configKey());
                    return;
                }

                limitManager.modifyLimit(player.getUniqueId(), items.configKey(), 1);

            }

        } else {

            player.closeInventory();
            player.sendMessage(utils.color(ConfigMessages.NOT_ENOUGH_COINS.toString())
                    .replace("%prefix%", utils.getPrefix()));

        }

    }

}
