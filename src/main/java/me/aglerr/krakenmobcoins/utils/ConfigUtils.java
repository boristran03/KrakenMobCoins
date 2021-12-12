package me.aglerr.krakenmobcoins.utils;

//this class coded by quanphungg_

import me.aglerr.krakenmobcoins.MobCoins;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class ConfigUtils {

    static ConfigUtils __instance;
    final MobCoins main = MobCoins.getInstance();

    public boolean disableMobCoinsFromSpawner;

    public boolean roseStackerSupport;

    public boolean wildStackerSupport;

    public boolean enabledAutoSave;

    public boolean enabledSalaryMode;

    public boolean receiveAfterMessage;

    public boolean enabledPhysicalCoin;

    public int autoSaveTaskTimer;

    public double startingBalance;

    public List<String> disabledWorlds;

    public static ConfigUtils getInstance() {
        return __instance == null ? new ConfigUtils() : __instance;
    }

    public void loadConfig() {
        FileConfiguration config = main.getConfig();
        disableMobCoinsFromSpawner = config.getBoolean("options.disableMobCoinsFromSpawner");
        wildStackerSupport = config.getBoolean("options.wildStackerSupport");
        roseStackerSupport = config.getBoolean("options.roseStackerSupport");
        enabledSalaryMode = config.getBoolean("options.salaryMode.enabled");
        receiveAfterMessage = config.getBoolean("options.salaryMode.receiveAfterMessage");
        disabledWorlds = config.getStringList("disabledWorlds");
        enabledPhysicalCoin = config.getBoolean("options.physicalMobCoin.enabled");
        autoSaveTaskTimer = config.getInt("autoSave.interval");
        startingBalance = config.getDouble("options.startingBalance");
    }
}
