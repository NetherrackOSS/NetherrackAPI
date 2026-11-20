package com.netherrack.api.permission;

import com.netherrack.api.plugin.Plugin;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * A plugin-owned set of permission changes for one subject.
 * <p>
 * Attachments are how a permission manager plugin changes what another plugin sees without editing
 * registrations. Later attachments win over earlier ones for the same node; unsetting a node removes
 * the attachment's rule so lower-priority defaults apply again.
 */
public final class PermissionAttachment {

    private final Permissible permissible;
    private final Plugin plugin;
    private final Map<String, PermissionState> values = new LinkedHashMap<>();
    private boolean removed;

    public PermissionAttachment(Permissible permissible, Plugin plugin) {
        if (permissible == null) {
            throw new IllegalArgumentException("attachment needs a permissible");
        }
        this.permissible = permissible;
        this.plugin = plugin;
    }

    /** Allows or denies a node for the subject this attachment belongs to. */
    public void setPermission(String node, boolean allowed) {
        checkNotRemoved();
        values.put(key(node), allowed ? PermissionState.ALLOW : PermissionState.DENY);
        permissible.recalculatePermissions();
    }

    /** Removes this attachment's rule for a node, leaving any default in place. */
    public void unsetPermission(String node) {
        checkNotRemoved();
        values.remove(key(node));
        permissible.recalculatePermissions();
    }

    /** Removes this attachment from its subject. Calling it twice does nothing. */
    public void remove() {
        if (removed) {
            return;
        }
        removed = true;
        values.clear();
        permissible.removeAttachment(this);
        permissible.recalculatePermissions();
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public Permissible getPermissible() {
        return permissible;
    }

    public boolean isRemoved() {
        return removed;
    }

    /** An immutable view of the nodes this attachment currently sets. */
    public Map<String, PermissionState> getPermissions() {
        return Collections.unmodifiableMap(new LinkedHashMap<>(values));
    }

    private void checkNotRemoved() {
        if (removed) {
            throw new IllegalStateException("permission attachment has been removed");
        }
    }

    private static String key(String node) {
        if (node == null || node.isBlank()) {
            throw new IllegalArgumentException("permission node may not be blank");
        }
        return node.toLowerCase(Locale.ROOT);
    }
}
