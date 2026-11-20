package com.netherrack.api.command;

import com.netherrack.api.plugin.CommandDescription;
import com.netherrack.api.plugin.Plugin;

import java.util.List;

/**
 * A command built from a plugin.yml declaration.
 * <p>
 * The server creates one per declared command and registers it during the plugin's enable phase.
 * A plugin either overrides {@code onCommand} on PluginBase or calls {@link #setExecutor} to send
 * the command somewhere else.
 */
public final class PluginCommand implements Command {

    private final Plugin plugin;
    private final CommandDescription description;
    private CommandExecutor executor;

    public PluginCommand(Plugin plugin, CommandDescription description) {
        if (plugin == null || description == null) {
            throw new IllegalArgumentException("a plugin command needs both an owner and a declaration");
        }
        this.plugin = plugin;
        this.description = description;
        if (plugin instanceof CommandExecutor commandExecutor) {
            this.executor = commandExecutor;
        }
    }

    public CommandDescription getDeclaration() {
        return description;
    }

    /** Points this command at another handler, or at nothing when passed null. */
    public void setExecutor(CommandExecutor executor) {
        this.executor = executor;
    }

    public CommandExecutor getExecutor() {
        return executor;
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
    }

    @Override
    public String getName() {
        return description.getName();
    }

    @Override
    public List<String> getAliases() {
        return description.getAliases();
    }

    @Override
    public String getDescription() {
        return description.getDescription();
    }

    @Override
    public String getUsage() {
        return description.getUsage();
    }

    @Override
    public String getPermission() {
        return description.getPermission();
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        return executor != null && executor.onCommand(sender, this, label, args);
    }
}
