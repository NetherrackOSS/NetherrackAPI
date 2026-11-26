package com.netherrack.api.event.player;

import com.netherrack.api.entity.Player;
import com.netherrack.api.world.Location;

import java.util.Objects;

/** Fired before a dead player is placed at a respawn point. Changing the location moves them. */
public final class PlayerRespawnEvent extends PlayerEvent {

    private Location respawnLocation;

    public PlayerRespawnEvent(Player player, Location respawnLocation) {
        super(player);
        setRespawnLocation(respawnLocation);
    }

    public Location getRespawnLocation() {
        return respawnLocation;
    }

    public void setRespawnLocation(Location respawnLocation) {
        this.respawnLocation = Objects.requireNonNull(respawnLocation, "respawnLocation");
    }
}
