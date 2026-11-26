package com.netherrack.api.event.player;

import com.netherrack.api.entity.Player;
import com.netherrack.api.event.Cancellable;
import com.netherrack.api.world.Location;

import java.util.Objects;

/**
 * Fired when a player's reported position changes. Cancelling restores the previous location, and
 * {@link #setTo(Location)} replaces the destination. Delivered on the player's network thread.
 */
public class PlayerMoveEvent extends PlayerEvent implements Cancellable {

    private final Location from;
    private Location to;
    private boolean cancelled;

    public PlayerMoveEvent(Player player, Location from, Location to) {
        super(player);
        this.from = Objects.requireNonNull(from, "from");
        setTo(to);
    }

    public Location getFrom() {
        return from;
    }

    public Location getTo() {
        return to;
    }

    public void setTo(Location to) {
        this.to = Objects.requireNonNull(to, "to");
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
