package com.netherrack.api.event.player;

import com.netherrack.api.entity.Player;
import com.netherrack.api.event.Cancellable;

/**
 * Fired before a connected player's metadata value changes. A null new value means the key is being
 * removed. Cancelling keeps the old value, and changing the new value changes what is stored.
 */
public final class PlayerMetadataChangeEvent extends PlayerEvent implements Cancellable {

    private final String key;
    private final String oldValue;
    private String newValue;
    private boolean cancelled;

    public PlayerMetadataChangeEvent(Player player, String key, String oldValue, String newValue) {
        super(player);
        this.key = key;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public String getKey() {
        return key;
    }

    public String getOldValue() {
        return oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    /** Sets the value to store; null removes the key. */
    public void setNewValue(String newValue) {
        this.newValue = newValue;
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
