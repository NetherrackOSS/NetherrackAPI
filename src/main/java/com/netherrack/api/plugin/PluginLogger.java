package com.netherrack.api.plugin;

/**
 * A plugin's console logger.
 * <p>
 * The loader gives each plugin its own logger, already prefixed with the plugin name, so a
 * plugin never has to guess how the server formats its lines. The four levels map onto the
 * server's own logger; debug output follows the server's logging settings.
 */
public interface PluginLogger {

    void info(String message);

    void warn(String message);

    void error(String message);

    void debug(String message);
}
