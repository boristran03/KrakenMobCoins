package com.quandz.krakenmobcoins.shops.items;

import java.util.List;

public record RotatingItems(String material, String name, int slot, List<Integer> slots, List<String> lore,
                            boolean glow) {

}
