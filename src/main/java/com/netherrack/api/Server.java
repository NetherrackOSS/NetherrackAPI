package com.netherrack.api;

import com.netherrack.api.entity.Player;
import com.netherrack.api.permission.PermissionManager;
import com.netherrack.api.plugin.PluginLogger;
import com.netherrack.api.plugin.PluginManager;
import com.netherrack.api.scheduler.Scheduler;
import com.netherrack.api.world.World;

import java.util.Collection;

/**
 * The server a plugin runs on, as seen through the API.
 * <p>
 * This stays small on purpose. It gives a plugin its bearings, the plugin manager and the two
 * shared objects events refer to, without exposing the server's internal classes.
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

    /** The loaded world, or null before it has been opened. */
    World getWorld();

    /** The players currently registered on the server. */
    Collection<Player> getPlayers();

    /** The plugin task scheduler, whose tasks run on the server tick. */
    Scheduler getScheduler();

    /** The registry of permission nodes plugins have declared. */
    PermissionManager getPermissionManager();
}
