package com.netherrack.api.plugin;

import com.netherrack.api.Server;
import com.netherrack.api.command.Command;
import com.netherrack.api.command.CommandExecutor;
import com.netherrack.api.command.CommandSender;
import com.netherrack.api.permission.PermissionManager;
import com.netherrack.api.scheduler.Scheduler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Base class for plugins, supplying everything except the lifecycle callbacks.
 * <p>
 * The loader calls {@link #initialize(...)} once, before any callback, with the objects the
 * plugin will need. Everything is held here rather than passed to callbacks so a plugin's main
 * class has a plain constructor and can be instantiated reflectively. The three lifecycle
 * methods do nothing by default, so a plugin only overrides the ones it cares about.
 */
public abstract class PluginBase implements Plugin, CommandExecutor {

    private PluginManager pluginManager;
    private Server server;
    private PluginDescription description;
    private Path dataFolder;
    private PluginLogger logger;
    private ClassLoader classLoader;
    private PluginConfig config;
    private boolean enabled;

    /**
     * Called by the plugin loader before onLoad(). Plugins should never call or override this.
     */
    public final void initialize(PluginManager pluginManager, Server server,
                                 PluginDescription description, Path dataFolder,
                                 PluginLogger logger, ClassLoader classLoader) {
        this.pluginManager = pluginManager;
        this.server = server;
        this.description = description;
        this.dataFolder = dataFolder;
        this.logger = logger;
        this.classLoader = classLoader;
        try {
            Files.createDirectories(dataFolder);
        } catch (IOException e) {
            if (logger != null) {
                logger.error("Could not create plugin data folder " + dataFolder + ": " + e.getMessage());
            }
        }
        this.config = new PluginConfig(dataFolder.resolve("config.yml"));
        this.config.load();
    }

    /** Called by the loader to keep {@link #isEnabled()} in step with the plugin's state. */
    public final void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void onLoad() {
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
    }

    /**
     * Handles a command declared by this plugin. Returning false shows its usage line. Plugins that
     * prefer a separate handler can call {@code getCommand(name).setExecutor(...)} instead.
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public String getName() {
        return description.getName();
    }

    @Override
    public PluginDescription getDescription() {
        return description;
    }

    @Override
    public Path getDataFolder() {
        return dataFolder;
    }

    @Override
    public PluginConfig getConfig() {
        return config;
    }

    @Override
    public void saveConfig() {
        try {
            config.save();
        } catch (IOException e) {
            logger.error("Could not save " + config.getFile() + ": " + e.getMessage());
        }
    }

    @Override
    public PluginLogger getLogger() {
        return logger;
    }

    @Override
    public Server getServer() {
        return server;
    }

    @Override
    public PluginManager getPluginManager() {
        return pluginManager;
    }

    @Override
    public ClassLoader getClassLoader() {
        return classLoader;
    }

    /** A declared command this plugin can configure, or null if it declared none with that name. */
    public com.netherrack.api.command.PluginCommand getCommand(String name) {
        return pluginManager == null ? null : pluginManager.getCommand(name, this);
    }

    /** Convenience for {@code getServer().getScheduler()}. */
    public Scheduler getScheduler() {
        return server == null ? null : server.getScheduler();
    }

    /** Convenience for {@code getServer().getPermissionManager()}. */
    public PermissionManager getPermissionManager() {
        return server == null ? null : server.getPermissionManager();
    }
}
