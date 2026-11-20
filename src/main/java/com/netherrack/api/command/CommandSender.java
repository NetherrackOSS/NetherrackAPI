package com.netherrack.api.command;

import com.netherrack.api.Server;
import com.netherrack.api.entity.Player;
import com.netherrack.api.permission.Permissible;

/**
 * Whoever ran a command: a player or the console.
 * <p>
 * The concrete object is an adapter over the server's own sender. Replies go to the player's chat
 * or the console log, and permission checks use the same attachment model as {@code player.hasPermission}.
 */
public interface CommandSender extends Permissible {

    String getName();

    /** Whether this sender is a player rather than the console. */
    boolean isPlayer();

    /** The player who ran the command, or null for the console. */
    Player getPlayer();

    Server getServer();

    void sendMessage(String message);

    /** Sends each string as its own line. */
    default void sendMessage(String... messages) {
        for (String message : messages) {
            sendMessage(message);
        }
    }
}
