package com.netherrack.api.event.player;

import com.netherrack.api.entity.Player;
import com.netherrack.api.entity.TeleportCause;
import com.netherrack.api.world.Location;

import java.util.Objects;

/**
 * Fired before a teleport is applied. It extends {@link PlayerMoveEvent} so listeners interested in
 * every position change can use one handler and still tell teleports apart by type.
 */
public final class PlayerTeleportEvent extends PlayerMoveEvent {

    private final TeleportCause cause;

    public PlayerTeleportEvent(Player player, Location from, Location to, TeleportCause cause) {
        super(player, from, to);
        this.cause = Objects.requireNonNull(cause, "cause");
    }

    public TeleportCause getCause() {
        return cause;
    }
}
