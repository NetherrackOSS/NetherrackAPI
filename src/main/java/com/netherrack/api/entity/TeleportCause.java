package com.netherrack.api.entity;

/** Why a player was moved in one step instead of walking. */
public enum TeleportCause {

    /** A console or player command. */
    COMMAND,

    /** A plugin called {@link Player#teleport(com.netherrack.api.world.Location)} directly. */
    PLUGIN,

    /** Respawning after death or changing worlds. */
    RESPAWN,

    /** A portal, end gateway or similar vanilla mechanism. */
    PORTAL,

    /** A source without a closer match. */
    UNKNOWN
}
