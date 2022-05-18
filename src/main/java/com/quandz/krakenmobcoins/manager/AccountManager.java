package com.quandz.krakenmobcoins.manager;

import com.eatthepath.uuid.FastUUID;
import com.google.gson.Gson;
import com.quandz.krakenmobcoins.MobCoins;
import com.quandz.krakenmobcoins.object.PlayerCoins;
import com.quandz.krakenmobcoins.utils.ConfigUtils;
import com.quandz.krakenmobcoins.utils.Timing;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AccountManager {

    private final File dataFolder;
    private final ConcurrentHashMap<String, PlayerCoins> playerCoins;
    private final ConfigUtils configUtils = ConfigUtils.getInstance();
    private final MobCoins plugin;

    public AccountManager(final MobCoins plugin) {
        this.plugin = plugin;
        dataFolder = new File(MobCoins.getInstance().getDataFolder(), "dataFolder");
        playerCoins = new ConcurrentHashMap<>();
    }

    public Map<String, PlayerCoins> getPlayerCoins() {
        return playerCoins;
    }

    @Nullable
    public PlayerCoins getPlayerData(UUID uuid) {
        return playerCoins.get(FastUUID.toString(uuid));
    }

    public void load(Player player, boolean sendDebug) {
        Timing timing = null;
        BufferedReader reader = null;
        BufferedWriter writer = null;

        if (sendDebug) timing = new Timing();

        Gson gson = new Gson();
        String uuid = FastUUID.toString(player.getUniqueId());
        File file = new File(dataFolder, uuid + ".json");
        try {
            PlayerCoins coins;
            boolean newFile = file.createNewFile();
            if (newFile) {
                writer = new BufferedWriter(new FileWriter(file));
                coins = new PlayerCoins(uuid);
                coins.setMoney(configUtils.startingBalance);
                gson.toJson(coins, PlayerCoins.class, writer);
            } else {
                reader = new BufferedReader(new FileReader(file));
                coins = gson.fromJson(reader, PlayerCoins.class);
            }
            playerCoins.put(uuid, coins);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            cleanUP(reader, writer);
        }
        if (timing != null) plugin.getLogger().info("Loaded data "
                + player.getName() + " (" + timing.getTotalTime() + "ms)");
    }

    public void save(PlayerCoins coins, boolean sendDebug) {
        Timing timing = null;
        BufferedWriter bufferedWriter = null;

        if (sendDebug) timing = new Timing();

        Gson gson = new Gson();
        File file = new File(dataFolder, coins.getUUID() + ".json");
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(file));
            gson.toJson(coins, PlayerCoins.class, bufferedWriter);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            cleanUP(null, bufferedWriter);
        }
        if (timing != null) plugin.getLogger().info("Saved data "
                + coins.getPlayerName() + " (" + timing.getTotalTime() + "ms)");
    }

    public void saveAll() {
        Collection<PlayerCoins> players = getPlayerCoins().values();
        players.forEach((playerData) -> this.save(playerData, false));
        plugin.getLogger().info(players.size() + " players' data has been saved");
    }

    public void loadAll() {
        Bukkit.getOnlinePlayers().forEach((player) -> load(player, false));
        plugin.getLogger().info(Bukkit.getOnlinePlayers().size() + " players' data has been loaded");
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
}
