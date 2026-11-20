package com.netherrack.api.world;

import java.util.Objects;

/**
 * An immutable copy of a block's name and state, for handing blocks to plugins.
 * <p>
 * A snapshot rather than the server's live Block keeps plugins from holding an object whose
 * state changes under them, and keeps protocol classes out of the API.
 */
public final class BlockSnapshot {

    private final String identifier;
    private final int stateHash;

    public BlockSnapshot(String identifier, int stateHash) {
        this.identifier = Objects.requireNonNull(identifier, "identifier");
        this.stateHash = stateHash;
    }

    public String getIdentifier() {
        return identifier;
    }

    public int getStateHash() {
        return stateHash;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof BlockSnapshot snapshot)) {
            return false;
        }
        return stateHash == snapshot.stateHash && identifier.equals(snapshot.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, stateHash);
    }

    @Override
    public String toString() {
        return identifier + "[" + stateHash + "]";
    }
}
