package me.aglerr.krakenmobcoins;

import fr.mrmicky.fastinv.FastInvManager;
import me.aglerr.krakenmobcoins.api.MobCoinsAPI;
import me.aglerr.krakenmobcoins.coinmob.CoinMobManager;
import me.aglerr.krakenmobcoins.commands.MainCommand;
import me.aglerr.krakenmobcoins.configs.MobsConfig;
import me.aglerr.krakenmobcoins.configs.ShopConfig;
import me.aglerr.krakenmobcoins.configs.TempDataConfig;
import me.aglerr.krakenmobcoins.enums.ConfigMessages;
import me.aglerr.krakenmobcoins.enums.ConfigMessagesList;
import me.aglerr.krakenmobcoins.listeners.*;
import me.aglerr.krakenmobcoins.manager.*;
import me.aglerr.krakenmobcoins.shops.ShopUtils;
import me.aglerr.krakenmobcoins.shops.loader.ItemsLoader;
import me.aglerr.krakenmobcoins.utils.ConfigUpdater;
import me.aglerr.krakenmobcoins.utils.ConfigUtils;
import me.aglerr.krakenmobcoins.utils.Metrics;
import me.aglerr.krakenmobcoins.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class MobCoins extends JavaPlugin {

    /**
     * Version 2.1
     * Main goal is to remove singletons usage and switching it
     * into dependency injection and also make the code more object oriented
     * <p>
     * TODO: LastRewardManager.class
     * TODO: Fixing database bugs
     */

    private static MobCoins __instance;

    private Set<UUID> mobSpawner;

    private List<EntityDamageEvent.DamageCause> damageCauses;

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

    private static MobCoinsAPI api;

    public static MobCoins getInstance() {
        return __instance;
    }

    @Override
    public void onEnable() {
        __instance = this;
        createDatabaseFile();
        init();
        File categoriesFolder = new File("plugins/KrakenMobcoins/categories");
        if (!categoriesFolder.exists()) {
            categoriesFolder.mkdirs();
        }

        registerConfigs();
        updateConfigs();

        new ConfigUtils().loadConfig();

        register();
        registerCommandsListeners();

        api = new MobCoinsAPI(this);

        getLogger().info("  __  __  ____  ____   _____ ____ _____ _   _  _____ \n" +
                " |  \\/  |/ __ \\|  _ \\ / ____/ __ \\_   _| \\ | |/ ____|\n" +
                " | \\  / | |  | | |_) | |   | |  | || | |  \\| | (___  \n" +
                " | |\\/| | |  | |  _ <| |   | |  | || | | . ` |\\___ \\ \n" +
                " | |  | | |__| | |_) | |___| |__| || |_| |\\  |____) |\n" +
                " |_|  |_|\\____/|____/ \\_____\\____/_____|_| \\_|_____/ \n" +
                "                                                     ");
        getLogger().info("Plugin đã được recode bởi quanphungg_");
        Bukkit.getOnlinePlayers().forEach((player) -> getAccountManager().loadPlayerData(player));
    }

    @Override
    public void onDisable(){
        notificationManager.saveToggledListToConfig();
        rotatingManager.saveNormalAndSpecialTime();
        accountManager.saveAllPlayerData();
        itemStockManager.saveStockToConfig();
        rotatingManager.saveRewards();
        limitManager.saveLimit();
    }

    private void register(){

        this.loadShop();

        notificationManager.loadToggledListFromConfig();
        coinMobManager.loadCoinMob();
        itemStockManager.loadStockFromConfig();
        limitManager.loadLimit();
        dependencyManager.setupDependency();

        this.loadDamageCausePhysical();

        ConfigMessages.initialize(this.getConfig());
        ConfigMessagesList.initialize(this.getConfig());

        new Metrics(this, 10310);
        FastInvManager.register(this);

        Bukkit.getScheduler().runTask(this, salaryManager::beginSalaryTask);
        Bukkit.getScheduler().runTask(this, accountManager::startAutoSaveTask);

    }

    private void loadShop(){
        if(this.getConfig().getBoolean("rotatingShop.enabled")){

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

    private void registerConfigs(){
        this.saveDefaultConfig();
        this.getConfig().options().copyDefaults(true);

        tempDataConfig.setup();
        mobsConfig.setup();
        shopConfig.setup();
    }

    public void reloadConfigs(){
        this.reloadConfig();
        mobsConfig.reloadData();
        shopConfig.reloadData();

        rotatingManager.saveRewards();
        itemsLoader.clearAllItemsList();
        categoryManager.clearCategory();
        rotatingManager.clearNormalAndSpecialItems();
        damageCauses.clear();
        coinMobManager.clearCoinMob();

        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {

            itemsLoader.loadAllItemsList();

            rotatingManager.loadRewards();
            categoryManager.loadCategory();
            coinMobManager.loadCoinMob();
            loadDamageCausePhysical();

            ConfigMessages.initialize(this.getConfig());
            ConfigMessagesList.initialize(this.getConfig());

        }, 3L);

    }

    private void updateConfigs(){
        File configFile = new File(this.getDataFolder(), "config.yml");
        List<String> listConfig = Arrays.asList("normalShop.items", "rotatingShop.items");

        try {
            ConfigUpdater.update(this, "config.yml", configFile, listConfig);
        }catch(IOException e){
            e.printStackTrace();
        }

        this.reloadConfig();

    }

    private void registerCommandsListeners(){
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new PlayerListener(this), this);
        pm.registerEvents(new PlayerInteract(this), this);
        pm.registerEvents(new EntityDeath(this), this);
        pm.registerEvents(new EntityDeathPhysical(this), this);
        pm.registerEvents(new CreatureSpawn(this), this);
        if(dependencyManager.isMythicMobs()){
            pm.registerEvents(new MythicMobDeath(this), this);
            pm.registerEvents(new MythicMobDeathPhysical(this), this);
        }

        this.getCommand("mobcoins").setExecutor(new MainCommand(this));

    }

    private void loadDamageCausePhysical() {
        for (String string : this.getConfig().getStringList("options.physicalMobCoin.deathCause")) {
            try {
                damageCauses.add(EntityDamageEvent.DamageCause.valueOf(string));
            } catch (IllegalArgumentException exception) {
                utils.sendConsoleMessage("Damage Cause with name '{string}' is invalid!".replace("{string}", string));
                exception.printStackTrace();
            }
        }
    }

    public void init() {
        mobSpawner = new HashSet<>();
        damageCauses = new ArrayList<>();
        tempDataConfig = new TempDataConfig(this);
        mobsConfig = new MobsConfig(this);
        shopConfig = new ShopConfig(this);
        itemsLoader = new ItemsLoader(this);
        shopUtils = new ShopUtils(this);
        utils = new Utils(this);
        coinMobManager = new CoinMobManager(this);
        salaryManager = new SalaryManager(this);
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
        if (!pluginFolder.exists()) {
            pluginFolder.mkdirs();
        }
        File dataFolder = new File(MobCoins.getInstance().getDataFolder(), "dataFolder");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
    }

    public static MobCoinsAPI getAPI() { return api; }
    public Utils getUtils() { return utils; }
    public TempDataConfig getTempDataManager() { return tempDataConfig; }
    public ShopConfig getShopManager() { return shopConfig; }
    public Set<UUID> getMobSpawner() { return mobSpawner; }
    public ShopUtils getShopUtils() { return shopUtils; }
    public List<EntityDamageEvent.DamageCause> getDamageCauses() { return damageCauses; }
    public CoinMobManager getCoinMobManager() { return coinMobManager; }
    public SalaryManager getSalaryManager() { return salaryManager; }
    public DependencyManager getDependencyManager() { return dependencyManager; }
    public MobsConfig getMobsManager() { return mobsConfig; }
    public ItemStockManager getItemStockManager() { return itemStockManager; }
    public ToggleNotificationManager getNotificationManager() { return notificationManager; }
    public CategoryManager getCategoryManager() { return categoryManager; }
    public AccountManager getAccountManager() { return accountManager; }
    public ItemsLoader getItemsLoader() { return itemsLoader; }
    public RotatingManager getRotatingManager() { return rotatingManager; }
    public PurchaseLimitManager getPurchaseLimitManager() { return limitManager; }
}
