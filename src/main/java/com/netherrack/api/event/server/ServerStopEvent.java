package com.netherrack.api.event.server;

/**
 * Fired when a graceful shutdown begins, before players are disconnected and plugins are
 * disabled. Delivered on the dedicated shutdown thread, which is not the server tick thread.
 */
public final class ServerStopEvent extends ServerEvent {
}
