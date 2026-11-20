package com.netherrack.api.event.player;

import com.netherrack.api.entity.Player;
import com.netherrack.api.event.Cancellable;

/**
 * Fired for an ordinary chat message, before it is broadcast. Changing the message changes what
 * everyone sees; cancelling stops the broadcast entirely. Delivered on the speaking player's
 * network thread.
 */
public final class PlayerChatEvent extends PlayerEvent implements Cancellable {

    private boolean cancelled;
    private String message;

    public PlayerChatEvent(Player player, String message) {
        super(player);
        this.message = message;
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
