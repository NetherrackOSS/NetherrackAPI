package com.netherrack.api.event.server;

/**
 * Fired once the world and listener are ready and plugins have been enabled, at the end of
 * startup. Delivered on the main startup thread. Because listeners are usually registered in
 * onEnable(), firing after enableAll() is what lets those plugins hear it.
 */
public final class ServerStartEvent extends ServerEvent {
}
