package com.quandz.krakenmobcoins.shops.items;

import java.util.List;

public record ShopNormalItems(String category, String configKey, String type, String material, int amount, String name,
                              boolean glow, List<String> lore, List<String> commands, int slot, List<Integer> slots,
                              double price, int limit, boolean useStock, int stock) {

}
