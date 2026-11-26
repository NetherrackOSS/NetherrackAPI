package com.netherrack.api.event.player;

import com.netherrack.api.entity.DamageCause;
import com.netherrack.api.entity.Player;
import com.netherrack.api.event.Cancellable;

import java.util.Objects;

/**
 * Fired before a player loses health. Cancelling prevents the damage entirely, and changing the
 * amount scales it. Delivered on the player's network thread or the server tick, whichever owns
 * the damage source.
 */
public final class PlayerDamageEvent extends PlayerEvent implements Cancellable {

    private final DamageCause cause;
    private final double originalAmount;
    private double amount;
    private boolean cancelled;

    public PlayerDamageEvent(Player player, DamageCause cause, double amount) {
        super(player);
        this.cause = Objects.requireNonNull(cause, "cause");
        this.originalAmount = Math.max(0, amount);
        this.amount = this.originalAmount;
    }

    public DamageCause getCause() {
        return cause;
    }

    /** The amount the server originally intended to apply. */
    public double getOriginalAmount() {
        return originalAmount;
    }

    /** The amount that will be applied after listeners have had their say. */
    public double getAmount() {
        return amount;
    }

    /** Sets the damage to apply; negative values are treated as zero. */
    public void setAmount(double amount) {
        this.amount = Math.max(0, amount);
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }
}
