package com.netherrack.api.event.player;

import com.netherrack.api.entity.Player;
import com.netherrack.api.event.Cancellable;

/**
 * Fired just before an action bar message is sent to one player.
 * <p>
 * Handlers may rewrite the message or its animation times, or cancel the send entirely. The
 * defaults match Nukkit's {@code sendActionBar(String)}: one tick in, no stay and one tick out.
 * Delivered on the receiving player's network thread.
 */
public final class PlayerActionBarEvent extends PlayerEvent implements Cancellable {

    private boolean cancelled;
    private String message;
    private int fadeInTime;
    private int stayTime;
    private int fadeOutTime;

    public PlayerActionBarEvent(Player player, String message, int fadeInTime, int stayTime,
                                int fadeOutTime) {
        super(player);
        this.message = message == null ? "" : message;
        this.fadeInTime = fadeInTime;
        this.stayTime = stayTime;
        this.fadeOutTime = fadeOutTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message == null ? "" : message;
    }

    public int getFadeInTime() {
        return fadeInTime;
    }

    public void setFadeInTime(int fadeInTime) {
        this.fadeInTime = fadeInTime;
    }

    public int getStayTime() {
        return stayTime;
    }

    public void setStayTime(int stayTime) {
        this.stayTime = stayTime;
    }

    public int getFadeOutTime() {
        return fadeOutTime;
    }

    public void setFadeOutTime(int fadeOutTime) {
        this.fadeOutTime = fadeOutTime;
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
