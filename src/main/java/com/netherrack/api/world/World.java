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

    /** The block at these coordinates, or null for air. y is a block coordinate, not eye height. */
    BlockSnapshot getBlock(int x, int y, int z);

    /** Replaces a block. Passing null clears it to air. */
    void setBlock(int x, int y, int z, BlockSnapshot block);

    /** Convenience for callers holding a {@link BlockPosition}. */
    default BlockSnapshot getBlock(BlockPosition position) {
        return getBlock(position.getX(), position.getY(), position.getZ());
    }

    /** Convenience for callers holding a {@link BlockPosition}. */
    default void setBlock(BlockPosition position, BlockSnapshot block) {
        setBlock(position.getX(), position.getY(), position.getZ(), block);
    }

    void save();
}
