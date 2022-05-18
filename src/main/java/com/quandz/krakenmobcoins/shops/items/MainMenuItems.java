package com.quandz.krakenmobcoins.shops.items;

import java.util.List;

public record MainMenuItems(String type, String material, String name, int slot, List<Integer> slots, List<String> lore,
                            boolean glow, String category) {

}
