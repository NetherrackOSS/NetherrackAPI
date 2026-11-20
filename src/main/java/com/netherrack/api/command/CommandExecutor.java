package com.netherrack.api.command;

/**
 * Handles a command declared in a plugin's plugin.yml.
 *
 * @return true if the command was understood; false shows its usage line to the sender
 */
@FunctionalInterface
public interface CommandExecutor {

    boolean onCommand(CommandSender sender, Command command, String label, String[] args);
}
