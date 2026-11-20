package com.netherrack.api.plugin;

import java.util.Collection;

/**
 * The part of plugin management plugins can see.
 * <p>
 * The server owns the implementation and calls the lifecycle methods itself; a plugin only
 * needs to look other plugins up. Event registration, commands and permissions will be added
 * here in later phases rather than in a separate manager, so there is one place to ask.
 */
public interface PluginManager {

    /** The plugin with this name, or null if it isn't loaded. */
    Plugin getPlugin(String name);

    /** Every loaded plugin, including disabled ones. */
    Collection<Plugin> getPlugins();

    /** Whether a plugin with this name is loaded and enabled. */
    boolean isPluginEnabled(String name);
}
