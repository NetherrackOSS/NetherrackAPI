package com.netherrack.api.event.player;

import com.netherrack.api.entity.Player;

/**
 * Fired after a player has been added to the player list and shown to everyone. Delivered on the
 * joining player's network thread.
 */
public final class PlayerJoinEvent extends PlayerEvent {

    public PlayerJoinEvent(Player player) {
        super(player);
    }
}
