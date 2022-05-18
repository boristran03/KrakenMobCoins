package com.quandz.krakenmobcoins.listeners;

import com.quandz.api.events.MobCoinsRedeemEvent;
import com.quandz.krakenmobcoins.MobCoins;
import com.quandz.krakenmobcoins.enums.ConfigMessages;
import com.quandz.krakenmobcoins.object.PlayerCoins;
import com.quandz.krakenmobcoins.utils.ConfigUtils;
import com.quandz.krakenmobcoins.utils.Utils;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public record PlayerInteract(MobCoins plugin) implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Utils utils = plugin.getUtils();
        ConfigUtils config = ConfigUtils.getInstance();

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ItemStack hand = player.getInventory().getItemInMainHand();
            if (hand.getType() == Material.AIR) return;

            NBTItem nbtItem = new NBTItem(hand);
            if (!nbtItem.hasNBTData()) return;

            String info = nbtItem.getString("info");
            if (!info.equals("krakenmobcoins")) return;

            double amount = 0;
            if (hand.getAmount() == 1) {
                amount = nbtItem.getDouble("amount");
            } else if (hand.getAmount() > 1) {
                amount = nbtItem.getDouble("amount") * hand.getAmount();
            }

            event.setCancelled(true);
            PlayerCoins playerCoins = plugin.getAccountManager().getPlayerData(player.getUniqueId());
            if (playerCoins == null) {
                player.sendMessage(utils.color(ConfigMessages.NO_ACCOUNT.toString())
                        .replace("%prefix%", utils.getPrefix()));
                return;
            }

            MobCoinsRedeemEvent mobCoinsRedeemEvent = new MobCoinsRedeemEvent(player, amount, hand);
            Bukkit.getPluginManager().callEvent(mobCoinsRedeemEvent);
            if (mobCoinsRedeemEvent.isCancelled()) return;

            player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
            player.updateInventory();

            playerCoins.setMoney(playerCoins.getMoney() + mobCoinsRedeemEvent.getAmount());
            player.sendMessage(utils.color(ConfigMessages.REDEEM.toString())
                    .replace("%prefix%", utils.getPrefix())
                    .replace("%coins%", utils.getDecimalFormat().format(amount)));

            if (config.redeemSoundEnabled) {
                String name = config.redeemSoundName;
                float volume = (float) config.receivedMobCoinsSoundVolume;
                float pitch = (float) config.redeemSoundPitch;

                player.playSound(player.getLocation(), name, volume, pitch);

            }

        }


    }

}
