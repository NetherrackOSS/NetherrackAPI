package com.netherrack.api.plugin;

import java.util.List;

/**
 * One command declared in the {@code commands:} block of a plugin.yml.
 * <pre>
 * commands:
 *   hello:
 *     description: Says hello
 *     aliases: [hi]
 *     usage: /hello [name]
 *     permission: hello.use
 * </pre>
 * A command may also be written as {@code hello: Says hello} when only a description is needed.
 */
public final class CommandDescription {

    private final String name;
    private final String description;
    private final List<String> aliases;
    private final String usage;
    private final String permission;

    public CommandDescription(String name, String description, List<String> aliases,
                              String usage, String permission) {
        if (name == null || !name.matches("[A-Za-z0-9._-]+")) {
            throw new PluginDescriptionException("command name \"" + name + "\" may only contain "
                    + "letters, digits, dots, dashes and underscores");
        }
        this.name = name;
        this.description = description == null ? "" : description;
        this.aliases = aliases == null ? List.of() : List.copyOf(aliases);
        this.usage = usage == null || usage.isBlank() ? "/" + name : usage;
        this.permission = permission == null ? "" : permission;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public String getUsage() {
        return usage;
    }

    /** The required node, or an empty string when the command needs no explicit permission. */
    public String getPermission() {
        return permission;
    }
}
