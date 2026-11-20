package com.netherrack.api.permission;

import com.netherrack.api.plugin.Plugin;

import java.util.Collection;

/**
 * The registry of known permission nodes.
 * <p>
 * A plugin registers the nodes it checks so the server knows their defaults. Permission managers
 * can also read that list to show every node available on the server. The registry is separate from
 * subjects: registering a node does not give it to anyone by itself.
 */
public interface PermissionManager {

    /** The node with this name, in any case, or null if it hasn't been registered. */
    Permission getPermission(String node);

    /** Every registered node, in registration order. */
    Collection<Permission> getPermissions();

    /** Registers or replaces a node. */
    void registerPermission(Permission permission);

    /** Forgets one node. Existing attachments to that name keep working as ad hoc rules. */
    void unregisterPermission(Permission permission);

    /** Forgets every node registered by this plugin, called on disable. */
    void unregisterPermissions(Plugin plugin);
}
