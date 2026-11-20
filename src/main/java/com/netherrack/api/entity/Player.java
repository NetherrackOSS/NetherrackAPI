package com.netherrack.api.entity;

import com.netherrack.api.world.World;

import java.util.UUID;

/**
 * A connected player as plugins see them.
 * <p>
 * This is deliberately narrow: enough for the first events to identify who acted and answer
 * them. More of the player will be added as events need it.
 */
public interface Player {

    String getName();

    UUID getUuid();

    /** The Xbox User ID, or an empty string for offline/self-signed logins. */
    String getXuid();

    World getWorld();

    void sendMessage(String message);

    void kick(String reason);
}
