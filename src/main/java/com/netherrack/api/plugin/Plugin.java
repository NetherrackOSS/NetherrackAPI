package com.netherrack.api.plugin;

import com.netherrack.api.Server;

import java.nio.file.Path;

/**
 * A plugin loaded by the server.
 * <p>
 * Most plugins extend {@link PluginBase}, which supplies everything except the lifecycle
 * methods. Implementing this interface directly is possible when a plugin needs its own base
 * class, but the loader still expects the three callbacks to be called in load/enable/disable
 * order.
 */
public interface Plugin {

    /**
     * Called while the server is starting, before its world and network are ready. Use this for
     * state that stands on its own; touching players or blocks here is too early.
     */
    void onLoad();

    /** Called once the server is fully started. Register commands and other features here. */
    void onEnable();

    /** Called on shutdown, in reverse load order. Release resources and cancel work here. */
    void onDisable();

    /** Whether the server considers this plugin enabled. */
    boolean isEnabled();

    /** The plugin's name, from plugin.yml. */
    String getName();

    /** The parsed plugin.yml. */
    PluginDescription getDescription();

    /** The directory the plugin may write to, usually plugins/&lt;Name&gt;/. */
    Path getDataFolder();

    /** This plugin's config.yml, loaded on first use. */
    PluginConfig getConfig();

    /** Writes the current config back to config.yml. */
    void saveConfig();

    /** A logger prefixed with the plugin's name. */
    PluginLogger getLogger();

    /** The server this plugin is running on. */
    Server getServer();

    /** The manager that loaded this plugin. */
    PluginManager getPluginManager();

    /** The classloader that loaded the plugin's main class and jar. */
    ClassLoader getClassLoader();
}
