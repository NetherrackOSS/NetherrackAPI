package com.netherrack.api.event.player;

import com.netherrack.api.entity.Player;
import com.netherrack.api.event.Cancellable;

/**
 * Fired just before a popup message is sent to one player.
 * <p>
 * Handlers may rewrite the message or cancel the send entirely. Delivered on the receiving
 * player's network thread.
 */
public final class PlayerPopupEvent extends PlayerEvent implements Cancellable {

    private boolean cancelled;
    private String message;

    public PlayerPopupEvent(Player player, String message) {
        super(player);
        this.message = message == null ? "" : message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message == null ? "" : message;
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
