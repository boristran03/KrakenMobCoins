package com.quandz.krakenmobcoins.configs;

import com.quandz.krakenmobcoins.MobCoins;
import com.quandz.krakenmobcoins.utils.Utils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ShopConfig {

    private final MobCoins plugin;
    public FileConfiguration data;
    public File cfg;

    public ShopConfig(final MobCoins plugin) {
        this.plugin = plugin;
    }

    public void setup() {
        Utils utils = plugin.getUtils();
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }

        cfg = new File(plugin.getDataFolder(), "shop.yml");

        if (!cfg.exists()) {
            plugin.saveResource("shop.yml", false);
            utils.sendConsoleMessage("shop.yml not found, creating shop.yml...");
        }

        data = YamlConfiguration.loadConfiguration(cfg);

    }

    public FileConfiguration getConfiguration() {
        return data;
    }

    public void saveData() {
        try {
            data.save(cfg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reloadData() {
        data = YamlConfiguration.loadConfiguration(cfg);
    }

}
