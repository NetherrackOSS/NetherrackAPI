package com.netherrack.api.permission;

import com.netherrack.api.plugin.Plugin;

import java.util.Locale;

/**
 * A named permission node a plugin registers with the server.
 * <p>
 * Registering a node gives it a default and a human-readable description. The server keeps the
 * existing op-level system intact, so a plugin can choose {@link PermissionDefault#OPERATOR} to
 * mean "as powerful as an operator" without knowing how command levels are stored.
 */
public final class Permission {

    private final String name;
    private final String description;
    private final PermissionDefault defaultState;
    private final Plugin plugin;
    private final String key;

    public Permission(String name) {
        this(name, "", PermissionDefault.UNDEFINED, null);
    }

    public Permission(String name, String description) {
        this(name, description, PermissionDefault.UNDEFINED, null);
    }

    public Permission(String name, String description, PermissionDefault defaultState) {
        this(name, description, defaultState, null);
    }

    public Permission(String name, String description, PermissionDefault defaultState, Plugin plugin) {
        if (name == null || !name.matches("[A-Za-z0-9_.*-]+")) {
            throw new IllegalArgumentException("permission node must contain only letters, digits, "
                    + "dots, dashes, underscores or a trailing wildcard: " + name);
        }
        this.name = name;
        this.description = description == null ? "" : description;
        this.defaultState = defaultState == null ? PermissionDefault.UNDEFINED : defaultState;
        this.plugin = plugin;
        this.key = name.toLowerCase(Locale.ROOT);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public PermissionDefault getDefault() {
        return defaultState;
    }

    /** The plugin that registered this node, or null for a node created ad hoc. */
    public Plugin getPlugin() {
        return plugin;
    }

    /** The lower-case form used for lookups, since permission nodes are case-insensitive. */
    public String getKey() {
        return key;
    }

    @Override
    public String toString() {
        return name;
    }
}
