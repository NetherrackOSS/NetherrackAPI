package com.netherrack.api.event;

/**
 * Handler order, from first to last.
 * <p>
 * MONITOR handlers run last and are expected only to observe the event. Earlier priorities may
 * change it; a MONITOR handler that cancels or rewrites it confuses every plugin after it.
 */
public enum EventPriority {
    LOWEST,
    LOW,
    NORMAL,
    HIGH,
    HIGHEST,
    MONITOR
}
