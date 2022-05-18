package com.quandz.krakenmobcoins.manager;

import com.quandz.api.MobCoinsExpansion;
import com.quandz.krakenmobcoins.MobCoins;
import com.quandz.krakenmobcoins.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

public class DependencyManager {

    private final MobCoins plugin;
    private boolean wildStacker = false;
    private boolean roseStacker = false;

    public DependencyManager(final MobCoins plugin) {
        this.plugin = plugin;
    }

    public void setupDependency() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        Utils utils = plugin.getUtils();
        int totalHooks = 0;

        if (pluginManager.getPlugin("PlaceholderAPI") != null) {
            totalHooks++;
            new MobCoinsExpansion(plugin).register();
            utils.sendConsoleMessage("PlaceholderAPI found, enabling hooks!");
        }
        if (pluginManager.getPlugin("WildStacker") != null) {
            totalHooks++;
            setWildStacker(true);
            utils.sendConsoleMessage("WildStacker found, enabling hooks!");
        }
        if (pluginManager.getPlugin("RoseStacker") != null) {
            totalHooks++;
            setRoseStacker(true);
            utils.sendConsoleMessage("§cRoseStacker found, enabling hooks!");
        }
        utils.sendConsoleMessage("Successfully hooked " + totalHooks + " plugins, enjoy!");
    }

    public boolean isRoseStacker() {
        return roseStacker;
    }
    public void setRoseStacker(boolean roseStacker) {
        this.roseStacker = roseStacker;
    }
    public boolean isWildStacker() {
        return wildStacker;
    }
    public void setWildStacker(boolean wildStacker) {
        this.wildStacker = wildStacker;
    }
}
