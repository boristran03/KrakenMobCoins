package com.quandz.krakenmobcoins.utils;

import com.cryptomorin.xseries.messages.Titles;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.quandz.krakenmobcoins.MobCoins;
import com.quandz.krakenmobcoins.enums.ConfigMessages;
import com.quandz.krakenmobcoins.shops.CategoryInventory;
import com.quandz.krakenmobcoins.shops.NormalShopInventory;
import com.quandz.krakenmobcoins.shops.RotatingShopInventory;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public record Utils(MobCoins plugin) {

    public String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public List<String> color(List<String> strings) {
        return strings.stream().map(this::color).collect(Collectors.toList());
    }

    public void sendConsoleMessage(String string) {
        System.out.println(color("[KrakenMobCoins] " + string));
    }

    public String getPrefix() {
        return color(ConfigMessages.PREFIX.toString());
    }

    public boolean isDouble(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public DecimalFormat getDecimalFormat() {
        return new DecimalFormat("###,###,###,###,###.##");
    }

    private String format(double d) {
        NumberFormat format = NumberFormat.getInstance(Locale.ENGLISH);
        format.setMaximumFractionDigits(2);
        format.setMinimumFractionDigits(0);
        return format.format(d);
    }

    private void setSkinViaBase64(SkullMeta meta, String base64) {
        try {
            Method setProfile = meta.getClass().getDeclaredMethod("setProfile", GameProfile.class);
            setProfile.setAccessible(true);
            GameProfile profile = new GameProfile(UUID.randomUUID(), "skull-texture");
            profile.getProperties().put("textures", new Property("textures", base64));

            setProfile.invoke(meta, profile);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            plugin.getLogger().log(Level.SEVERE, "There was a severe internal reflection error when attempting to set the skin of a player skull via base64!");
            e.printStackTrace();
        }
    }

    private String toLong(double amt) {
        return String.valueOf((long) amt);
    }

    public ItemStack getMobCoinItem(double amount) {
        ConfigUtils config = ConfigUtils.getInstance();
        Utils utils = plugin.getUtils();

        String material = config.mobCoinsItemMaterial;
        if (material == null) throw new NullPointerException("Material name cannot be null");

        String name = config.mobCoinsItemName;
        List<String> lore = new ArrayList<>();
        config.mobCoinsItemLore.forEach((line) -> lore.add(line.replace("%coins%", this.getDecimalFormat().format(amount))));

        ItemStack item = null;
        if (material.contains(";")) {
            String[] split = material.split(";");
            if (split[0].equalsIgnoreCase("head")) {

                String texture = split[1];
                item = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
                setSkinViaBase64(Objects.requireNonNull(skullMeta), texture);
                skullMeta.setDisplayName(utils.color(name));
                skullMeta.setLore(utils.color(lore));
                item.setItemMeta(skullMeta);

            }

        } else {
            Material parsedMaterial = Material.matchMaterial(material);

            if (parsedMaterial == null) {
                throw new NullPointerException("Wrong material name!");
            }
            item = new ItemStack(parsedMaterial);
            ItemMeta itemMeta = item.getItemMeta();
            Objects.requireNonNull(itemMeta).setDisplayName(utils.color(name));
            itemMeta.setLore(utils.color(lore));
            item.setItemMeta(itemMeta);
        }

        NBTItem nbtItem = new NBTItem(Objects.requireNonNull(item));
        nbtItem.setDouble("amount", amount);
        nbtItem.setString("info", "krakenmobcoins");
        return nbtItem.getItem();
    }

    public boolean isSupportVersion() {
        return Bukkit.getVersion().contains("1.16") ||
                Bukkit.getVersion().contains("1.17") ||
                Bukkit.getVersion().contains("1.18");

    }

    public int[] getRemainingTime(long remaining) {
        int days = (int) (remaining / 86400000L);
        int hours = (int) (remaining % 86400000L) / 3600000;
        int minutes = (int) (remaining % 3600000L / 60000L);
        int seconds = (int) (remaining % 60000L / 1000L);
        return new int[]{days, hours, minutes, seconds};
    }

    public String formatTime(long remaining) {
        int[] remainingTime = getRemainingTime(remaining);
        StringBuilder string = new StringBuilder();
        for (int i = 0; i < remainingTime.length; i++) {
            if (remainingTime[i] != 0) {
                String s = "";
                switch (i) {
                    case 0 -> s = "d";
                    case 1 -> s = "h";
                    case 2 -> s = "m";
                    case 3 -> s = "s";
                }
                if (string.length() == 0) {
                    string = new StringBuilder(remainingTime[i] + s);
                } else {
                    string.append(" ").append(remainingTime[i]).append(s);
                }
            }
        }
        return string.toString();
    }

    public void openShopMenu(Player player) {
        FileConfiguration config = plugin.getConfig();
        if (config.getBoolean("rotatingShop.enabled")) {
            String title = color(config.getString("rotatingShop.title"));
            int size = config.getInt("rotatingShop.size");

            new RotatingShopInventory(size, title, player, plugin).open(player);
        } else {
            String title = color(config.getString("normalShop.title"));
            int size = config.getInt("normalShop.size");

            new CategoryInventory(plugin, size, title, player).open(player);
        }

    }

    public void openCategory(String category, Player player) {
        FileConfiguration config = plugin.getConfig();
        String finalCategory = category + ".yml";
        if (plugin.getCategoryManager().isCategoryExist(finalCategory)) {
            FileConfiguration configuration = plugin.getCategoryManager().getCategory(finalCategory);
            String title = color(configuration.getString("title"));
            int size = configuration.getInt("size");

            new NormalShopInventory(size, title, finalCategory, player, plugin).open(player);

        } else {

            player.sendMessage(color(config.getString("messages.categoryNotExist"))
                    .replace("%prefix%", getPrefix())
                    .replace("%category%", category));

        }
    }

    public void exampleShop(File file) {

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.set("title", "Shop Menu");
        config.set("size", 36);

        // Vote Key
        List<String> lore = new ArrayList<>();
        lore.add("&7You can use this key on /crates");
        lore.add("");
        lore.add(" &7Price: &610.25 coins");
        lore.add(" &7Limit: &6%limit% / %maxLimit%");
        lore.add("");
        lore.add("&a&lCLICK TO PURCHASE!");

        List<String> commands = new ArrayList<>();
        commands.add("give %player% TRIPWIRE_HOOK 1");
        commands.add("broadcast &a&l%player% just bought 1x Vote Key!");

        config.set("items.voteKey.type", "shop");
        config.set("items.voteKey.material", "TRIPWIRE_HOOK");
        config.set("items.voteKey.amount", 1);
        config.set("items.voteKey.slot", 10);
        config.set("items.voteKey.price", 10.25);
        config.set("items.voteKey.limit", 3);
        config.set("items.voteKey.useStock", true);
        config.set("items.voteKey.stock", 5);
        config.set("items.voteKey.glow", false);
        config.set("items.voteKey.name", "&aVote Key");
        config.set("items.voteKey.lore", lore);
        config.set("items.voteKey.commands", commands);


        // Border
        List<Integer> slots = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35);

        config.set("items.borderItem.type", "dummy");
        config.set("items.borderItem.material", "BLACK_STAINED_GLASS_PANE");
        config.set("items.borderItem.amount", 1);
        config.set("items.borderItem.name", "&f");
        config.set("items.borderItem.slots", slots);

        // Back Button
        List<String> list = new ArrayList<>();
        list.add("&7Click to go back to categories menu!");

        config.set("items.backButton.type", "back");
        config.set("items.backButton.material", "BARRIER");
        config.set("items.backButton.amount", 1);
        config.set("items.backButton.slot", 31);
        config.set("items.backButton.name", "&cBack");
        config.set("items.backButton.lore", list);


        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public int getBooster(Player player) {
        for (PermissionAttachmentInfo perm : player.getEffectivePermissions()) {
            if (perm.getPermission().startsWith("krakenmobcoins.booster.")) {
                String permission = perm.getPermission().replace(".", ";");
                String[] split = permission.split(";");
                return Integer.parseInt(split[2]);
            }
        }
        return 0;
    }

    public void sendSound(Player player) {
        ConfigUtils config = ConfigUtils.getInstance();
        if (config.enabledReceivedMobCoinsSound) {
            String name = config.receivedMobCoinsSound.toUpperCase();
            float volume = (float) config.receivedMobCoinsSoundVolume;
            float pitch = (float) config.receivedMobCoinsSoundPitch;
            player.playSound(player.getLocation(), name, volume, pitch);
        }
    }

    public void sendMessage(Player player, double amount) {
        FileConfiguration config = plugin.getConfig();
        if (config.getBoolean("receivedMobCoins.message.enabled")) {
            String message = config.getString("receivedMobCoins.message.message");
            player.sendMessage(color(message)
                    .replace("%prefix%", getPrefix())
                    .replace("%amount%", this.getDecimalFormat().format(amount)));
        }
    }

    public void sendTitle(Player player, double amount) {
        ConfigUtils config = ConfigUtils.getInstance();
        if (config.enabledReceivedMobCoinsTitle) {

            int fadeIn = config.receivedMobCoinsTitleFadeIn;
            int stay = config.receivedMobCoinsTitleStay;
            int fadeOut = config.receivedMobCoinsTitleFadeOut;

            String title = config.receivedMobCoinsTitle.replace("%amount%", this.getDecimalFormat().format(amount));
            String subtitle = config.receivedMobCoinsSubtitle.replace("%amount%", this.getDecimalFormat().format(amount));

            Titles.sendTitle(player, fadeIn, stay, fadeOut, color(title), color(subtitle));

        }
    }

    public String formatShort(double d) {
        if (d < 1000L) {
            return format(d);
        }
        if (d < 1000000L) {
            return format(d / 1000L) + "K";
        }
        if (d < 1000000000L) {
            return format(d / 1000000L) + "M";
        }
        if (d < 1000000000000L) {
            return format(d / 1000000000L) + "B";
        }
        if (d < 1000000000000000L) {
            return format(d / 1000000000000L) + "T";
        }
        if (d < 1000000000000000000L) {
            return format(d / 1000000000000000L) + "Q";
        }
        return toLong(d);
    }

}
