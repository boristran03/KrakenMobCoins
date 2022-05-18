package com.quandz.krakenmobcoins.shops.items;

import java.util.List;

public record ShopItems(String configKey, String material, int amount, String name, boolean glowing, List<String> lore,
                        List<String> commands, double price, boolean special, int limit, boolean useStock, int stock) {

}
