package com.netherrack.api.metadata;

import java.util.Map;

/**
 * Something that can carry plugin-owned string values.
 * <p>
 * Metadata is deliberately untyped and unpersisted: it exists so chat, permission and minigame
 * plugins can share small values such as a prefix without agreeing on a database. Servers fire a
 * cancellable {@code PlayerMetadataChangeEvent} when a connected player's value changes.
 */
public interface MetadataHolder {

    /** The value for this key, or null when it is unset. Keys are case-insensitive. */
    String getMeta(String key);

    /**
     * Sets a value, or clears it when the value is null. Empty strings are kept so a plugin can
     * distinguish "blank" from "not set".
     */
    void setMeta(String key, String value);

    /** Clears a value, equivalent to {@code setMeta(key, null)}. */
    void removeMeta(String key);

    /** An immutable snapshot of the current values, keyed in lower case. */
    Map<String, String> getMeta();

    /** Convenience for the {@code prefix} key. */
    default String getPrefix() {
        return getMeta("prefix");
    }

    /** Convenience for the {@code prefix} key. */
    default void setPrefix(String value) {
        setMeta("prefix", value);
    }

    /** Convenience for the {@code suffix} key. */
    default String getSuffix() {
        return getMeta("suffix");
    }

    /** Convenience for the {@code suffix} key. */
    default void setSuffix(String value) {
        setMeta("suffix", value);
    }
}
