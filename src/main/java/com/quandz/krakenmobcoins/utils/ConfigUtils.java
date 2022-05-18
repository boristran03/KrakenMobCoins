package com.quandz.krakenmobcoins.utils;

//this class coded by quanphungg_

import com.quandz.krakenmobcoins.MobCoins;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.entity.EntityDamageEvent;

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
    public int announceTaskTimer;
    public double startingBalance;
    public List<String> disabledWorlds;
    public List<EntityDamageEvent.DamageCause> damageCauses;
    public boolean ignoreDeathCause;
    public boolean enabledReceivedMobCoinsSound;
    public boolean enabledReceivedMobCoinsTitle;
    public String receivedMobCoinsTitle;
    public String receivedMobCoinsSubtitle;
    public int receivedMobCoinsTitleFadeIn;
    public int receivedMobCoinsTitleStay;
    public int receivedMobCoinsTitleFadeOut;
    public String receivedMobCoinsSound;
    public double receivedMobCoinsSoundVolume;
    public double receivedMobCoinsSoundPitch;
    public String mobCoinsItemMaterial;
    public String mobCoinsItemName;
    public List<String> mobCoinsItemLore;
    public boolean redeemSoundEnabled;
    public String redeemSoundName;
    public double redeemSoundPitch;
    public double redeemSoundVolume;

    private ConfigUtils() {
        __instance = this;
        loadConfig();
    }

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
        enabledAutoSave = config.getBoolean("autoSave.enabled");
        autoSaveTaskTimer = config.getInt("autoSave.interval");
        startingBalance = config.getDouble("options.startingBalance");
        announceTaskTimer = config.getInt("options.salaryMode.announceEvery");
        ignoreDeathCause = config.getBoolean("options.physicalMobCoin.ignoreDeathCause");
        enabledReceivedMobCoinsTitle = config.getBoolean("receivedMobCoins.title.enabled");
        receivedMobCoinsTitle = config.getString("receivedMobCoins.title.titles.title");
        receivedMobCoinsSubtitle = config.getString("receivedMobCoins.title.titles.subtitle");
        receivedMobCoinsTitleFadeIn = config.getInt("receivedMobCoins.title.titles.fadeIn");
        receivedMobCoinsTitleStay = config.getInt("receivedMobCoins.title.titles.stay");
        receivedMobCoinsTitleFadeOut = config.getInt("receivedMobCoins.title.titles.fadeOut");
        enabledReceivedMobCoinsSound = config.getBoolean("receivedMobCoins.sound.enabled");
        receivedMobCoinsSound = config.getString("receivedMobCoins.sound.name");
        receivedMobCoinsSoundVolume = config.getDouble("receivedMobCoins.sound.volume");
        receivedMobCoinsSoundPitch = config.getDouble("receivedMobCoins.sound.pitch");
        mobCoinsItemMaterial = config.getString("mobcoinItem.material");
        mobCoinsItemName = config.getString("mobcoinItem.name");
        mobCoinsItemLore = config.getStringList("mobcoinItem.lore");
        redeemSoundEnabled = config.getBoolean("sounds.onRedeem.enabled");
        redeemSoundName = config.getString("sounds.onRedeem.name");
        redeemSoundPitch = config.getDouble("sounds.onRedeem.volume");
        redeemSoundVolume = config.getDouble("sounds.onRedeem.pitch");
        damageCauses = config.getStringList("options.physicalMobCoin.deathCause")
                .stream().map(EntityDamageEvent.DamageCause::valueOf).toList();
    }
}
