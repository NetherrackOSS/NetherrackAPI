package com.netherrack.api.event.player;

import com.netherrack.api.entity.Player;

/**
 * Fired after a player has been removed from the player list and saved. Delivered on the
 * disconnecting player's network thread.
 */
public final class PlayerQuitEvent extends PlayerEvent {

    public PlayerQuitEvent(Player player) {
        super(player);
    }
}
