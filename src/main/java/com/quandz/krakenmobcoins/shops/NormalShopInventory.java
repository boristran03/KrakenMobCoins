package com.quandz.krakenmobcoins.shops;

import com.cryptomorin.xseries.XMaterial;
import com.google.common.primitives.Ints;
import com.quandz.krakenmobcoins.MobCoins;
import com.quandz.krakenmobcoins.manager.ItemStockManager;
import com.quandz.krakenmobcoins.manager.PurchaseLimitManager;
import com.quandz.krakenmobcoins.object.PlayerCoins;
import com.quandz.krakenmobcoins.shops.items.ShopNormalItems;
import com.quandz.krakenmobcoins.utils.ItemBuilder;
import com.quandz.krakenmobcoins.utils.Utils;
import fr.mrmicky.fastinv.FastInv;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class NormalShopInventory extends FastInv {

    public NormalShopInventory(int size, String title, String category, Player player, MobCoins plugin) {
        super(size, title);

        FileConfiguration config = plugin.getConfig();
        Utils utils = plugin.getUtils();
        PurchaseLimitManager limitManager = plugin.getPurchaseLimitManager();
        PlayerCoins playerCoins = plugin.getAccountManager().getPlayerData(player.getUniqueId());
        final ItemStockManager stockManager = plugin.getItemStockManager();

        for (ShopNormalItems items : plugin.getItemsLoader().getShopNormalItemsList()) {

            List<String> lore = new ArrayList<>();
            if (items.useStock()) {
                int finalStock = 0;
                if (stockManager.isItemExist(items.configKey()))
                    finalStock = stockManager.getItemStock(items.configKey());

                if (!stockManager.isItemExist(items.configKey())) {
                    finalStock = items.stock();
                    stockManager.setStock(items.configKey(), items.stock());
                }

                String placeholder = null;
                if (finalStock <= 0) placeholder = config.getString("placeholders.outOfStock");
                if (finalStock > 0) placeholder = String.valueOf(finalStock);
                if (placeholder == null) placeholder = "Placeholder Error!";

                String limit = null;
                if (limitManager.containsLimit(player.getUniqueId(), items.configKey()))
                    limit = String.valueOf(limitManager.getLimit(player.getUniqueId(), items.configKey()));

                if (limit == null) limit = "0";

                for (String line : items.lore()) {
                    lore.add(line.replace("%maxLimit%", String.valueOf(items.limit()))
                            .replace("%limit%", limit)
                            .replace("%stock%", placeholder)
                            .replace("%coins%", utils.getDecimalFormat().format(playerCoins.getMoney()))
                            .replace("%price%", String.valueOf(items.price())));
                }

            } else {

                String limit = null;
                if (limitManager.containsLimit(player.getUniqueId(), items.configKey()))
                    limit = String.valueOf(limitManager.getLimit(player.getUniqueId(), items.configKey()));

                if (limit == null) limit = "0";

                for (String line : items.lore()) {
                    lore.add(line.replace("%maxLimit%", String.valueOf(items.limit()))
                            .replace("%limit%", limit)
                            .replace("%coins%", utils.getDecimalFormat().format(playerCoins.getMoney()))
                            .replace("%price%", String.valueOf(items.price())));
                }

            }

            if (items.category().equalsIgnoreCase(category)) {

                ItemBuilder builder = ItemBuilder.start(XMaterial.matchXMaterial(items.material()).get().parseItem())
                        .amount(items.amount())
                        .name(items.name())
                        .lore(lore)
                        .flag(ItemFlag.HIDE_ATTRIBUTES);
                if (items.glow()) builder.enchant(Enchantment.ARROW_INFINITE).flag(ItemFlag.HIDE_ENCHANTS);
                ItemStack stack = builder.build();

                if (items.slots().isEmpty()) {
                    setItem(items.slot(), stack, event -> {

                        if (items.type().equals("shop")) {
                            plugin.getShopUtils().buyHandler(items, player, stack);
                        }

                        if (items.type().equals("back")) {
                            utils.openShopMenu(player);
                        }

                    });
                } else {

                    setItems(Ints.toArray(items.slots()), stack, event -> {
                        if (items.type().equals("shop")) {

                            plugin.getShopUtils().buyHandler(items, player, stack);
                        }

                        if (items.type().equals("back")) {
                            utils.openShopMenu(player);
                        }

                    });
                }

            }


        }

        if (config.getBoolean("options.autoUpdateGUI.enabled")) {
            int tick = config.getInt("options.autoUpdateGUI.updateEvery");
            BukkitTask task = Bukkit.getServer().getScheduler().runTaskTimer(plugin, () -> {
                for (ShopNormalItems items : plugin.getItemsLoader().getShopNormalItemsList()) {

                    List<String> lore = new ArrayList<>();
                    if (items.useStock()) {
                        int finalStock = 0;
                        if (stockManager.isItemExist(items.configKey()))
                            finalStock = stockManager.getItemStock(items.configKey());

                        if (!stockManager.isItemExist(items.configKey())) {
                            finalStock = items.stock();
                            stockManager.setStock(items.configKey(), items.stock());
                        }

                        String placeholder = null;
                        if (finalStock <= 0) placeholder = config.getString("placeholders.outOfStock");
                        if (finalStock > 0) placeholder = String.valueOf(finalStock);
                        if (placeholder == null) placeholder = "Placeholder Error!";

                        String limit = null;
                        if (limitManager.containsLimit(player.getUniqueId(), items.configKey()))
                            limit = String.valueOf(limitManager.getLimit(player.getUniqueId(), items.configKey()));

                        if (limit == null) limit = "0";

                        for (String line : items.lore()) {
                            lore.add(line.replace("%maxLimit%", String.valueOf(items.limit()))
                                    .replace("%limit%", limit)
                                    .replace("%stock%", placeholder)
                                    .replace("%coins%", utils.getDecimalFormat().format(playerCoins.getMoney()))
                                    .replace("%price%", String.valueOf(items.price())));
                        }

                    } else {

                        String limit = null;
                        if (limitManager.containsLimit(player.getUniqueId(), items.configKey()))
                            limit = String.valueOf(limitManager.getLimit(player.getUniqueId(), items.configKey()));

                        if (limit == null) limit = "0";

                        for (String line : items.lore()) {
                            lore.add(line.replace("%maxLimit%", String.valueOf(items.limit()))
                                    .replace("%limit%", limit)
                                    .replace("%coins%", utils.getDecimalFormat().format(playerCoins.getMoney()))
                                    .replace("%price%", String.valueOf(items.price())));
                        }

                    }

                    if (items.category().equalsIgnoreCase(category)) {

                        ItemBuilder builder = ItemBuilder.start(XMaterial.matchXMaterial(items.material()).get().parseItem())
                                .amount(items.amount())
                                .name(items.name())
                                .lore(lore)
                                .flag(ItemFlag.HIDE_ATTRIBUTES);
                        if (items.glow()) builder.enchant(Enchantment.ARROW_INFINITE).flag(ItemFlag.HIDE_ENCHANTS);
                        ItemStack stack = builder.build();

                        if (items.slots().isEmpty()) {
                            setItem(items.slot(), stack, event -> {

                                if (items.type().equals("shop")) {
                                    plugin.getShopUtils().buyHandler(items, player, stack);
                                }

                                if (items.type().equals("back")) {
                                    utils.openShopMenu(player);
                                }

                            });
                        } else {

                            setItems(Ints.toArray(items.slots()), stack, event -> {
                                if (items.type().equals("shop")) {

                                    plugin.getShopUtils().buyHandler(items, player, stack);
                                }

                                if (items.type().equals("back")) {
                                    utils.openShopMenu(player);
                                }

                            });
                        }

                    }


                }
            }, 0L, tick);
            this.addCloseHandler(event -> task.cancel());
        }


    }
}
