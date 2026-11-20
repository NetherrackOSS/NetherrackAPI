package com.netherrack.api.plugin;

import com.netherrack.api.event.Event;
import com.netherrack.api.event.Listener;

import java.util.Collection;

/**
 * The part of plugin management plugins can see.
 * <p>
 * The server owns the implementation and calls the lifecycle methods itself; a plugin looks
 * other plugins up here and registers its listeners. Event registration lives in the same
 * manager rather than a second one, so there is one place to ask.
 */
public interface PluginManager {

    /** The plugin with this name, or null if it isn't loaded. */
    Plugin getPlugin(String name);

    /** Every loaded plugin, including disabled ones. */
    Collection<Plugin> getPlugins();

    /** Whether a plugin with this name is loaded and enabled. */
    boolean isPluginEnabled(String name);

    /** Registers all @EventHandler methods on a listener. */
    void registerEvents(Listener listener, Plugin plugin);

    /** Removes a listener's handlers, for a plugin that is reloading one of its own listeners. */
    void unregisterEvents(Listener listener);

    /** Removes every handler a plugin registered. The server does this on disable. */
    void unregisterEvents(Plugin plugin);

    /** Delivers an event to its registered handlers. */
    void callEvent(Event event);
}
