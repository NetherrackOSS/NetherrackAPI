package com.netherrack.api.command;

import com.netherrack.api.plugin.Plugin;

import java.util.List;

/**
 * A command plugins can declare in plugin.yml and handle themselves.
 * <p>
 * The server adapts this to its own command registry: aliases become suggestions, the permission
 * node is checked before execution, and the usage line is shown when the executor returns false.
 */
public interface Command {

    String getName();

    default List<String> getAliases() {
        return List.of();
    }

    /** The description shown in help and command suggestions. */
    String getDescription();

    /** A human-readable usage line, such as "/hello [name]". */
    default String getUsage() {
        return "/" + getName();
    }

    /** The permission node required to run this command, or null for everyone. */
    default String getPermission() {
        return null;
    }

    /** The plugin that declared this command. */
    Plugin getPlugin();

    boolean execute(CommandSender sender, String label, String[] args);
}
