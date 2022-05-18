package com.quandz.krakenmobcoins;

import com.quandz.api.MobCoinsAPI;
import com.quandz.krakenmobcoins.coinmob.CoinMobManager;
import com.quandz.krakenmobcoins.commands.MainCommand;
import com.quandz.krakenmobcoins.configs.MobsConfig;
import com.quandz.krakenmobcoins.configs.ShopConfig;
import com.quandz.krakenmobcoins.configs.TempDataConfig;
import com.quandz.krakenmobcoins.enums.ConfigMessages;
import com.quandz.krakenmobcoins.enums.ConfigMessagesList;
import com.quandz.krakenmobcoins.listeners.*;
import com.quandz.krakenmobcoins.manager.*;
import com.quandz.krakenmobcoins.shops.ShopUtils;
import com.quandz.krakenmobcoins.shops.loader.ItemsLoader;
import com.quandz.krakenmobcoins.threads.AutoSaveThread;
import com.quandz.krakenmobcoins.threads.SalaryThread;
import com.quandz.krakenmobcoins.utils.ConfigUpdater;
import com.quandz.krakenmobcoins.utils.ConfigUtils;
import com.quandz.krakenmobcoins.utils.Metrics;
import com.quandz.krakenmobcoins.utils.Utils;
import fr.mrmicky.fastinv.FastInvManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class MobCoins extends JavaPlugin {

    private static MobCoins __instance;
    private static MobCoinsAPI api;
    private Set<UUID> mobSpawner;
    private TempDataConfig tempDataConfig;
    private MobsConfig mobsConfig;
    private ShopConfig shopConfig;
    private ItemsLoader itemsLoader;
    private ShopUtils shopUtils;
    private Utils utils;
    private CoinMobManager coinMobManager;
    private SalaryManager salaryManager;
    private DependencyManager dependencyManager;
    private ItemStockManager itemStockManager;
    private ToggleNotificationManager notificationManager;
    private RotatingManager rotatingManager;
    private CategoryManager categoryManager;
    private AccountManager accountManager;
    private PurchaseLimitManager limitManager;

    public static MobCoins getInstance() {
        return __instance;
    }

    public static MobCoinsAPI getAPI() {
        return api;
    }

    @Override
    public void onEnable() {
        __instance = this;
        createDatabaseFile();
        initPlugin();

        registerConfigs();
        updateConfigs();

        register();
        registerCommandsListeners();

        api = new MobCoinsAPI(this);

        sendInformation();
        getAccountManager().loadAll();

        if (!getUtils().isSupportVersion()) {
            getLogger().info("This plugin just work with version higher than 1.9");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    private void sendInformation() {
        getLogger().info("""
                 __  __  ____  ____   _____ ____ _____ _   _  _____\s
                |  \\/  |/ __ \\|  _ \\ / ____/ __ \\_   _| \\ | |/ ____|
                | \\  / | |  | | |_) | |   | |  | || | |  \\| | (___ \s
                | |\\/| | |  | |  _ <| |   | |  | || | | . ` |\\___ \\\s
                | |  | | |__| | |_) | |___| |__| || |_| |\\  |____) |
                |_|  |_|\\____/|____/ \\_____\\____/_____|_| \\_|_____/\s
                                                                   \s"""
                .indent(1));
        getLogger().info("Plugin đã được recode bởi quanphungg_");
    }

    @Override
    public void onDisable() {
        notificationManager.saveToggledListToConfig();
        rotatingManager.saveNormalAndSpecialTime();
        accountManager.saveAll();
        itemStockManager.saveStockToConfig();
        rotatingManager.saveRewards();
        limitManager.saveLimit();
    }

    private void register() {
        this.loadShop();
        notificationManager.loadToggledListFromConfig();
        coinMobManager.loadCoinMob();
        itemStockManager.loadStockFromConfig();
        limitManager.loadLimit();
        dependencyManager.setupDependency();

        ConfigMessages.initialize(this.getConfig());
        ConfigMessagesList.initialize(this.getConfig());

        new Metrics(this, 10310);
        FastInvManager.register(this);

        runThreads();
    }

    public void runThreads() {
        ConfigUtils config = ConfigUtils.getInstance();

        if (config.enabledSalaryMode) {
            int salaryDelay = config.announceTaskTimer;
            new SalaryThread(this, salaryDelay * 20L);
        }

        if (config.enabledAutoSave) {
            int saveDelay = config.autoSaveTaskTimer;
            new AutoSaveThread(this, saveDelay * 20L);
        }
    }

    private void loadShop() {
        if (this.getConfig().getBoolean("rotatingShop.enabled")) {

            rotatingManager.loadNormalAndSpecialTime();
            itemsLoader.loadRotatingItems();
            itemsLoader.loadShopItems();
            rotatingManager.loadRewards();

            Bukkit.getScheduler().runTask(this, rotatingManager::startCounting);

        } else {

            categoryManager.loadCategory();
            itemsLoader.loadMainMenu();
            itemsLoader.loadShopNormal();

        }
    }

    private void registerConfigs() {
        this.saveDefaultConfig();
        this.getConfig().options().copyDefaults(true);

        tempDataConfig.setup();
        mobsConfig.setup();
        shopConfig.setup();
    }

    public void reloadConfigs() {
        this.reloadConfig();
        mobsConfig.reloadData();
        shopConfig.reloadData();

        rotatingManager.saveRewards();
        itemsLoader.clearAllItemsList();
        categoryManager.clearCategory();
        rotatingManager.clearNormalAndSpecialItems();
        coinMobManager.clearCoinMob();

        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {

            itemsLoader.loadAllItemsList();

            rotatingManager.loadRewards();
            categoryManager.loadCategory();
            coinMobManager.loadCoinMob();

            ConfigMessages.initialize(this.getConfig());
            ConfigMessagesList.initialize(this.getConfig());

        }, 3L);

    }

    private void updateConfigs() {
        File configFile = new File(this.getDataFolder(), "config.yml");
        List<String> listConfig = Arrays.asList("normalShop.items", "rotatingShop.items");

        try {
            ConfigUpdater.update(this, "config.yml", configFile, listConfig);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.reloadConfig();

    }

    private void registerCommandsListeners() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new PlayerListener(this), this);
        pm.registerEvents(new PlayerInteract(this), this);
        pm.registerEvents(new EntityDeath(this), this);
        pm.registerEvents(new EntityDeathPhysical(this), this);
        pm.registerEvents(new CreatureSpawn(this), this);

        this.getCommand("mobcoins").setExecutor(new MainCommand(this));

    }

    public void initPlugin() {
        mobSpawner = new HashSet<>();
        tempDataConfig = new TempDataConfig(this);
        mobsConfig = new MobsConfig(this);
        shopConfig = new ShopConfig(this);
        itemsLoader = new ItemsLoader(this);
        shopUtils = new ShopUtils(this);
        utils = new Utils(this);
        coinMobManager = new CoinMobManager(this);
        salaryManager = new SalaryManager();
        dependencyManager = new DependencyManager(this);
        itemStockManager = new ItemStockManager(this);
        notificationManager = new ToggleNotificationManager(this);
        rotatingManager = new RotatingManager(this);
        categoryManager = new CategoryManager(this);
        accountManager = new AccountManager(this);
        limitManager = new PurchaseLimitManager(this);
    }

    private void createDatabaseFile() {
        File pluginFolder = new File("plugins/KrakenMobcoins");
        if (pluginFolder.mkdirs()) {
            getLogger().info("Plugin folder has been created");
        }
        File dataFolder = new File(MobCoins.getInstance().getDataFolder(), "dataFolder");
        if (dataFolder.mkdirs()) {
            getLogger().info("Data folder has been created");
        }

        File categoriesFolder = new File("plugins/KrakenMobcoins/categories");
        if (categoriesFolder.mkdirs()) {
            getLogger().info("Categories folder has been created");
        }
    }

    public Utils getUtils() {
        return utils;
    }

    public TempDataConfig getTempDataManager() {
        return tempDataConfig;
    }

    public ShopConfig getShopManager() {
        return shopConfig;
    }

    public Set<UUID> getMobSpawner() {
        return mobSpawner;
    }

    public ShopUtils getShopUtils() {
        return shopUtils;
    }

    public CoinMobManager getCoinMobManager() {
        return coinMobManager;
    }

    public SalaryManager getSalaryManager() {
        return salaryManager;
    }

    public DependencyManager getDependencyManager() {
        return dependencyManager;
    }

    public MobsConfig getMobsManager() {
        return mobsConfig;
    }

    public ItemStockManager getItemStockManager() {
        return itemStockManager;
    }

    public ToggleNotificationManager getNotificationManager() {
        return notificationManager;
    }

    public CategoryManager getCategoryManager() {
        return categoryManager;
    }

    public AccountManager getAccountManager() {
        return accountManager;
    }

    public ItemsLoader getItemsLoader() {
        return itemsLoader;
    }

    public RotatingManager getRotatingManager() {
        return rotatingManager;
    }

    public PurchaseLimitManager getPurchaseLimitManager() {
        return limitManager;
    }
}
