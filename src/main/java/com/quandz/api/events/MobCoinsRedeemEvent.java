package com.quandz.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class MobCoinsRedeemEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private final double amount;
    private final ItemStack item;
    private boolean isCancelled;

    public MobCoinsRedeemEvent(Player player, double amount, ItemStack item) {
        this.player = player;
        this.amount = amount;
        this.item = item;
        this.isCancelled = false;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public boolean isCancelled() {
        return this.isCancelled;
    }

    public void setCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public Player getPlayer() {
        return this.player;
    }

    /**
     * @return the amount of mobcoins player redeemed.
     */
    public double getAmount() {
        return this.amount;
    }

    /**
     * @return the mobcoins itemstack.
     */
    public ItemStack getItem() {
        return this.item;
    }

}
