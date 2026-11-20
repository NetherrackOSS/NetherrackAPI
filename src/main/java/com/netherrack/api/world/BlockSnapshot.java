package com.netherrack.api.world;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * An immutable copy of a block's name, state hash and properties, for handing blocks to plugins.
 * <p>
 * A snapshot rather than the server's live Block keeps plugins from holding an object whose state
 * changes under them, and keeps protocol classes out of the API. The state hash identifies an exact
 * vanilla state on the wire; the properties say what that state means ("facing" is "north", a
 * slab's "top_slot_bit" is false). Properties use the same lower-case names vanilla does, with
 * Boolean for bits, Integer for numbers and String for choices.
 */
public final class BlockSnapshot {

    private final String identifier;
    private final int stateHash;
    private final Map<String, Object> states;

    public BlockSnapshot(String identifier, int stateHash) {
        this(identifier, stateHash, Map.of());
    }

    public BlockSnapshot(String identifier, int stateHash, Map<String, ?> states) {
        this.identifier = Objects.requireNonNull(identifier, "identifier");
        this.stateHash = stateHash;
        Map<String, Object> copy = new LinkedHashMap<>();
        if (states != null) {
            for (Map.Entry<String, ?> entry : states.entrySet()) {
                String name = entry.getKey();
                Object value = entry.getValue();
                if (name == null || name.isBlank()) {
                    throw new IllegalArgumentException("block state name may not be blank");
                }
                if (!(value instanceof Boolean || value instanceof Integer || value instanceof String)) {
                    throw new IllegalArgumentException("unsupported value for block state " + name + ": " + value);
                }
                copy.put(name.toLowerCase(Locale.ROOT), value);
            }
        }
        this.states = Collections.unmodifiableMap(copy);
    }

    /** The Bedrock namespaced identifier, e.g. "minecraft:grass_block". */
    public String getIdentifier() {
        return identifier;
    }

    /** The hash of the exact vanilla state, as used by Bedrock's hashed block network ids. */
    public int getStateHash() {
        return stateHash;
    }

    /** This state's properties, or an empty map for a block that has none. */
    public Map<String, Object> getStates() {
        return states;
    }

    /** A property's value, or null if this state doesn't have it. Names are case-insensitive. */
    public Object getState(String name) {
        return name == null ? null : states.get(name.toLowerCase(Locale.ROOT));
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof BlockSnapshot snapshot)) {
            return false;
        }
        return stateHash == snapshot.stateHash && identifier.equals(snapshot.identifier)
                && states.equals(snapshot.states);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, stateHash, states);
    }

    @Override
    public String toString() {
        return states.isEmpty() ? identifier + "[" + stateHash + "]"
                : identifier + states + "[" + stateHash + "]";
    }
}
