package com.netherrack.api.plugin;

/**
 * Thrown when a plugin.yml is missing a required field or can't be read.
 * <p>
 * The loader catches this per plugin: one bad plugin.yml stops that plugin from loading, not
 * the server from starting.
 */
public class PluginDescriptionException extends RuntimeException {

    public PluginDescriptionException(String message) {
        super(message);
    }

    public PluginDescriptionException(String message, Throwable cause) {
        super(message, cause);
    }
}
