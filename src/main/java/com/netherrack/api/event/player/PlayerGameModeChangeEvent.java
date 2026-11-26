package com.netherrack.api.event.player;

import com.netherrack.api.entity.GameMode;
import com.netherrack.api.entity.Player;
import com.netherrack.api.event.Cancellable;

import java.util.Objects;

/** Fired before a player's game mode changes. Cancelling keeps the old mode. */
public final class PlayerGameModeChangeEvent extends PlayerEvent implements Cancellable {

    private final GameMode from;
    private final GameMode to;
    private boolean cancelled;

    public PlayerGameModeChangeEvent(Player player, GameMode from, GameMode to) {
        super(player);
        this.from = Objects.requireNonNull(from, "from");
        this.to = Objects.requireNonNull(to, "to");
    }

    public GameMode getFrom() {
        return from;
    }

    public GameMode getTo() {
        return to;
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
