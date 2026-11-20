package com.netherrack.api.entity;

/**
 * The game modes a player can be in.
 * <p>
 * The ids are Bedrock's, so they can be compared with values read from a saved world without
 * guessing: survival is 0, creative 1, adventure 2 and spectator 6.
 */
public enum GameMode {

    SURVIVAL(0),
    CREATIVE(1),
    ADVENTURE(2),
    SPECTATOR(6);

    private final int id;

    GameMode(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    /** The mode with this Bedrock id, or null when the id is unknown. */
    public static GameMode fromId(int id) {
        for (GameMode mode : values()) {
            if (mode.id == id) {
                return mode;
            }
        }
        return null;
    }
}
