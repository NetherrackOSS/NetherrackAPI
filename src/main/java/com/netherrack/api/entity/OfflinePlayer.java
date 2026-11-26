package com.netherrack.api.entity;

import java.util.UUID;

/**
 * A player identity known to the server, whether or not they are connected.
 * <p>
 * Offline players are identities, not saved world data. Netherrack currently persists player
 * op status by XUID, so this is enough for plugins that need to look up an operator or remember a
 * name. It is not a store of inventories or locations.
 */
public interface OfflinePlayer {

    /** The last known player name, or a readable placeholder if none was ever seen. */
    String getName();

    /** The player's Java UUID, or null when only an XUID is known. */
    UUID getUuid();

    /** The Xbox User ID, or an empty string for offline/self-signed logins. */
    String getXuid();

    /** Whether this identity is connected right now. */
    boolean isOnline();

    /** The connected player, or null when they are offline. */
    Player getPlayer();
}
