package com.quandz.krakenmobcoins.shops;

import com.cryptomorin.xseries.XMaterial;
import com.google.common.primitives.Ints;
import com.quandz.krakenmobcoins.MobCoins;
import com.quandz.krakenmobcoins.manager.AccountManager;
import com.quandz.krakenmobcoins.manager.ItemStockManager;
import com.quandz.krakenmobcoins.manager.PurchaseLimitManager;
import com.quandz.krakenmobcoins.object.PlayerCoins;
import com.quandz.krakenmobcoins.shops.items.RotatingItems;
import com.quandz.krakenmobcoins.shops.items.ShopItems;
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

public class RotatingShopInventory extends FastInv {

    public RotatingShopInventory(int size, String title, Player player, MobCoins plugin) {
        super(size, title);

        final ItemStockManager stockManager = plugin.getItemStockManager();
        final AccountManager accountManager = plugin.getAccountManager();

        FileConfiguration config = plugin.getConfig();
        PurchaseLimitManager limitManager = plugin.getPurchaseLimitManager();
        Utils utils = plugin.getUtils();

        PlayerCoins playerCoins = accountManager.getPlayerData(player.getUniqueId());

        List<Integer> normalSlots = config.getIntegerList("rotatingShop.normalItemSlots");

        int normalCounter = 0;
        for (ShopItems normal : plugin.getRotatingManager().getNormalItems()) {

            List<String> lore = new ArrayList<>();
            if (normal.useStock()) {

                int finalStock = 0;
                if (stockManager.isItemExist(normal.configKey()))
                    finalStock = stockManager.getItemStock(normal.configKey());
                if (!stockManager.isItemExist(normal.configKey())) {
                    finalStock = normal.stock();
                    stockManager.setStock(normal.configKey(), normal.stock());
                }

                String placeholder = null;
                if (finalStock <= 0) placeholder = config.getString("placeholders.outOfStock");
                if (finalStock > 0) placeholder = String.valueOf(finalStock);
                if (placeholder == null) placeholder = "Placeholder Error!";

                String limit = null;
                if (limitManager.containsLimit(player.getUniqueId(), normal.configKey()))
                    limit = String.valueOf(limitManager.getLimit(player.getUniqueId(), normal.configKey()));
                if (limit == null) limit = "0";

                for (String line : normal.lore()) {
                    lore.add(line.replace("%maxLimit%", String.valueOf(normal.limit()))
                            .replace("%limit%", limit)
                            .replace("%stock%", placeholder)
                            .replace("%price%", String.valueOf(normal.price())));
                }
            } else {

                String limit = null;
                if (limitManager.containsLimit(player.getUniqueId(), normal.configKey()))
                    limit = String.valueOf(limitManager.getLimit(player.getUniqueId(), normal.configKey()));
                if (limit == null) limit = "0";

                for (String line : normal.lore()) {
                    lore.add(line.replace("%maxLimit%", String.valueOf(normal.limit()))
                            .replace("%limit%", limit)
                            .replace("%price%", String.valueOf(normal.price())));
                }
            }

            ItemBuilder builder = ItemBuilder.start(XMaterial.matchXMaterial(normal.material()).get().parseItem())
                    .name(normal.name())
                    .amount(normal.amount())
                    .lore(lore)
                    .flag(ItemFlag.HIDE_ATTRIBUTES);

            if (normal.glowing()) builder.enchant(Enchantment.ARROW_INFINITE).flag(ItemFlag.HIDE_ENCHANTS);
            ItemStack stack = builder.build();

            this.setItem(normalSlots.get(normalCounter), stack, event -> plugin.getShopUtils().buyHandler(normal, player, stack));
            normalCounter++;

            if (normalCounter == normalSlots.size()) break;
        }

        int specialCounter = 0;
        for (ShopItems special : plugin.getRotatingManager().getSpecialItems()) {
            List<Integer> specialSlots = config.getIntegerList("rotatingShop.specialItemSlots");

            List<String> lore = new ArrayList<>();
            if (special.useStock()) {

                int finalStock = 0;
                if (stockManager.isItemExist(special.configKey()))
                    finalStock = stockManager.getItemStock(special.configKey());
                if (!stockManager.isItemExist(special.configKey())) {
                    finalStock = special.stock();
                    stockManager.setStock(special.configKey(), special.stock());
                }

                String placeholder = null;
                if (finalStock <= 0) placeholder = config.getString("placeholders.outOfStock");
                if (finalStock > 0) placeholder = String.valueOf(finalStock);
                if (placeholder == null) placeholder = "Placeholder Error!";

                String limit = null;
                if (limitManager.containsLimit(player.getUniqueId(), special.configKey()))
                    limit = String.valueOf(limitManager.getLimit(player.getUniqueId(), special.configKey()));
                if (limit == null) limit = "0";

                for (String line : special.lore()) {
                    lore.add(line.replace("%maxLimit%", String.valueOf(special.limit()))
                            .replace("%limit%", limit)
                            .replace("%stock%", placeholder)
                            .replace("%price%", String.valueOf(special.price())));
                }

            } else {

                String limit = null;
                if (limitManager.containsLimit(player.getUniqueId(), special.configKey()))
                    limit = String.valueOf(limitManager.getLimit(player.getUniqueId(), special.configKey()));
                if (limit == null) limit = "0";

                for (String line : special.lore()) {
                    lore.add(line.replace("%maxLimit%", String.valueOf(special.limit()))
                            .replace("%limit%", limit)
                            .replace("%price%", String.valueOf(special.price())));
                }

            }

            ItemBuilder builder = ItemBuilder.start(XMaterial.matchXMaterial(special.material()).get().parseItem())
                    .name(special.name())
                    .amount(special.amount())
                    .lore(lore)
                    .flag(ItemFlag.HIDE_ATTRIBUTES);

            if (special.glowing()) builder.enchant(Enchantment.ARROW_INFINITE).flag(ItemFlag.HIDE_ENCHANTS);
            ItemStack stack = builder.build();

            this.setItem(specialSlots.get(specialCounter), stack, event -> plugin.getShopUtils().buyHandler(special, player, stack));
            specialCounter++;

            if (specialCounter == specialSlots.size()) break;

        }

        for (RotatingItems items : plugin.getItemsLoader().getRotatingItemsList()) {

            List<String> lore = new ArrayList<>();
            for (String line : items.lore()) {
                lore.add(line.replace("%coins%", utils.getDecimalFormat().format(playerCoins.getMoney()))
                        .replace("%timeNormal%", plugin.getRotatingManager().getFormattedResetTime(false))
                        .replace("%timeSpecial%", plugin.getRotatingManager().getFormattedResetTime(true)));
            }

            ItemBuilder builder = ItemBuilder.start(XMaterial.matchXMaterial(items.material()).get().parseItem())
                    .name(items.name())
                    .lore(lore)
                    .flag(ItemFlag.HIDE_ATTRIBUTES);
            if (items.glow()) builder.enchant(Enchantment.ARROW_INFINITE).flag(ItemFlag.HIDE_ATTRIBUTES);
            ItemStack stack = builder.build();

            if (items.slots().isEmpty()) {
                setItem(items.slot(), stack);
            } else {
                setItems(Ints.toArray(items.slots()), stack);
            }

        }

        if (config.getBoolean("options.autoUpdateGUI.enabled")) {
            int tick = config.getInt("options.autoUpdateGUI.updateEvery");
            BukkitTask task = Bukkit.getServer().getScheduler().runTaskTimer(plugin, () -> {
                int taskNormal = 0;
                for (ShopItems normal : plugin.getRotatingManager().getNormalItems()) {

                    List<String> lore = new ArrayList<>();
                    if (normal.useStock()) {

                        int finalStock = 0;
                        if (stockManager.isItemExist(normal.configKey()))
                            finalStock = stockManager.getItemStock(normal.configKey());
                        if (!stockManager.isItemExist(normal.configKey())) {
                            finalStock = normal.stock();
                            stockManager.setStock(normal.configKey(), normal.stock());
                        }

                        String placeholder = null;
                        if (finalStock <= 0) placeholder = config.getString("placeholders.outOfStock");
                        if (finalStock > 0) placeholder = String.valueOf(finalStock);
                        if (placeholder == null) placeholder = "Placeholder Error!";

                        String limit = null;
                        if (limitManager.containsLimit(player.getUniqueId(), normal.configKey()))
                            limit = String.valueOf(limitManager.getLimit(player.getUniqueId(), normal.configKey()));
                        if (limit == null) limit = "0";

                        for (String line : normal.lore()) {
                            lore.add(line.replace("%maxLimit%", String.valueOf(normal.limit()))
                                    .replace("%limit%", limit)
                                    .replace("%stock%", placeholder)
                                    .replace("%price%", String.valueOf(normal.price())));
                        }
                    } else {

                        String limit = null;
                        if (limitManager.containsLimit(player.getUniqueId(), normal.configKey()))
                            limit = String.valueOf(limitManager.getLimit(player.getUniqueId(), normal.configKey()));
                        if (limit == null) limit = "0";

                        for (String line : normal.lore()) {
                            lore.add(line.replace("%maxLimit%", String.valueOf(normal.limit()))
                                    .replace("%limit%", limit)
                                    .replace("%price%", String.valueOf(normal.price())));
                        }
                    }

                    ItemBuilder builder = ItemBuilder.start(XMaterial.matchXMaterial(normal.material()).get().parseItem())
                            .name(normal.name())
                            .amount(normal.amount())
                            .lore(lore)
                            .flag(ItemFlag.HIDE_ATTRIBUTES);

                    if (normal.glowing()) builder.enchant(Enchantment.ARROW_INFINITE).flag(ItemFlag.HIDE_ENCHANTS);
                    ItemStack stack = builder.build();

                    this.setItem(normalSlots.get(taskNormal), stack, event -> plugin.getShopUtils().buyHandler(normal, player, stack));
                    taskNormal++;

                    if (taskNormal == normalSlots.size()) break;
                }

                int taskSpecial = 0;
                for (ShopItems special : plugin.getRotatingManager().getSpecialItems()) {
                    List<Integer> specialSlots = config.getIntegerList("rotatingShop.specialItemSlots");

                    List<String> lore = new ArrayList<>();
                    if (special.useStock()) {

                        int finalStock = 0;
                        if (stockManager.isItemExist(special.configKey()))
                            finalStock = stockManager.getItemStock(special.configKey());
                        if (!stockManager.isItemExist(special.configKey())) {
                            finalStock = special.stock();
                            stockManager.setStock(special.configKey(), special.stock());
                        }

                        String placeholder = null;
                        if (finalStock <= 0) placeholder = config.getString("placeholders.outOfStock");
                        if (finalStock > 0) placeholder = String.valueOf(finalStock);
                        if (placeholder == null) placeholder = "Placeholder Error!";

                        String limit = null;
                        if (limitManager.containsLimit(player.getUniqueId(), special.configKey()))
                            limit = String.valueOf(limitManager.getLimit(player.getUniqueId(), special.configKey()));
                        if (limit == null) limit = "0";

                        for (String line : special.lore()) {
                            lore.add(line.replace("%maxLimit%", String.valueOf(special.limit()))
                                    .replace("%limit%", limit)
                                    .replace("%stock%", placeholder)
                                    .replace("%price%", String.valueOf(special.price())));
                        }

                    } else {

                        String limit = null;
                        if (limitManager.containsLimit(player.getUniqueId(), special.configKey()))
                            limit = String.valueOf(limitManager.getLimit(player.getUniqueId(), special.configKey()));
                        if (limit == null) limit = "0";

                        for (String line : special.lore()) {
                            lore.add(line.replace("%maxLimit%", String.valueOf(special.limit()))
                                    .replace("%limit%", limit)
                                    .replace("%price%", String.valueOf(special.price())));
                        }

                    }

                    ItemBuilder builder = ItemBuilder.start(XMaterial.matchXMaterial(special.material()).get().parseItem())
                            .name(special.name())
                            .amount(special.amount())
                            .lore(lore)
                            .flag(ItemFlag.HIDE_ATTRIBUTES);

                    if (special.glowing()) builder.enchant(Enchantment.ARROW_INFINITE).flag(ItemFlag.HIDE_ENCHANTS);
                    ItemStack stack = builder.build();

                    this.setItem(specialSlots.get(taskSpecial), stack, event -> plugin.getShopUtils().buyHandler(special, player, stack));
                    taskSpecial++;

                    if (taskSpecial == specialSlots.size()) break;

                }

                for (RotatingItems items : plugin.getItemsLoader().getRotatingItemsList()) {

                    List<String> lore = new ArrayList<>();
                    for (String line : items.lore()) {
                        lore.add(line.replace("%coins%", utils.getDecimalFormat().format(playerCoins.getMoney()))
                                .replace("%timeNormal%", plugin.getRotatingManager().getFormattedResetTime(false))
                                .replace("%timeSpecial%", plugin.getRotatingManager().getFormattedResetTime(true)));
                    }

                    ItemBuilder builder = ItemBuilder.start(XMaterial.matchXMaterial(items.material()).get().parseItem())
                            .name(items.name())
                            .lore(lore)
                            .flag(ItemFlag.HIDE_ATTRIBUTES);
                    if (items.glow()) builder.enchant(Enchantment.ARROW_INFINITE).flag(ItemFlag.HIDE_ATTRIBUTES);
                    ItemStack stack = builder.build();

                    if (items.slots().isEmpty()) {
                        setItem(items.slot(), stack);
                    } else {
                        setItems(Ints.toArray(items.slots()), stack);
                    }

                }
            }, 0L, tick);
            this.addCloseHandler(event -> task.cancel());
        }

    }

}
