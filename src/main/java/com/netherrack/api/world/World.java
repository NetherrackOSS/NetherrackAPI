package com.netherrack.api.world;

/**
 * A world as plugins see it.
 * <p>
 * Only the pieces events need are here for now: identity, spawn, and reading or replacing a
 * single block. Broader world editing can be added without changing the events that use this.
 */
public interface World {

    String getName();

    long getSeed();

    BlockPosition getSpawn();

    /** The block at these coordinates, or null for air. */
    BlockSnapshot getBlock(int x, int y, int z);

    /** Replaces a block. Passing null clears it to air. */
    void setBlock(int x, int y, int z, BlockSnapshot block);

    void save();
}
