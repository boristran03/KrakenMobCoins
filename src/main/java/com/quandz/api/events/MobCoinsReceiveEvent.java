package com.quandz.api.events;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MobCoinsReceiveEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private final Entity entity;
    private boolean isCancelled;
    private double amountBeforeMultiplier;
    private double amountAfterMultiplier;
    private double multiplierAmount;
    private int multiplier;

    public MobCoinsReceiveEvent(Player player, double amountBeforeMultiplier, double amountAfterMultiplier, double multiplierAmount, Entity entity, int multiplier) {
        this.player = player;
        this.amountBeforeMultiplier = amountBeforeMultiplier;
        this.amountAfterMultiplier = amountAfterMultiplier;
        this.multiplierAmount = multiplierAmount;
        this.entity = entity;
        this.isCancelled = false;
        this.multiplier = multiplier;
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

    public Entity getEntity() {
        return this.entity;
    }

    public int getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(int multiplier) {
        this.multiplier = multiplier;
    }

    public double getAmountBeforeMultiplier() {
        return amountBeforeMultiplier;
    }

    public void setAmountBeforeMultiplier(double amountBeforeMultiplier) {
        this.amountBeforeMultiplier = amountBeforeMultiplier;
    }

    public double getAmountAfterMultiplier() {
        return amountAfterMultiplier;
    }

    public void setAmountAfterMultiplier(double amountAfterMultiplier) {
        this.amountAfterMultiplier = amountAfterMultiplier;
    }

    public double getMultiplierAmount() {
        return multiplierAmount;
    }

    public void setMultiplierAmount(double multiplierAmount) {
        this.multiplierAmount = multiplierAmount;
    }

}
