package com.netherrack.api.event.player;

import com.netherrack.api.entity.Player;
import com.netherrack.api.event.Cancellable;

/**
 * Fired after the login chain is checked and the Player exists, but before the client is told
 * login succeeded. Cancelling disconnects them with the kick message. Delivered on the connecting
 * player's network thread.
 */
public final class PlayerLoginEvent extends PlayerEvent implements Cancellable {

    private boolean cancelled;
    private String kickMessage = "You were kicked by a plugin.";

    public PlayerLoginEvent(Player player) {
        super(player);
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    public String getKickMessage() {
        return kickMessage;
    }

    public void setKickMessage(String kickMessage) {
        this.kickMessage = kickMessage == null ? "" : kickMessage;
    }
}
