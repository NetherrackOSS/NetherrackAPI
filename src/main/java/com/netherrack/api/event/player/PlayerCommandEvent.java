package com.netherrack.api.event.player;

import com.netherrack.api.entity.Player;
import com.netherrack.api.event.Cancellable;

/**
 * Fired before a command a player typed is dispatched. The command has no leading slash; changing
 * it changes what the server runs, and cancelling stops the command altogether. Delivered on the
 * commanding player's network thread.
 */
public final class PlayerCommandEvent extends PlayerEvent implements Cancellable {

    private boolean cancelled;
    private String command;

    public PlayerCommandEvent(Player player, String command) {
        super(player);
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command == null ? "" : command;
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
