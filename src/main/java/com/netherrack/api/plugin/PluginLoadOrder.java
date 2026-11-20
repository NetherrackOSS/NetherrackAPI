package com.netherrack.api.plugin;

/**
 * When a plugin's {@link Plugin#onLoad()} runs.
 * <p>
 * STARTUP plugins load before the world and network exist, for plugins that only need to
 * prepare their own state. POSTWORLD plugins load after those are ready, which is the default
 * because most plugins will want them. Either way, {@link Plugin#onEnable()} is only called
 * once the server is fully started.
 */
public enum PluginLoadOrder {
    STARTUP,
    POSTWORLD
}
