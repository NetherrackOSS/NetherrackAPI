package com.netherrack.api.permission;

import com.netherrack.api.plugin.Plugin;

/**
 * Something that can hold permissions: a player, the console, or a plugin's own test subject.
 * <p>
 * Checks are live, so an attachment takes effect as soon as it is changed. There is no snapshot of
 * effective permissions to refresh; {@link #recalculatePermissions()} exists so a subject with a
 * cache can clear it and so plugins have a single way to announce a batch of changes.
 */
public interface Permissible {

    /** Whether this subject has the node, after attachments, defaults and operator status. */
    default boolean hasPermission(String node) {
        return getPermissionValue(node) == PermissionState.ALLOW;
    }

    /** The resolved state of a node, including a possible explicit {@link PermissionState#DENY}. */
    PermissionState getPermissionValue(String node);

    /** Adds an empty attachment owned by this plugin. */
    PermissionAttachment addAttachment(Plugin plugin);

    /** Removes a previously added attachment. */
    void removeAttachment(PermissionAttachment attachment);

    /** Clears any cached result after a batch of attachment changes. */
    void recalculatePermissions();
}
