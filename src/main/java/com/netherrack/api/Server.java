package com.netherrack.api;

import com.netherrack.api.plugin.PluginLogger;
import com.netherrack.api.plugin.PluginManager;

/**
 * The server a plugin runs on, as seen through the API.
 * <p>
 * This is deliberately small. It gives a plugin its bearings and access to the plugin manager
 * without exposing the server's internal classes; world, player and scheduler access will be
 * added here as those parts of the API are built.
 */
public interface Server {

    /** The server's name, "Netherrack". */
    String getName();

    /** The server's version, such as "0.4.0-beta". */
    String getVersion();

    /** The manager that loaded this plugin. */
    PluginManager getPluginManager();

    /** A logger that prefixes lines with the server's own name. */
    PluginLogger getLogger();
}
