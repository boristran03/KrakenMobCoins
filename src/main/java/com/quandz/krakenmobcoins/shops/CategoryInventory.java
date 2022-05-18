package com.quandz.krakenmobcoins.shops;

import com.google.common.primitives.Ints;
import com.quandz.krakenmobcoins.MobCoins;
import com.quandz.krakenmobcoins.object.PlayerCoins;
import com.quandz.krakenmobcoins.shops.items.MainMenuItems;
import com.quandz.krakenmobcoins.utils.ItemBuilder;
import com.quandz.krakenmobcoins.utils.Utils;
import fr.mrmicky.fastinv.FastInv;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CategoryInventory extends FastInv {

    public CategoryInventory(MobCoins plugin, int size, String title, Player player) {
        super(size, title);

        PlayerCoins playerCoins = plugin.getAccountManager().getPlayerData(player.getUniqueId());
        Utils utils = plugin.getUtils();

        for (MainMenuItems item : plugin.getItemsLoader().getMainMenuItemsList()) {

            List<String> lore = new ArrayList<>();
            for (String line : item.lore()) {
                lore.add(line.replace("%coins%", utils.getDecimalFormat().format(playerCoins.getMoney())));
            }
            String category = item.category();
            ItemStack parsedItem = new ItemStack(Objects.requireNonNull(Material.matchMaterial(item.material())));
            ItemBuilder builder = ItemBuilder.start(parsedItem)
                    .name(item.name())
                    .lore(lore)
                    .flag(ItemFlag.HIDE_ATTRIBUTES);
            if (item.glow()) builder.enchant(Enchantment.ARROW_INFINITE).flag(ItemFlag.HIDE_ENCHANTS);
            ItemStack stack = builder.build();

            if (item.slots().isEmpty()) {
                setItem(item.slot(), stack, event -> {
                    if (item.type().equals("category")) {
                        utils.openCategory(category, player);
                    }
                });
            } else {
                setItems(Ints.toArray(item.slots()), stack, event -> {
                    if (item.type().equals("category")) {
                        utils.openCategory(category, player);
                    }
                });
            }

        }

    }

}
