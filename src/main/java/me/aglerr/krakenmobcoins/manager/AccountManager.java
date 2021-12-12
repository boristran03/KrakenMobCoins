package me.aglerr.krakenmobcoins.manager;

import com.google.gson.Gson;
import me.aglerr.krakenmobcoins.MobCoins;
import me.aglerr.krakenmobcoins.database.PlayerCoins;
import me.aglerr.krakenmobcoins.utils.ConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class AccountManager {

    private final File dataFolder;
    private final ConcurrentHashMap<String, PlayerCoins> playerCoins = new ConcurrentHashMap<>();
    private final ConfigUtils configUtils = ConfigUtils.getInstance();

    private final MobCoins plugin;

    public AccountManager(final MobCoins plugin) {
        this.plugin = plugin;
        dataFolder = new File(MobCoins.getInstance().getDataFolder(), "dataFolder");
    }

    public Map<String, PlayerCoins> getPlayerCoins() {
        return playerCoins;
    }

    @Nullable
    public PlayerCoins getPlayerData(String uuid) {
        return playerCoins.get(uuid);
    }

//    public void createPlayerData(@NotNull String uuid, double coinAmount) {
//        PlayerCoins coins = new PlayerCoins(uuid);
//        coins.setMoney(coinAmount);
//
//        CompletableFuture.runAsync(() -> savePlayerData(coins)).thenAccept(result ->
//                Bukkit.getScheduler().runTask(plugin, () -> playerCoins.put(uuid, coins)));
//
//    }

    public void loadPlayerData(Player player) {
        long start = System.currentTimeMillis();
        String uuid = player.getUniqueId().toString();
        File file = new File(dataFolder, uuid + ".json");
        BufferedReader reader = null;
        BufferedWriter writer = null;
        try {
            Gson gson = new Gson();
            boolean newFile = file.createNewFile();
            reader = new BufferedReader(new FileReader(file));
            writer = new BufferedWriter(new FileWriter(file));
            PlayerCoins coins;
            if (newFile) {
                coins = new PlayerCoins(uuid);
                gson.toJson(coins, PlayerCoins.class, writer);
            } else {
                coins = gson.fromJson(reader, PlayerCoins.class);
            }
            playerCoins.put(uuid, coins);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            cleanUP(reader, writer);
            long end = System.currentTimeMillis();
            long latency = end - start;
            plugin.getLogger().log(Level.WARNING, "Loaded data "
                    + player.getName() + " (" + latency + "ms)");
        }
    }

    public void savePlayerData(PlayerCoins coins) {
        long start = System.currentTimeMillis();
        Gson gson = new Gson();
        BufferedWriter bufferedWriter = null;
        File file = new File(dataFolder, coins.getUUID() + ".json");
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(file));
            gson.toJson(coins, PlayerCoins.class, bufferedWriter);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            cleanUP(null, bufferedWriter);
        }
        long end = System.currentTimeMillis();
        long latency = end - start;
        plugin.getLogger().log(Level.WARNING, "Saved data "
                + coins.getPlayerName() + " (" + latency + "ms)");
    }

    public void saveAllPlayerData() {
        plugin.getAccountManager().getPlayerCoins().values().forEach(this::savePlayerData);
    }

    public void cleanUP(Reader reader, Writer writer) {
        try {
            if (reader != null) {
                reader.close();
            }
            if (writer != null) {
                writer.flush();
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public List<PlayerCoins> getTop() {
//
//        List<PlayerCoins> coins = new ArrayList<>();
//        for (String a : playerCoins.keySet()) {
//            coins.add(getPlayerData(a));
//        }
//
//        List<PlayerCoins> convert = new ArrayList<>(coins);
//
//        convert.sort((pt1, pt2) -> {
//
//            Float f1 = (float) pt1.getMoney();
//            Float f2 = (float) pt2.getMoney();
//
//            return f2.compareTo(f1);
//
//        });
//        if (convert.size() > 10) {
//            convert = convert.subList(0, 10);
//        }
//        return convert;
//
//    }

    public void startAutoSaveTask() {
        if (configUtils.enabledAutoSave) {
            int interval = configUtils.autoSaveTaskTimer;
            Bukkit.getScheduler().runTaskTimerAsynchronously(plugin,
                    this::saveAllPlayerData, 0L, 20 * interval);
        }
    }

}
